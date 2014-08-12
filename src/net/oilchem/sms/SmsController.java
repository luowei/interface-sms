package net.oilchem.sms;

import net.oilchem.common.BaseController;
import net.oilchem.common.bean.InerCache;
import net.oilchem.common.bean.JsonRet;
import net.oilchem.common.bean.NeedLogin;
import net.oilchem.common.utils.EHCacheUtil;
import net.oilchem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static net.oilchem.common.bean.Config.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-24
 * Time: 上午8:58
 * To change this template use File | Settings | File Templates.
 */
@NeedLogin
@Controller
@RequestMapping("/sms")
public class SmsController extends BaseController {

    @Autowired
    SmsRepository smsRepository;

    @ResponseBody
    @RequestMapping("/getPushSMS")
    public JsonRet<Map> getPushSMS(HttpServletRequest request, Sms sms, String accessToken) {

        User user = EHCacheUtil.<User>getValue("smsUserCache",
                String.valueOf(request.getAttribute("accessToken")));

        Map map = new HashMap();
        map.put("ts", String.valueOf(new Date().getTime()));
        map.put("accessToken", String.valueOf(request.getAttribute("accessToken")));

        List<Sms> smsList = smsRepository.getPushSMS(user, sms);

        map.put("messages", smsList);
        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(map);
        return ret;
    }

    /**
     * 取当天的信息
     *
     * @param request
     * @param sms
     * @param accessToken
     * @return
     */
    @ResponseBody
    @RequestMapping("/getMessages")
    public JsonRet<Map> getMessages(HttpServletRequest request, Sms sms, String accessToken) {
        User user = EHCacheUtil.<User>getValue("smsUserCache",
                String.valueOf(request.getAttribute("accessToken")));

        Map map = new HashMap();
        map.put("ts", String.valueOf(new Date().getTime()));
        map.put("accessToken", String.valueOf(request.getAttribute("accessToken")));

        List<Sms> smsList = smsRepository.getMessages(user, sms);

        map.put("messages", smsList);
        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(map);
        return ret;
    }

    @NeedLogin(false)
    @ResponseBody
    @RequestMapping("/getMessageTrial")
    public JsonRet<Map> getMessageTrial(String key, String ts) {

        Map map = new HashMap();
        map.put("ts", String.valueOf(new Date().getTime()));

        List<Sms> smsList = smsRepository.getMessageTrial(key, ts);

        map.put("messages", smsList);
        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(map);
        return ret;
    }

    @ResponseBody
    @RequestMapping("/getConfig")
    public JsonRet<Map> getConfig(String accessToken, HttpServletRequest request) throws IOException {
        User user = EHCacheUtil.<User>getValue("smsUserCache",
                String.valueOf(request.getAttribute("accessToken")));

        Map dataMap = new HashMap();
        Map configMap = new HashMap();

        List<Group> groups = smsRepository.getCategories(user);

        configMap.put("categories", groups);
        configMap.put("customerServicename", customerServicename);
        configMap.put("customerServiceNumber", customerServiceNumber);
        configMap.put("pageSizeWhileSearchingLocalSMS", String.valueOf(pageSizeWhileSearchingLocalSMS));
        configMap.put("latestAppVersion", latestAppVersion);
        configMap.put("appDownload", appDownload);
        configMap.put("getPushTimeInterval", String.valueOf(getPushTimeInterval));

        dataMap.put("accessToken", String.valueOf(request.getAttribute("accessToken")));
        dataMap.put("config", configMap);
        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(dataMap);
        return ret;
    }

    @ResponseBody
    @RequestMapping("/getCategories")
    public JsonRet<Map> getCategories(String accessToken, HttpServletRequest request) {

        User user = EHCacheUtil.<User>getValue("smsUserCache",
                String.valueOf(request.getAttribute("accessToken")));

        Map dataMap = new HashMap();

        List<Group> groups = smsRepository.getCategories(user);
        dataMap.put("categories", groups);

        dataMap.put("accessToken", String.valueOf(request.getAttribute("accessToken")));

        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(dataMap);
        return ret;
    }

    @ResponseBody
    @RequestMapping("/getReplies")
    public JsonRet<Map> getReplies(String accessToken, String id, HttpServletRequest request) {
        User user = EHCacheUtil.<User>getValue("smsUserCache",
                String.valueOf(request.getAttribute("accessToken")));

        Map dataMap = new HashMap();

        List<Reply> replies = smsRepository.getReplies(id, user);
        dataMap.put("replies", replies);

        dataMap.put("accessToken", String.valueOf(request.getAttribute("accessToken")));

        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(dataMap);
        return ret;
    }

    @ResponseBody
    @RequestMapping("/pushReply")
    public JsonRet<Map> pushReply(String accessToken, Reply reply, HttpServletRequest request) {
        User user = EHCacheUtil.<User>getValue("smsUserCache",
                String.valueOf(request.getAttribute("accessToken")));
        if (isBlank(reply.getUsername())) {
            reply.setUsername(user.getUsername());
        }
        Map dataMap = new HashMap();
        Reply re = smsRepository.pushReply(reply);

        dataMap.put("reply", re);
        dataMap.put("accessToken", String.valueOf(request.getAttribute("accessToken")));
        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(dataMap);
        return ret;
    }

    @ResponseBody
    @RequestMapping("/changePushStat")
    public JsonRet<Map> changePushStat(String accessToken, Group group, HttpServletRequest request) {

        User user = EHCacheUtil.<User>getValue("smsUserCache",
                String.valueOf(request.getAttribute("accessToken")));

        Map dataMap = new HashMap();

        smsRepository.updateCategories(user, group);
        group.setId(isNumeric(group.getGroupId()) ? Integer.valueOf(group.getGroupId()) : null);
        List<Group> groups = new ArrayList<Group>();
        groups.add(group);
        dataMap.put("categories", groups);
        dataMap.put("accessToken", String.valueOf(request.getAttribute("accessToken")));
        JsonRet<Map> ret = new JsonRet<Map>();
        ret.setData(dataMap);
        return ret;
    }


    @NeedLogin(false)
    @ResponseBody
    @RequestMapping("/clearGroupCache")
    public String clearGroupCache() {
        InerCache.clearCache();
        return "var json = {'result':'1',message:'1'};";
    }

}
