package com.uc.framework.login.wechat;

import com.alibaba.fastjson.JSON;
import com.uc.framework.logger.Logs;
import com.uc.framework.redis.RedisHandler;

public class RefreshTokenStore {

    private static String key(String token) {
        return "wechat.refreshtoken." + token;
    }

    public static void store(String token, String refreshToken) {
        String k = key(token);
        RedisHandler.setExpire(k, refreshToken, 10);
        Logs.e(RefreshTokenStore.class, "RefreshTokenStore store >>k=" + k + ",refreshToken=" + refreshToken);
    }

    public static String getStore(String token) {
        String k = key(token);
        String str = RedisHandler.get(k);
        Logs.e(RefreshTokenStore.class,
                "RefreshTokenStore getStore >>k=" + k + ",str=" + JSON.toJSONString(str));
        return str;
    }

}
