package com.uc.framework.login;

public interface HeaderParser {

    /***
     * 
     * title: 请求头解析器
     *
     * @param request
     * @return
     * @author HadLuo 2020-10-9 9:28:43
     */
    public User parse();

    /***
     * 
     * title: 获取token
     *
     * @return
     * @author HadLuo 2020-10-19 18:10:10
     */
    public String getToken();
}
