package com.uc.framework.login;

import com.uc.framework.login.wechat.WechatUser;

/**
 * title: 登录拦截器
 * 
 * @author HadLuo
 * @date 2020-9-2 17:01:12
 */
public class WechatUserThreadLocal {

    private static final ThreadLocal<WechatUser> threadlocal = new ThreadLocal<WechatUser>();

    public static void set(WechatUser user) {
        threadlocal.set(user);
    }

    public static WechatUser get() {
        return threadlocal.get();
    }

    public static void remove() {
        threadlocal.remove();
    }
}
