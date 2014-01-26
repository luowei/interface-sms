package net.oilchem.common;

import net.oilchem.common.bean.NeedLogin;
import net.oilchem.common.utils.EHCacheTool;
import net.oilchem.user.User;
import net.oilchem.user.UserRepository;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static net.oilchem.common.BaseController.getIpAddr;
import static net.oilchem.common.bean.Config.session_max_interval;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (! (handler instanceof HandlerMethod)){
            return true;
        }
            Method method = ((HandlerMethod) handler).getMethod();
        boolean needLogin = hasLoginAnotation(method.getDeclaringClass(), method);

        //是否需要登录
        if (!needLogin) {
            return true;
        } else {          //验证用户令牌

            String accessToken = request.getAttribute("accessToken") == null ? null : String.valueOf(request.getAttribute("accessToken"));
            accessToken = (accessToken == null ? request.getParameter("accessToken") : accessToken);
            if(isBlank(accessToken)){
                request.getRequestDispatcher("/user/authFaild").forward(request, response);
            }

            //首从session里取用户信息
            User user = EHCacheTool.<User>getValue("user");

            //首先根据令牌到数据库找
            if (user==null) {
                user = userRepository.findByAccessToken(accessToken);
                if (user == null) {
                    request.getRequestDispatcher("/user/authFaild").forward(request, response);
                }
            }

            if(user.getStopClient()!=null && user.getStopClient().intValue()==1 ){
                request.getRequestDispatcher("/user/noData").forward(request, response);
            }

            if (isBlank(user.getAccessToken()) ||
                    !user.getAccessToken().equals(accessToken)) {
                request.getRequestDispatcher("/user/authFaild").forward(request, response);
            }

            EHCacheTool.setValue("user", user);
//            userRepository.updateLoginInfo(user);
            return true;
        }

    }

    /**
     * 检查用户登录
     *
     * @param request
     * @return
     */
    private boolean checkUserLogin(HttpServletRequest request) {

        User user = null;

        //首从session里取用户信息
        Object userObj = request.getSession().getAttribute("user");

        String accessToken = request.getAttribute("accessToken") == null ? null : String.valueOf(request.getAttribute("accessToken"));
        accessToken = (accessToken == null ? request.getParameter("accessToken") : accessToken);
        if(isBlank(accessToken)){
            return false;
        }

        //首先根据令牌到数据库找
        if (userObj==null) {
            user = userRepository.findByAccessToken(accessToken);
            if (user == null) {
                return false;
            }
        }else {
            user = (User)userObj;
        }

        if (isBlank(user.getAccessToken()) ||  user.getStopClient()==1 ||
                !user.getAccessToken().equals(accessToken)) {
            return false;
        }

        //获得客户端ip
        user.setLastIp(getIpAddr(request));

        request.getSession().setMaxInactiveInterval(session_max_interval);
        request.getSession().setAttribute("user", user);
        userRepository.updateLoginInfo(user);
        return true;
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

