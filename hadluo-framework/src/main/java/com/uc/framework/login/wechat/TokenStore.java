package com.uc.framework.login.wechat;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.uc.framework.logger.Logs;
import com.uc.framework.redis.RedisHandler;

public class TokenStore {

    private static String key(String token) {
        return "wechat.token." + token;
    }

    public static void store(String token, AccessToken user, int second) {
        String k = key(token);
        RedisHandler.setExpire(k, JSON.toJSONString(user), second);
        Logs.e(TokenStore.class,
                "TokenStore store >>k=" + k + ",user=" + JSON.toJSONString(user) + ",second=" + second);
    }

    public static AccessToken getStore(String token) {
        String k = key(token);
        String str = RedisHandler.get(k);
        Logs.e(TokenStore.class, "TokenStore getStore >>k=" + k + ",str=" + JSON.toJSONString(str));
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return JSON.parseObject(str, AccessToken.class);
    }

}
