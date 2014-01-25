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
        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(map);
        return ret;
    }

    @ResponseBody
    @RequestMapping("/getMessages")
    public JsonRet<Map> getMessages(HttpServletRequest request,Sms sms){
        User user = (User)request.getSession().getAttribute("user");

        Map map = new HashMap();
        map.put("ts",String.valueOf(new Date().getTime()));

        List<Sms> smsList = smsRepository.getMessages(user);

        map.put("messages",smsList);
        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(map);
        return ret;
    }

    @ResponseBody
    @RequestMapping("/getMessageTrial")
    public JsonRet<Map> getMessageTrial(HttpServletRequest request,Sms sms){

        return null;
    }

    @ResponseBody
    @RequestMapping("/getConfig")
    public JsonRet<Map> getConfig(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        Map dataMap = new HashMap();
        Map configMap = new HashMap();

        List<Group> groups=smsRepository.getCategories(user);
        configMap.put("categories",groups);

        configMap.put("customerServicename",user.getUsername());
        configMap.put("customerServiceNumber",user.getUsername());
        configMap.put("pageSizeWhileSearchingLocalSMS","10");
        configMap.put("latestAppVersion","1.0");
        configMap.put("appDownload","http://www.oilchem.net/download/android/sms.apk");

        dataMap.put("config",configMap);
        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(dataMap);
        return ret;
    }

    @ResponseBody
    @RequestMapping("/getCategories")
    public JsonRet<Map> getCategories(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        Map dataMap = new HashMap();

        List<Group> groups=smsRepository.getCategories(user);
        dataMap.put("categories",groups);

        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(dataMap);
        return ret;
    }

    @ResponseBody
    @RequestMapping("/changePushStat")
    public JsonRet<Map> changePushStat(HttpServletRequest request,Group group){
        User user = (User)request.getSession().getAttribute("user");
        Map dataMap = new HashMap();

        smsRepository.updateCategories(user,group);

        dataMap.put("categories",group);
        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(dataMap);
        return ret;
    }

}
