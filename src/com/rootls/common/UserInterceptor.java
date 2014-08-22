package com.rootls.common;

import com.rootls.common.bean.NeedLogin;
import com.rootls.user.UserRepository;
import com.rootls.common.utils.EHCacheUtil;
import com.rootls.sms.SmsRepository;
import com.rootls.user.User;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-2-20
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class UserInterceptor extends HandlerInterceptorAdapter {
    private UserRepository userRepository;

    @Resource
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private SmsRepository smsRepository;

    @Resource
    public void setSmsRepository(SmsRepository smsRepository){
        this.smsRepository = smsRepository;
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.afterConcurrentHandlingStarted(request, response, handler);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Method method = ((HandlerMethod) handler).getMethod();
        boolean needLogin = hasLoginAnotation(method.getDeclaringClass(), method);



        //是否需要登录
        if (!needLogin) {
            return true;
        } else {          //验证用户令牌
            synchronized ("accessToken".intern()) {

                String accessToken = request.getAttribute("accessToken") == null ? null : String.valueOf(request.getAttribute("accessToken"));
                accessToken = (accessToken == null ? request.getParameter("accessToken") : accessToken);
                if (isBlank(accessToken)) {
                    request.getRequestDispatcher("/user/authFaild.do").forward(request, response);
                    return false;
                }

                //首从session里取用户信息
                User user = EHCacheUtil.<User>getValue("smsUserCache", accessToken);

                //首先根据令牌到数据库找
                String token = null;
                if (user == null) {

                    //第一层验证
                    user = userRepository.findByAccessToken(accessToken);
                    if (request.getServletPath().contains("userLogout")) {
                        request.getRequestDispatcher("/user/userLogout.do").forward(request, response);
                        return false;
                    } else if (user == null) {


                        //第二层验证
                        user = userRepository.findByAccessTokenBak(accessToken);
                        if (user != null) {
                            token = randomUUID().toString().replace("-", "");
                        } else {
                            request.getRequestDispatcher("/user/authFaild.do").forward(request, response);
                            return false;
                        }

//                        EHCacheUtil.<String>setValue("userGroups", user.getUsername(), smsRepository.getPushGroupsStr(user));
                    } else {
                        //每次请求更新accessToken
                        userRepository.updateAccessTokenbak(accessToken);
                        token = randomUUID().toString().replace("-", "");
                    }

                    if(request.getRequestURI().equals("/sms/getPushSMS.do") || request.getRequestURI().equals("/sms/getIOSPushSMS.do") ) {
                        EHCacheUtil.<String>setValue("userGroups", user.getUsername(), smsRepository.getPushGroupsStr(user));
                    }

                }

                if (!(user.getStopClient().intValue() == 1 || user.getStopClient().intValue() == 2)) {
                    request.setAttribute("accessToken", user.getAccessToken());
                    request.getRequestDispatcher("/user/noData.do").forward(request, response);
                    return false;
                }

                if (isBlank(user.getAccessToken()) ||
                        !(user.getAccessToken().equals(accessToken) || user.getAccessTokenbak().equals(accessToken)) ) {
                    request.getRequestDispatcher("/user/authFaild.do").forward(request, response);
                    return false;
                }

                //-----------
//                user.setAccessToken(randomUUID().toString().replace("-", ""));
////                userRepository.updateAccessTokenbakByUsername(user.getUsername());
//                userRepository.updateToken(user);
//                EHCacheUtil.setValue("smsUserCache", user.getAccessToken(), user);

                //更新token
                if (token != null) {
                    user.setAccessToken(token);
                    userRepository.updateToken(user);
                    EHCacheUtil.setValue("smsUserCache", user.getAccessToken(), user);
                }

//              userRepository.updateLoginInfo(user);
                userRepository.updateViewtime(user);
                request.setAttribute("accessToken", user.getAccessToken());
                return true;
            }
        }
    }

    private boolean hasLoginAnotation(Class clazz, Method method) {

        NeedLogin methodAnnotation = method.getAnnotation(NeedLogin.class);
        if (method.isAnnotationPresent(NeedLogin.class)) {
            return methodAnnotation.value();
        }

        Annotation clazzAnnotation = clazz.getAnnotation(NeedLogin.class);
        if (clazz.isAnnotationPresent(NeedLogin.class)) {
            return ((NeedLogin) clazzAnnotation).value();
        } else {
            return false;
        }
    }

}

