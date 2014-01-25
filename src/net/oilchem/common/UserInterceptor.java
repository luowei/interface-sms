package net.oilchem.common;

import net.oilchem.common.bean.NeedLogin;
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
import static net.oilchem.common.bean.Config.SESSION_MAX_INTERVAL;
import static net.oilchem.common.utils.IPMacUtil.validBindIp;
import static net.oilchem.common.utils.Md5Util.generatePassword;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

        Method method = ((HandlerMethod) handler).getMethod();
        boolean needLogin = hasLoginAnotation(method.getDeclaringClass(), method);

        boolean result = false;
        //是否需要登录
        if (!needLogin) {
            result = true;
        } else {

            //判断用户登陆
            if (checkUserLogin(request)) {
                result = true;
            } else {
                result = false;
            }
        }
        if (!result) {
            request.getRequestDispatcher("/user/nologin").forward(request, response);
        }
        return result;
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
        if (userObj == null) {     //新会话
            String username = request.getAttribute("username") == null ? null : String.valueOf(request.getAttribute("username"));
            username = (username == null ? request.getParameter("username") : username);

            String password = request.getAttribute("password") == null ? null : String.valueOf(request.getAttribute("password"));
            password = (password == null ? request.getParameter("password") : password);
            password = (password == null ? generatePassword(password) : password);       //加密

            String accessToken = request.getAttribute("accessToken") == null ? null : String.valueOf(request.getAttribute("accessToken"));
            accessToken = (accessToken == null ? request.getParameter("accessToken") : accessToken);


            //1.非第一次登录：首先根据令牌到数据库找
            User dbUser = null;
            if (isNotBlank(accessToken) && isBlank(username) && isBlank(password)) {
                dbUser = userRepository.findByAccessToken(accessToken);
                if (dbUser == null) {
                    return false;
                }
            }

            //2.第一次登录：根据用户名、密码到数据库找
            if (isNotBlank(username) && isNotBlank(password)) {
                dbUser = userRepository.findByUsernameAndPassword(username, password);

                if (dbUser == null) {
                    return false;
                } else if (isBlank(dbUser.getAccessToken())) {       //第一次登录
                    user.setUsername(username);
                    String token = request.getSession().getId();
                    user.setAccessToken(token);
                } else if (!dbUser.getAccessToken().equals(accessToken)) {
                    return false;
                }
                user.setStopClient(dbUser.getStopClient());
                user.setPassword(password);
            }

            //获得客户端ip
            user.setLastIp(getIpAddr(request));
        }else {    //非新会话
            user = (User) userObj;
        }
//        //判断是否有访问数据接口的权限
//        okUser = checkInterfaceRight(request, user, clientIp);

        request.getSession().setMaxInactiveInterval(SESSION_MAX_INTERVAL);
        request.getSession().setAttribute("user", user);
        userRepository.updateLoginInfo(user);
        return true;
    }

    private boolean checkInterfaceRight(HttpServletRequest request, User user, String clientIp) {
        boolean okUser = true;//判断进出口接口
        return okUser;
    }

    /**
     * 检查ip地址
     *
     * @param clientIp
     * @param ipRight
     * @return
     */
    private Boolean newCheckIP(String clientIp, String ipRight) {
        String bindIps[] = ipRight.replaceAll("\\r\\n", "|").split("\\|");
        return validBindIp(bindIps, clientIp);
    }


    /**
     * 检查ip地址(老方法)
     *
     * @param clientIp
     * @param ipRight
     * @return
     */
    private boolean checkIP(String clientIp, String ipRight) {
        if (ipRight == null) {
            ipRight = "";
        }
        if (ipRight.equals("*.*.*.*")) {
            return true;
        }
        boolean isOk = false;

        String array[] = ipRight.replaceAll("\\r\\n", "|").split("\\|");
        for (int i = 0; i < array.length; i++) {
            isOk = true;
            String[] clientIP = clientIp.split("[.]");
            String[] checkIP = array[i].split("[.]");
            if (!(clientIP[0].equals(checkIP[0])) && checkIP[0].indexOf("*") == -1) {
                isOk = false;
            }
            if (!(clientIP[1].equals(checkIP[1])) && checkIP[1].indexOf("*") == -1) {
                isOk = false;
            }
            if (!(clientIP[2].equals(checkIP[2])) && checkIP[2].indexOf("*") == -1) {
                isOk = false;
            }
            if (!(clientIP[3].equals(checkIP[3])) && checkIP[3].indexOf("*") == -1) {
                isOk = false;
            }
            if (isOk) {
                return isOk;
            }
        }
        return isOk;
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

