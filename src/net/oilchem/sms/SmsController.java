package net.oilchem.sms;

import net.oilchem.common.BaseController;
import net.oilchem.common.bean.JsonRet;
import net.oilchem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-24
 * Time: 上午8:58
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/sms")
public class SmsController extends BaseController {

    @Autowired
    SmsRepository smsRepository;

    @ResponseBody
    @RequestMapping("/getPushSMS")
    public JsonRet<Map> getPushSMS(HttpServletRequest request){

        User user = (User)request.getSession().getAttribute("user");

        Map map = new HashMap();
        map.put("ts",new Date().getTime());

        List<Sms> smsList = smsRepository.list(user);

        map.put("messages",smsList);
        return new JsonRet<Map>();
    }

    @ResponseBody
    @RequestMapping("/getConfig")
    public JsonRet<Map> getConfig(HttpServletRequest request){

        User user = (User)request.getSession().getAttribute("user");
        List<Group> groups=smsRepository.listGroup(user);
        return null;
    }

    @ResponseBody
    @RequestMapping("/getMessages")
    public JsonRet<Map> getMessages(HttpServletRequest request,Sms sms){
        User user = (User)request.getSession().getAttribute("user");

        Map map = new HashMap();
        map.put("ts",new Date().getTime());

        List<Sms> smsList = smsRepository.getMessages(user);

        map.put("messages",smsList);
        return new JsonRet<Map>();

    }



}
