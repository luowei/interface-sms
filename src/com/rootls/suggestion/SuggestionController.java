package com.rootls.suggestion;

import com.rootls.common.bean.NeedLogin;
import com.rootls.common.utils.EHCacheUtil;
import com.rootls.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by luowei on 2014/6/27.
 */
@NeedLogin
@Controller
@RequestMapping("/html")
public class SuggestionController {

    @Autowired
    SuggestionRepository suggestionRepository;

    @RequestMapping("/suggestion")
    public String addSuggestion(Model model,String accessToken,HttpServletRequest request){
        User user = EHCacheUtil.<User>getValue("smsUserCache",
                String.valueOf(request.getAttribute("accessToken")));
        model.addAttribute("user",user);
        return "suggestion";
    }

    @RequestMapping("/addSuggestion")
    public String addSuggestion(HttpServletRequest request,Model model,String accessToken,Suggestion suggestion){
        User user = EHCacheUtil.<User>getValue("smsUserCache",
                String.valueOf(request.getAttribute("accessToken")));
        suggestionRepository.addSuggestion(suggestion);
        model.addAttribute("msg","ok").addAttribute("user",user);
        return "suggestion";
    }

    @NeedLogin(false)
    @RequestMapping("/about")
    public String about(HttpServletRequest request,Model model,String accessToken){

        return "about";
    }
}
