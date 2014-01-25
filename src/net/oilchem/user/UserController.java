package net.oilchem.user;

import net.oilchem.common.BaseController;
import net.oilchem.common.bean.NeedLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;

import static java.lang.String.format;
import static net.oilchem.common.bean.Config.SESSION_MAX_INTERVAL;
import static net.oilchem.common.utils.Md5Util.generatePassword;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-24
 * Time: 上午8:57
 * To change this template use File | Settings | File Templates.
 */
@Controller
@NeedLogin
@SessionAttributes("user")
@RequestMapping("/user")
public class UserController extends BaseController {


    @Autowired
    UserRepository userRepository;

    @NeedLogin(false)
    @ResponseBody
    @RequestMapping("/nologin")
    public String nologin() {
        return format(FORMAT,"\"1\"","\"\"",
                "\"login\":\"0\",\"message\":\"登录失败！\",\"accessToken\":\"\"");
    }

    @NeedLogin(false)
    @RequestMapping("/login")
    public String login(HttpServletRequest request, User user) {

        String username = user.getUsername();
        String password = (user.getPassword() != null ? generatePassword(user.getPassword()) : user.getPassword());
        user.setPassword(password);

        //2.第一次登录：根据用户名、密码到数据库找
        if (isNotBlank(username) && isNotBlank(password)) {
            User dbUser = userRepository.findByUsernameAndPassword(username, password);

            if (dbUser == null) {
                return "forward:/user/nologin";
            } else if (isBlank(dbUser.getAccessToken())) {       //第一次登录,更新accessToken
                user.setUsername(username);
                user.setStopClient(dbUser.getStopClient());
                String token = request.getSession().getId();
                user.setAccessToken(token);
            } else {
                user.setStopClient(dbUser.getStopClient());
                user.setAccessToken(dbUser.getAccessToken());
            }
            user.setPassword(password);
        }

        //获得客户端ip
        user.setLastIp(getIpAddr(request));

        request.getSession().setMaxInactiveInterval(SESSION_MAX_INTERVAL);
        request.getSession().setAttribute("user", user);
        userRepository.updateLoginInfo(user);
        return  format(FORMAT,"\"1\"","\"\"",
                "\"login\":\"1\",\"message\":\"登录成功！\",\"accessToken\":\""+user.getAccessToken()+"\"");
    }

    @ResponseBody
    @RequestMapping("/logout")
    public String logout(SessionStatus sessionStatus) {
        if (!sessionStatus.isComplete()) {
            sessionStatus.setComplete();
        }
        return format(FORMAT,"\"1\"","\"\"",
                "\"login\":\"1\",\"message\":\"登出成功！\",\"accessToken\":\"\"");
    }

    @NeedLogin(false)
    @RequestMapping("/register")
    public String register(HttpServletRequest request, User user) {

        return null;
    }



}
