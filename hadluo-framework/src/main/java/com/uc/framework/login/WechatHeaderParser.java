package com.uc.framework.login;

/**
 * 
 * title: 留客请求头解析器
 *
 * @author HadLuo
 * @date 2020-10-9 9:29:19
 */
public class WechatHeaderParser extends AbstractHeaderParser {

    public WechatHeaderParser(String value) {
        super(value);
    }

    @Override
    public User parse() {
        throw new UnsupportedOperationException();
    }

}
