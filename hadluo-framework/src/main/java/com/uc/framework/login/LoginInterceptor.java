package com.uc.framework.login;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.uc.framework.logger.Logs;
import com.uc.framework.logger.alert.AlertContext;
import com.uc.framework.login.wechat.DefaultWechatLoginProcossor;
import com.uc.framework.login.wechat.WechatException;
import com.uc.framework.login.wechat.WechatLoginProcossor;
import com.uc.framework.login.wechat.WechatUser;
import com.uc.framework.obj.Result;

/**
 * login登录拦截器
 * 
 * @author HadLuo
 * @date 2020-9-2 15:44:38
 */
@Component
@RefreshScope
public class LoginInterceptor implements HandlerInterceptor {

    private static final int UnkonwLoginCode = 10011;
    private static final String UnkonwLoginMsg = "登录异常";

    private static final Map<String, Object> properties = new HashMap<String, Object>();

    public static void addProperties(String key, Object value) {
        properties.put(key, value);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        List<Annotation> annotations = Lists.newArrayList();
        boolean outter = false;
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            Login login = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
            WechatLogin wechatLogin = ((HandlerMethod) handler).getMethodAnnotation(WechatLogin.class);
            if (login == null && wechatLogin == null) {
                return true;
            }
            if (login != null) {
                annotations.add(login);
            } else if (wechatLogin != null) {
                annotations.add(wechatLogin);
            }
            outter = true;
        }
        if (!outter) {
            // 外部接口 或者 资源
            return true;
        }

        if (CollectionUtils.isEmpty(annotations)) {
            return true;
        }

        if (annotations.get(0) instanceof WechatLogin) {
            // 走微信登录
            return wechatLogin(request, response);
        }
        // 走比邻登录
        if (Logs.isDev()) {
            // 开发环境
            User user = new DebugHeaderParser((String) properties.get("token")).parse();
            if (user == null) {
                sendJsonMessage(response, Result.err(UnkonwLoginCode, UnkonwLoginMsg));
                Logs.e(getClass(), "login>>parse ex");
                return false;
            }
            UserThreadLocal.set(user);
            return true;
        } else {
            // 正式环境 ,测试
            try {
                HeaderParser parser = AbstractHeaderParser.resolver(request);
                if (parser == null) {
                    sendJsonMessage(response, Result.err(UnkonwLoginCode, UnkonwLoginMsg));
                    Logs.e(getClass(), "login>>resolver ex");
                    return false;
                }
                User user = parser.parse();
                if (user == null) {
                    sendJsonMessage(response, Result.err(UnkonwLoginCode, UnkonwLoginMsg));
                    Logs.e(getClass(), "login>>parse ex");
                    return false;
                }
                UserThreadLocal.set(user);
                return true;
            } catch (Throwable e) {
                Logs.e(getClass(), "[登录拦截器异常]>>" + request, e);
                AlertContext.robot().alert("[登录拦截器异常]>>" + request, e);
            }
            sendJsonMessage(response, Result.err(UnkonwLoginCode, UnkonwLoginMsg));
            return false;
        }
    }

    private boolean wechatLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HeaderParser parser = AbstractHeaderParser.resolver(request);
        String appId = (String) properties.get("appId");
        String appSecrect = (String) properties.get("appSecrect");
        WechatLoginProcossor wechatLoginProcossor = new DefaultWechatLoginProcossor();
        WechatUser user = null;
        try {
            user = wechatLoginProcossor.getByToken(parser.getToken(), appId, appSecrect);
            Logs.e(getClass(), "wechat login>>"+ JSON.toJSONString(user));
            if (user == null) {
                sendJsonMessage(response, Result.err(40001, "登录失效，请退出重新登录！"));
                return false;
            }
            WechatUserThreadLocal.set(user);
            return true;
        } catch (WechatException e) {
            sendJsonMessage(response, Result.err(e.getCode(), e.getMessage()));
        } catch (Throwable e) {
            Logs.e(getClass(), "[微信登录拦截器异常]>>user=" + JSON.toJSONString(user), e);
            sendJsonMessage(response, Result.err(40001, UnkonwLoginMsg));
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        UserThreadLocal.remove();
        WechatUserThreadLocal.remove();
    }

    public static void sendJsonMessage(HttpServletResponse response, Object obj) throws Exception {
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(JSON.toJSONString(obj));
        writer.close();
        response.flushBuffer();
        Logs.e(LoginInterceptor.class, "login ex >> " + JSON.toJSONString(obj));
    }
}
