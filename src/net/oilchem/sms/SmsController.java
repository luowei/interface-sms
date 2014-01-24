package net.oilchem.sms;

import net.oilchem.common.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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


}
