package net.oilchem.user;

import net.oilchem.common.BaseController;
import net.oilchem.common.bean.NeedLogin;
import net.oilchem.common.utils.EHCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static net.oilchem.common.bean.Config.*;
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
@NeedLogin(false)
@SessionAttributes("user")
@RequestMapping("/user")
public class UserController extends BaseController {


    @Autowired
    UserRepository userRepository;

    @ResponseBody
    @RequestMapping("/loginFaild")
    public String nologin() {
        return format(json_format, "1", "",
                "\"login\":\"0\",\"message\":\"" + login_faild + "\",\"accessToken\":\"\"");
    }

    @ResponseBody
    @RequestMapping("/authFaild")
    public String authFaild() {
        return format(json_format, "1", "",
                "\"login\":\"0\",\"message\":\"" + authentication_faild + "\",\"accessToken\":\"\"");
    }

    @ResponseBody
    @RequestMapping("/noData")
    public String noData(HttpServletRequest request) {
        return format(json_format, "1", "",
                "\"accessToken\":\""+String.valueOf(request.getAttribute("accessToken"))+"\"");
    }

    @ResponseBody
    @RequestMapping("/userLogin")
    public String login(HttpServletRequest request, User user) {

        String username = user.getUsername();
//        String password = (user.getPassword() != null ? generatePassword(user.getPassword()) : user.getPassword());
        String password =  user.getPassword();
        user.setPassword(password);
        String imei = user.getImei();

        //第一次登录：根据用户名、密码到数据库找
        if (isNotBlank(username) && isNotBlank(password) && isNotBlank(imei)) {
            User dbUser = userRepository.findByUsernameAndPassword(username, password);

            //找不到此用户
            if (dbUser == null) {
                return format(json_format, "1", "",
                        "\"login\":\"0\",\"username\":\"\",\"message\":\"" + login_faild + "\",\"accessToken\":\"\"");
            }

            if(imei.equals(dbUser.getImei()) || isBlank(dbUser.getImei())){        //imei相同
                String token = randomUUID().toString().replace("-", "");
                user.setAccessToken(token);
                user.setImei(imei);
            }else {                                   //imei不相同
                //accessToken为空,更新accessToken
                if ( isBlank(dbUser.getAccessToken())) {
                    String token = randomUUID().toString().replace("-", "");
                    user.setAccessToken(token);
                    user.setImei(imei);
                } else {
                    return format(json_format, "1", "",
                            "\"login\":\"0\",\"username\":\"\",\"message\":\"用户已在其它设备登录，请注销后再登录\",\"accessToken\":\"\"");
                }
            }

            user.setStopClient(dbUser.getStopClient());
            user.setRealName(dbUser.getRealName());
            user.setPassword(password);              //密码设置为加密后的密码
            user.setLastIp(getIpAddr(request));      //获得客户端ip
            EHCacheUtil.<User>setValue("smsUserCache", user.getAccessToken(), user);
            userRepository.updateLoginInfo(user);
            return format(json_format, "1", "",
                    "\"login\":\"1\",\"username\":\""+user.getRealName()+"\",\"message\":\"" + login_success + "\",\"accessToken\":\"" + user.getAccessToken() + "\"");
        }
        return format(json_format, "1", "",
                "\"login\":\"0\",\"username\":\"\",\"message\":\"" + login_faild + "\",\"accessToken\":\"\"");
    }

    @ResponseBody
    @RequestMapping("/userLogout")
    public String logout(HttpServletRequest request, String accessToken) {
        User user = EHCacheUtil.<User>getValue("smsUserCache",
                String.valueOf(request.getAttribute("accessToken")));

        if (user == null) {
            user = (user==null?userRepository.findByAccessToken(accessToken):user);
        }
        if (user!=null) {
            userRepository.cleanAccessToken(user);
            EHCacheUtil.removeElment("smsUserCache",user.getAccessToken());
        }
        return format(json_format, "1", "",
                "\"logout\":\"1\"");
    }

    @ResponseBody
    @RequestMapping("/userRegister")
    public String register(HttpServletRequest request, String cell, String authCode) {
//        String code = EHCacheUtil.<String>getValue("authCodeCache", "authCode");
        if (isBlank(cell)) {
            return format(json_format, "1", "",
                    "\"login\":\"0\",\"message\":\"手机号码为空，注册失败\"");
        }

        String sessionId = request.getHeader("sessionId");
        String code = isNotBlank(sessionId)? EHCacheUtil.<String>getValue("authCodeCache", sessionId): null;

        if (isNotBlank(authCode) && isNotBlank(code)
                && authCode.toUpperCase().equals(code)) {
            String clientIp = getIpAddr(request);
            User user = new User(cell, clientIp);
            if(!userRepository.exsitUser(user)){
                userRepository.register(user);
                return format(json_format, "1", "",
                        "\"login\":\"1\",\"message\":\"已成功提交，请等待人工联系\"");
            }else {
                return format(json_format, "1", "",
                        "\"login\":\"1\",\"message\":\"号码已注册，无需重复注册，请等待人工联系\"");
            }
        } else {
            return format(json_format, "1", "",
                    "\"login\":\"1\",\"message\":\"验证码有误，注册失败\"");
        }
    }

    @RequestMapping("/authCode")
    public void authCode(HttpServletRequest req, HttpServletResponse resp, Integer height, Integer width)
            throws IOException {

        width = (width == null || width == 0 ? 90 : width);//定义图片的width
        height = (height == null || height == 0 ? 20 : height);//定义图片的height
        int codeCount = 4;//定义图片上显示验证码的个数
        int xx = 15;
        int fontHeight = 18;
        int codeY = 16;
//        char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
//                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
//                'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        char[] codeSequence = {  '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
//      Graphics2D gd = buffImg.createGraphics();
        //Graphics2D gd = (Graphics2D) buffImg.getGraphics();
        Graphics gd = buffImg.getGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, width, height);

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
        // 设置字体。
        gd.setFont(font);

        // 画边框。
        gd.setColor(Color.BLACK);
        gd.drawRect(0, 0, width - 1, height - 1);

        // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
        gd.setColor(Color.BLACK);
        for (int i = 0; i < 40; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            gd.drawLine(x, y, x + xl, y + yl);
        }

        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;

        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf(codeSequence[random.nextInt(9)]);
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);

            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, (i + 1) * xx, codeY);

            // 将产生的四个随机数组合在一起。
            randomCode.append(code);
        }
        // 将四位数字的验证码保存到Session中。
//        HttpSession session = req.getSession();
//        session.setMaxInactiveInterval(300);
//        session.setAttribute("authCode", randomCode.toString().toUpperCase());

        String sessionId = randomUUID().toString().replace("-", "");
        EHCacheUtil.<String>setValue("authCodeCache", sessionId,randomCode.toString().toUpperCase(),300);
        resp.setHeader("sessionId",sessionId);

        // 禁止图像缓存。
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);

        resp.setContentType("image/jpeg");

        // 将图像输出到Servlet输出流中。
        ServletOutputStream sos = resp.getOutputStream();
        ImageIO.write(buffImg, "jpeg", sos);
        sos.close();
    }


    @ResponseBody
    @RequestMapping("/updateApp")
    public String updateApp(String version) {
        int update = 0;
        String downloadUrl = "";
        if (isNotBlank(version) && !version.equals(latestAppVersion)) {
            update = 1;
            downloadUrl = appDownload;
        }
        return format(json_format, "1", "",
                "\"update\":\"" + update + "\",\"downloadUrl\":\"" + downloadUrl + "\"");
    }


    @ResponseBody
    @RequestMapping("/getAuthCode")
    public String getAuthCode(){
        return format(json_format, "1", "",
                "\"url\":\"http://android.oilchem.net/user/authCode.do\"");
    }

}
