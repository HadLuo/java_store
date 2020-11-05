package com.uc.framework.login.wechat;

public class WechatException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -2815169013722056143L;
    int code;

    public WechatException(int code, String err) {
        super(err);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
