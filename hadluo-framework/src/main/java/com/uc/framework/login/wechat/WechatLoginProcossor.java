package com.uc.framework.login.wechat;

public abstract class WechatLoginProcossor {

    private WechatUri wechatUri;
    String appId;
    String appSecrect;

    /***
     * 
     * title: 授权第一步： 获取 access_token
     *
     * @param code
     * @return
     * @author HadLuo 2020-10-15 10:33:12
     */
    abstract AccessToken selectAccessToken(String code);

    /***
     * 
     * title: 授权第二步： 获取 userinfo
     *
     * @param code
     * @return
     * @author HadLuo 2020-10-15 10:33:12
     */
    abstract WechatUser userinfo(AccessToken token);

    /***
     * 
     * title: 构造实例
     *
     * @return
     * @author HadLuo 2020-10-15 10:38:33
     */
    public static WechatLoginProcossor create() {
        return new DefaultWechatLoginProcossor();
    }

    public void reset(String appId, String appSecrect) {
        // 构造 uri
        this.wechatUri = new WechatUri(appId, appSecrect);
        this.appId = appId;
        this.appSecrect = appSecrect;
    }

    /***
     * 
     * title: 启动登录流程
     *
     * @param appId
     * @param appSerect
     * @param code
     * @author HadLuo 2020-10-15 10:38:42
     */
    public WechatUser startLogin(String appId, String appSecrect, String code) {
        reset(appId, appSecrect);
        // 授权第一步： 获取 access_token
        AccessToken token = selectAccessToken(code);
        // 获取用户
        return userinfo(token);
    }

    /***
     * 
     * title: 根据token 获取微信用户信息
     *
     * @param token
     * @return
     * @author HadLuo 2020-10-19 16:48:17
     */
    public abstract WechatUser getByToken(String token, String appId, String appSecrect);

    public String getAppId() {
        return appId;
    }

    public String getAppSecrect() {
        return appSecrect;
    }

    public WechatUri getWechatUri() {
        return wechatUri;
    }

}
