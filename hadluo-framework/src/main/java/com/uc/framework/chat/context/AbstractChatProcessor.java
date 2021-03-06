package com.uc.framework.chat.context;

import java.util.LinkedList;
import java.util.List;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.uc.framework.chat.Chat;
import com.uc.framework.chat.ChatConfigure;
import com.uc.framework.chat.ChatGroup;
import com.uc.framework.chat.ChatRequest;
import com.uc.framework.chat.ErrorFuture;
import com.uc.framework.chat.Future;
import com.uc.framework.chat.PauseFuture;
import com.uc.framework.chat.strategy.AckStrategy;
import com.uc.framework.logger.Logs;
import com.uc.framework.redis.RedisHandler;
import com.uc.framework.redis.queue.DelayQueue;
import com.uc.framework.redis.queue.MessageListener;
import com.uc.framework.redis.queue.Task;
import com.uc.framework.thread.AsyncTask;

public abstract class AbstractChatProcessor implements ChatProcessor, MessageListener {
    /** 控制器 */
    AbstarctChatController controller;
    /** 配置中心 */
    ChatConfigure configure;
    /** 参数 */
    ChatRequest request;
    /** 消息管道 */
    final MessagePipeLine pipeLine = new MessagePipeLine(this);

    /**
     * 
     * title: 延时队列的 构造key
     *
     * @return
     * @author HadLuo 2020-9-19 16:09:20
     */
    public abstract DelayQueue getQueue();

    public AbstarctChatController controller() {
        if (controller == null) {
            controller = new DefaultChatController(configure, request.getAlias());
        }
        return controller;
    }

    @Override
    public void setRequest(ChatRequest request) {
        // TODO Auto-generated method stub
        this.request = request;
    }

    @Override
    public void setController(AbstarctChatController controller) {
        this.controller = controller;
    }

    @Override
    public void setConfigure(ChatConfigure configure) {
        this.configure = configure;
    }

    /**
     * 
     * title: 将剧本发射到 redis queue
     *
     * @param playId
     * @author HadLuo 2020-9-26 17:12:05
     */
    public void launch(ChatGroup chatGroup) {
        if (chatGroup == null || CollectionUtils.isEmpty(chatGroup.getChats())) {
            return;
        }
        String pauseGroupId = controller().checkPause(chatGroup);
        if (!StringUtils.isEmpty(pauseGroupId)) {
            // 有暂停的剧本 ，清楚已经存在的
            Logs.e(getClass(),
                    "有暂停的剧本" + JSON.toJSONString(pauseGroupId) + ",uuid=" + chatGroup.getGroupUuid());
            // 清除暂停的存储 消息。 以便新消息能触发
            controller().clearStore(chatGroup.getGroupUuid(), pauseGroupId);
        }
        // 创建聊天消息包 <所有要发的群id， 发言人消息> 排序好的聊天包
        LinkedList<Chat> chats = controller().createSortedChat(chatGroup);
        // 回调聊天剧本开始
        configure.getLifeCycle().onStartup(chatGroup.getGroupUuid(), chats);
        // 取出第一个聊天包， 发送
        Chat chat = chats.removeFirst();
        if (null == chat) {
            return;
        }
        // 需要发送的群
        List<String> needSendWxGroupIds = chat.getNeedSendGroupWxIds() ;
        
        chat.setLimit(chat.getSort());
        // 将消息放入管道， 流入到redis延时队列里面
        pipeLine.add(chat);
        if (CollectionUtils.isEmpty(chats)) {
            configure.getLifeCycle().onGroupFinish(chat.getGroupUuid(), chat.getCurrentSendGroupWxId());
            return;
        }
        // 取最大的limit
        int limit = chats.get(chats.size() - 1).getSort();
        // 设置limit
        chat.setLimit(limit);
        // 将剩余消息 ，存储到redis 发送池中
        saveChatRedisPools(chats, limit,needSendWxGroupIds);
    }

    /***
     * 
     * title: 将要发的消息 临时存储到redis
     *
     * @param pair
     * @author HadLuo 2020-9-27 14:59:46
     */
    private void saveChatRedisPools(LinkedList<Chat> chats, int limit,List<String> needSendWxGroupIds) {
        int initSort = chats.get(0).getSort();
        while (true) {
            if (CollectionUtils.isEmpty(chats)) {
                return;
            }
            // 取出 下一个 放入到 存储池
            Chat chat = chats.removeFirst();
            if (chat == null) {
                break;
            }
            chat.setLimit(limit);
            // 放入存储池
            controller().storeChat(chat);
            // 群发送状态存储
            for (String groupWxId : needSendWxGroupIds) {
                // 每个群都是 发送到 第一个 发言人
                controller().storeGroupSortStatus(chat.getGroupUuid(), groupWxId, initSort);
            }
        }
    }

    /***
     * title: 单个群收到延时队列的消息
     */
    @Override
    public void onMessage(Task task) {
        Chat chat = (Chat) task.getData();
        // 单条消息开始推送
        configure.getLifeCycle().onSingleSendStart(chat.getGroupUuid(), chat);
        // 调用业务代码 异步 发送 群消息
        Future future = configure.getSendChatStrategy().asyncSend(chat);
        // 获取业务返回唯一操作码， 用来 作半消息的ack确认
        String ackKey = future.getAckKey();
        // 是立即发送失败 需要继续发送下一个发言人消息 ，否则 等待mq异步回调 消息结果
        if (future instanceof ErrorFuture) {
            // 回调单条消息发送失败
            configure.getLifeCycle().onSingleSendError(future.getAckKey(), chat.getGroupUuid(), chat,
                    future.getErrorMessage());
            // 取出这一个组的 下一条消息继续发送
            moveToNext(chat);
        } else if (future instanceof PauseFuture) {
            // 需要暂停这个群 的这条消息推送
            startPause(future.getUuid(), future.getGroupId(), -1);
            // 如果是暂停了， 需要 将这个消息 设置为一个标志， 后面 恢复暂停判断标志存在就继续发
            String k = request.getAlias() + "." + chat.getGroupUuid() + "." + chat.getCurrentSendGroupWxId();
            RedisHandler.set(k, "1");
        } else {
            // 半消息，需要kafka回复确认 消息是发成功了 , 放入消息临时存储
            controller().storeHalfChat(ackKey, chat);
            configure.getLifeCycle().onSingleHalfSendSuccess(chat.getGroupUuid(), ackKey, chat);
        }
    }

    /***
     * 
     * title:取出这这个群的 下一条消息继续发送
     *
     * @param chat
     * @author HadLuo 2020-10-9 18:46:55
     */
    private void moveToNext(Chat chat) {
        // 如果是被暂停的， 不取下一条
        if (controller().getPause(chat.getGroupUuid(), chat.getCurrentSendGroupWxId())) {
            return;
        }
        // 将要发送的游标置为下一个
        int nextSort = controller().moveToNext(chat);
        Chat nextChat = controller().getChat(chat.getGroupUuid(), nextSort);
        Logs.e(getClass(), "moveToNext>>nextSort=" + nextSort + ",chat=" + JSON.toJSONString(chat)
                + ",nextchat=" + nextChat);
        // 放入管道
        pipeLine.add(nextChat, chat.getCurrentSendGroupWxId());
        if (chat.getLimit() != 0 && chat.getLimit() <= nextSort - 1) {
            Logs.e(getClass(), "群维度发言人发送完成>>limit=" + chat.getLimit() + ",nextSort=" + nextSort + ",chat="
                    + JSON.toJSONString(chat));
            // 某个群的 所有发言人 发言完成
            configure.getLifeCycle().onGroupFinish(chat.getGroupUuid(), chat.getCurrentSendGroupWxId());
        }
    }

    @Override
    public void onAck(AckStrategy ackStrategy) {
        Future future = ackStrategy.get();
        // 查询ack 缓存池
        Chat chat = controller().getHalfChat(future.getAckKey(), future.getGroupId());
        if (chat == null) {
            // 延迟执行， 确认半消息存储成功
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    onAck0(controller().getHalfChat(future.getAckKey(), future.getGroupId()), future);
                }
            }, 2000);

            return;
        }
        onAck0(chat, future);
    }

    private void onAck0(Chat chat, Future future) {
        if (chat == null) {
            return;
        }
        String ackKey = future.getAckKey();
        if (future instanceof ErrorFuture) {
            // 消息失败
            configure.getLifeCycle().onSingleAckSendError(chat.getGroupUuid(), ackKey, chat,
                    future.getErrorMessage());
        } else {
            // 消息发送成功 ,回调发送成功
            configure.getLifeCycle().onSingleAckSendSuccess(chat.getGroupUuid(), ackKey, chat);
        }
        // 将半消息 删除
        controller().delHalfChat(ackKey, chat.getCurrentSendGroupWxId());
        if (controller().getPause(chat.getGroupUuid(), chat.getCurrentSendGroupWxId())) {
            // 如果是暂停了， 需要 将这个消息 设置为一个标志， 后面 恢复暂停判断标志存在就继续发
            String k = request.getAlias() + "." + chat.getGroupUuid() + "." + chat.getCurrentSendGroupWxId();
            RedisHandler.set(k, "1");
            return;
        }
        // 取下条消息继续发送
        moveToNext(chat);
    }

    public ChatRequest getRequest() {
        return request;
    }

    @Override
    public void startPause(String groupUuid, String groupWxId, int second) {
        controller().storePause(groupUuid, groupWxId, second);
        configure.getLifeCycle().onPause(groupUuid, groupWxId);
    }

    @Override
    public void cancelPause(String groupUuid, String groupWxId) {
        controller().delPause(groupUuid, groupWxId);
        String k = request.getAlias() + "." + groupUuid + "." + groupWxId;
        if ("1".equals(RedisHandler.get(k))) {
            // 获取当前发送到哪
            int sort = controller().getGroupSortStatus(groupUuid, groupWxId);
            Chat chat = controller().getChat(groupUuid, sort);
            if (chat == null) {
                return;
            }
            configure.getLifeCycle().onResume(groupUuid, groupWxId);
            chat.setCurrentSendGroupWxId(groupWxId);
            moveToNext(chat);
        }
        // 如果是暂停了， 需要 将这个消息 设置为一个标志， 后面 恢复暂停判断标志存在就继续发
        RedisHandler.del(k);
    }

    @Override
    public void clearStore(String groupUuid) {
        // 删除所有 redis store
        controller().clearStore(groupUuid);
    }
}
