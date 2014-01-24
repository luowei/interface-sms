package net.oilchem.common;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-24
 * Time: 下午4:10
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionHandler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        e.printStackTrace();

        ModelAndView view = new ModelAndView(new MappingJacksonJsonView());
        view.addObject("stat",0);
        view.addObject("error",e.getMessage());
        view.addObject("data",new HashMap());
        return view;
//        try {
//            PrintWriter write = response.getWriter();
//            write.print("{\"stat\":0,\"error\":\""+e.getMessage()+"\",\"data\":{} }");
//            return null;
//        } catch (IOException e1) {
//            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
    }
}
