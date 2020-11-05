package com.uc.framework.login.wechat;

public class WechatUri {

    static final String Default_Domain = "api.weixin.qq.com";
    static final String Default_Protocol = "https";
    /** 域名 */
    private String domain;
    /** 协议 */
    private String protocol;

    private String appId;
    private String appSecret;

    public WechatUri(String appId, String appSecret) {
        this.domain = Default_Domain;
        this.protocol = Default_Protocol;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public String buildAccessTokenUri(String code) {
        return protocol + "://" + domain + "/sns/oauth2/access_token?appid=" + appId + "&secret=" + appSecret
                + "&code=" + code + "&grant_type=authorization_code";
    }

    public String buildUserInfoUri(String token, String openid) {
        return protocol + "://" + domain + "/sns/userinfo?access_token=" + token + "&openid=" + openid
                + "&lang=zh_CN";
    }

    public String buildRefreshTokenUri(String refreshToken) {
        return protocol + "://" + domain + "/sns/oauth2/refresh_token?appid=" + appId + "&grant_type="
                + "refresh_token" + "&refresh_token=" + refreshToken;
    }
}
