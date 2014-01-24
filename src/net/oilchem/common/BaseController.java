package net.oilchem.common;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-2-22
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
public class BaseController {

    public static String FORMAT =  "{\"stat\":%s,\"error\":\"%s\",\"data\":{ %s } }";

    Integer getTotalPage(Integer pageSize,Integer totalCount){
        if(pageSize!=null && !pageSize.equals(0)){
            if(totalCount%pageSize==0){
                return totalCount / pageSize;
            }else {
                return totalCount / pageSize+1;
            }
        }
        return 0;
    }

    public static String getIpAddr(HttpServletRequest request/* ,@RequestHeader MultiValueMap<String,String> headers*/) {
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

    private static boolean isNullIP(String ipAddress) {
        return ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress);
    }

    private static boolean isIPAddress(String str1) {
        if (str1 == null || str1.trim().length() < 7 || str1.trim().length() > 15) {
            return false;
        } else {
            return true;
        }
    }

}
