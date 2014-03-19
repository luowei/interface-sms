package net.oilchem.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-3-19
 * Time: 上午10:21
 * To change this template use File | Settings | File Templates.
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    public RequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public String[] getParameterValues(String parameter) {

        System.out.println("Trimming parameters...");
        String[] results = super.getParameterValues(parameter);

        if (results == null) {
            return null;
        }

        int count = results.length;

        String[] trimResults = new String[count];

        for (int i = 0; i < count; i++) {
            trimResults[i] = results[i].trim();
            trimResults[i] = trimResults[i].replace("<", "");
            trimResults[i] = trimResults[i].replace(">", "");
            trimResults[i] = trimResults[i].replace("'", "");
        }


        return trimResults;
    }

    public Object getAttribute(String name) {
        Object val = super.getAttribute(name);
        if(val!=null && val instanceof String ){
            String value = String.valueOf(val).trim().replace("'","");
            return value;
        }
        return super.getAttribute(name);
    }
}
