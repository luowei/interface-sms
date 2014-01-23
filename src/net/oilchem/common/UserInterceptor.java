package net.oilchem.common;

import net.oilchem.common.bean.Config;
import net.oilchem.common.bean.FrequentCheck;
import net.oilchem.common.bean.NeedLogin;
import net.oilchem.user.User;
import net.oilchem.user.UserRepository;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static java.lang.System.currentTimeMillis;
import static net.oilchem.common.bean.Config.SESSION_MAX_INTERVAL;
import static net.oilchem.common.bean.Config.loginCache;
import static net.oilchem.common.utils.IPMacUtil.validBindIp;
import static net.oilchem.common.utils.Md5Util.generatePassword;
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
        boolean frequentCheck = hasFrequentCheckAnotation(method.getDeclaringClass(), method);

        boolean result = false;
        //是否需要登录
        if (!needLogin) {
            result = true;
        } else {

            //检查登陆是否过于频繁
            if (frequentCheck && tooFrequent(request, response)) return result;

            //判断用户登陆
            if (checkUserLogin(request)) {
                result = true;
            } else {
                result = false;
            }
        }
        if (!result) {
            request.getRequestDispatcher("/user/nologin.do").forward(request, response);
        }
        return result;
    }

    /**
     * 用户登录太频繁
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    private boolean tooFrequent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getAttribute("username") == null ? null : String.valueOf(request.getAttribute("username"));
        username = (username == null ? request.getParameter("username") : username);

        Long lastLogin = loginCache.get(username);
        if (lastLogin == null) {
            lastLogin = 0l;
            loginCache.put(username, lastLogin);
        }

        if (currentTimeMillis() - lastLogin < Config.LOGIN_IDLE) {
            request.getRequestDispatcher("/user/toofrequent.do").forward(request, response);
            return true;
        }
        loginCache.put(username, currentTimeMillis());
        return false;
    }

    /**
     * 检查用户登录
     *
     * @param request
     * @return
     */
    private boolean checkUserLogin(HttpServletRequest request) {

        boolean okUser = false;
        User user = null;
        String clientIp = null;
        String username = null;
        String password = null;

        Object userObj = request.getSession().getAttribute("user");
        if (userObj != null) {
            user = (User) userObj;
            username = user.getUsername();
            password = user.getPassword();
        } else {
            username = request.getAttribute("username") == null ? null : String.valueOf(request.getAttribute("username"));
            username = (username == null ? request.getParameter("username") : username);
            password = request.getAttribute("password") == null ? null : String.valueOf(request.getAttribute("password"));
            password = (password == null ? request.getParameter("password") : password);
        }

        if (isNotBlank(username)) {
            //获得用户加密后的密码
            String secretPass = null;
            if(user!=null){
                secretPass = user.getPassword();
            }else if(isNotBlank(password) ){
                secretPass = generatePassword(password);
            }else {
                return false;
            }

            //检查用户信息是否完整来自于数据库
            Boolean fromDB = user != null && user.getIpRight() != null;
            user = !fromDB ? userRepository
                    .findByUsernameAndPassword(username.replace("'", ""), secretPass) : user;

            //获得客户端ip
            clientIp = getIpAddr(request);

            //判断是否有访问数据接口的权限
            okUser = checkInterfaceRight(request, user, clientIp);
        }

        if (okUser) {
            request.getSession().setMaxInactiveInterval(SESSION_MAX_INTERVAL);
            request.getSession().setAttribute("user", user);
            userRepository.updateLoginInfo(user, clientIp);
            return true;
        } else {
            request.getSession().setAttribute("user", null);
            return false;
        }
    }

    private boolean checkInterfaceRight(HttpServletRequest request, User user, String clientIp) {
        boolean okUser;//判断进出口接口
        if (request.getServletPath().startsWith("/trade")) {
            okUser = user == null ? false : checkIP(clientIp, user.getIpRight()) && isNotBlank(user.getStartYearMonth());

        } else {    //判断价格接口
            okUser = user == null ? false : checkIP(clientIp, user.getIpRight())
                    && user.getPriceFlag() != null && user.getPriceFlag().equals(1) && user.getStartDate() != null;
        }
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

    private String getIpAddr(HttpServletRequest request/* ,@RequestHeader MultiValueMap<String,String> headers*/) {
        String ipAddress = request.getHeader("x-forwarded-for");
        ipAddress = isNullIP(ipAddress) ? request.getHeader("Proxy-Client-IP") : ipAddress;
        ipAddress = isNullIP(ipAddress) ? request.getHeader("WL-Proxy-Client-IP") : ipAddress;
        ipAddress = isNullIP(ipAddress) ? request.getHeader("WL-Proxy-Client-IP") : ipAddress;
        ipAddress = isNullIP(ipAddress) ? request.getRemoteAddr() : ipAddress;

        if (ipAddress != null && ipAddress.indexOf(".") == -1) {
            return null;

            //"***.***.***.***".length() = 15
        } else if (ipAddress != null && ipAddress.length() > 15 && ipAddress.indexOf(",") > 0) {

            //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            String[] temparyip = ipAddress.split(",");
            for (int i = 0; i < temparyip.length; i++) {
                if (isIPAddress(temparyip[i])
                        && temparyip[i] != "127.0.0.1"
                        && temparyip[i].substring(0, 3) != "10."
                        && temparyip[i].substring(0, 8) != "192.168."
                        && temparyip[i].substring(0, 7) != "172.16."
                        && temparyip[i].substring(0, 8) != "169.254.") {
                    ipAddress = temparyip[i];
                }
            }
            // ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
        }
        return ipAddress;
    }

    private boolean isNullIP(String ipAddress) {
        return ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress);
    }

    private static boolean isIPAddress(String str1) {
        if (str1 == null || str1.trim().length() < 7 || str1.trim().length() > 15) {
            return false;
        } else {
            return true;
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

    private boolean hasFrequentCheckAnotation(Class clazz, Method method) {

        FrequentCheck methodAnnotation = method.getAnnotation(FrequentCheck.class);
        if (method.isAnnotationPresent(FrequentCheck.class)) {
            return methodAnnotation.value();
        }

        Annotation clazzAnnotation = clazz.getAnnotation(FrequentCheck.class);
        if (clazz.isAnnotationPresent(FrequentCheck.class)) {
            return ((FrequentCheck) clazzAnnotation).value();
        } else {
            return false;
        }
    }
}

