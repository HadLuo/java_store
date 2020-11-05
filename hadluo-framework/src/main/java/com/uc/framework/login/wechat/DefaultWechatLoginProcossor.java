package com.uc.framework.login.wechat;

import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.uc.framework.Holder;
import com.uc.framework.logger.Logs;
import com.uc.framework.logger.alert.AlertContext;
import com.uc.framework.web.Https;

public class DefaultWechatLoginProcossor extends WechatLoginProcossor {

    @Override
    public AccessToken selectAccessToken(String code) {
        // token
        String url = getWechatUri().buildAccessTokenUri(code);
        Holder<AccessToken> holder = new Holder<AccessToken>();
        try {
            Https.doGet(url, (json) -> {
                Logs.e(getClass(), "Wechat AccessToken >>" + url + ",result>>" + JSON.toJSONString(json));
                AccessToken token = JSON.parseObject(json, AccessToken.class);
                if (!StringUtils.isEmpty(token.getErrmsg())) {
                    // 请求错误
                    throw new WechatException(token.getErrcode(), token.getErrmsg());
                }
                // 请求 通过 返回 token
                holder.set(token);
                // 存储到redis
                int s = Integer.parseInt(token.getExpires_in());
                if (s <= 0) {
                    s = Integer.parseInt(token.getExpires_in());
                }
                TokenStore.store(token.getAccess_token(), token, s);
                RefreshTokenStore.store(token.getAccess_token(), token.getRefresh_token());
            });
            return holder.get();
        } catch (WechatException e) {
            throw e;
        } catch (Exception e) {
            Logs.e(getClass(), "http請求失败 ,url=" + url, e);
            AlertContext.robot().alert("http請求失败 ,url=" + url, e);
            throw new WechatException(-1111, "网络错误");
        }
    }

    @Override
    WechatUser userinfo(AccessToken token) {
        String url = getWechatUri().buildUserInfoUri(token.getAccess_token(), token.getOpenid());
        Holder<WechatUser> holder = new Holder<WechatUser>();
        try {
            Https.doGet(url, (json) -> {
                Logs.e(getClass(), "Wechat userinfo >>" + url + ",result>>" + JSON.toJSONString(json));
                WechatUser user = JSON.parseObject(json, WechatUser.class);
                if (!StringUtils.isEmpty(user.getErrmsg())) {
                    // 请求错误
                    throw new WechatException(user.getErrcode(), user.getErrmsg());
                }
                // 设置 token
                user.setAccess_token(token.getAccess_token());
                // 请求 通过 返回 token
                holder.set(user);
            });
            return holder.get();
        } catch (WechatException e) {
            throw e;
        } catch (Exception e) {
            Logs.e(getClass(), "http請求失败 ,url=" + url, e);
            AlertContext.robot().alert("http請求失败 ,url=" + url, e);
            throw new WechatException(-1111, "网络错误");
        }
    }

    @Override
    public WechatUser getByToken(String token, String appId, String appSecrect) {
        reset(appId, appSecrect);
        AccessToken accessToken = TokenStore.getStore(token);
        if (accessToken == null) {
            // 已经过期 , 刷新 token Fal7ZZKlVXZSg
            String url = getWechatUri().buildRefreshTokenUri(RefreshTokenStore.getStore(token));
            Holder<AccessToken> holder = new Holder<AccessToken>();
            try {
                Https.doGet(url, (json) -> {
                    Logs.e(getClass(),
                            "Wechat refresh token >>" + url + ",result>>" + JSON.toJSONString(json));
                    AccessToken accessToken2 = JSON.parseObject(json, AccessToken.class);
                    if (!StringUtils.isEmpty(accessToken2.getErrmsg())) {
                        // 请求错误
                        throw new WechatException(accessToken2.getErrcode(), accessToken2.getErrmsg());
                    }
                    // 请求 通过 返回 token
                    holder.set(accessToken2);
                    // 存储到redis
                    int s = Integer.parseInt(accessToken2.getExpires_in()) - 30;
                    if (s <= 0) {
                        s = Integer.parseInt(accessToken2.getExpires_in());
                    }
                    TokenStore.store(accessToken2.getAccess_token(), accessToken2, s);
                    RefreshTokenStore.store(accessToken2.getAccess_token(), accessToken2.getRefresh_token());
                });
                accessToken = holder.get();
            } catch (WechatException e) {
                throw e;
            } catch (Exception e) {
                Logs.e(getClass(), "http請求失败 ,url=" + url, e);
                AlertContext.robot().alert("http請求失败 ,url=" + url, e);
                throw new WechatException(-1111, "网络错误");
            }
        }
        return userinfo(accessToken);
    }

}
