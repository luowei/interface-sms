package com.rootls.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-2-25
 * Time: 下午12:46
 * To change this template use File | Settings | File Templates.
 */
public class IPMacUtil {

    /**
     * 验证requestIp是否匹配bindIp，匹配返回true，否则返回false
     * @param bindIp 绑定的IP
     * @param requestIp 待验证的IP，即登录IP
     * @return
     * @throws Exception
     */
    public static boolean validBindIp(String bindIp, String requestIp){
        //无绑定IP，则所有IP都是正确的。
        if (StringUtils.isEmpty(bindIp)) {
            return true;
        }

        if (StringUtils.isEmpty(requestIp)) {
            return false;
        }
        //是否绑定的标志，true为2个IP匹配，false为不匹配。
        boolean validBindIp = true;
        //split 函数需要的参数是正则表达式，【.】在正则表达式有特殊含义，所以要转义。
        String[] bindIpSplits = bindIp.split("\\.");
        String[] requestIpSplits = requestIp.split("\\.");
        int bindIpSplitsLength = bindIpSplits.length;
        int requestIpSplitsLength = requestIpSplits.length;
        int minLength = bindIpSplitsLength < requestIpSplitsLength ? bindIpSplitsLength : requestIpSplitsLength;

        for (int i = 0; i < minLength; i++) {
            //标志为false，则跳出循环。
            if (!validBindIp) {
                break;
            }
            //"*"匹配所有
            if (bindIpSplits[i].indexOf("*") != -1) {
                continue;
            }
            //对应的值是否相等。如果不等，则绑定标志置为false。
            if (!requestIpSplits[i].equals(bindIpSplits[i])) {
                validBindIp = false;
            }
        }

        return validBindIp;
    }

    public static boolean validBindIp(String[] bindIps, String requestIp) {
        if (bindIps == null || bindIps.length == 0) {
            return true;
        }
        if (StringUtils.isEmpty(requestIp)) {
            return false;
        }
        boolean validBindIp = false;
        for (String bindIp : bindIps) {
            //如果有一个符合，则比较完成，退出循环，返回true。
            if (validBindIp) {
                break;
            }
            validBindIp = IPMacUtil.validBindIp(bindIp, requestIp);
        }
        return validBindIp;
    }

    public static boolean validBindIp(String bindIpsWithSeparator, String requestIp, String separator){
        if (StringUtils.isEmpty(bindIpsWithSeparator)) {
            return true;
        }
        if (StringUtils.isEmpty(requestIp)) {
            return false;
        }
        if (StringUtils.isEmpty(separator)) {
            separator = ",";
        }
        String[] bindIps = bindIpsWithSeparator.split(separator);
        return IPMacUtil.validBindIp(bindIps, requestIp);
    }


    //---------------------  macth  mac  address  ---------------------
    /**
     * 验证requestMac是否匹配bindMac，若匹配返回true，否则返回false
     * @param bindMac 绑定的物理地址
     * @param requestMac 待验证的物理地址
     * @return
     * @throws Exception
     */
    public static boolean validBindMac(String bindMac, String requestMac){
        //无绑定MAC，则所有MAC都是正确的。
        if (StringUtils.isEmpty(bindMac)) {
            return true;
        }

        if (StringUtils.isEmpty(requestMac)) {
            return false;
        }
        //是否绑定的标志，true为2个MAC匹配，false为不匹配。
        boolean validBindMac = true;

        String[] bindMacSplits = bindMac.split("-");
        String[] requestMacSplits = requestMac.split("-");
        int bindMacSplitsLength = bindMacSplits.length;
        int requestMacSplitsLength = requestMacSplits.length;
        int minLength = bindMacSplitsLength < requestMacSplitsLength ? bindMacSplitsLength : requestMacSplitsLength;

        for (int i = 0; i < minLength; i++) {
            //标志为false，则跳出循环。
            if (!validBindMac) {
                break;
            }
            //"*"匹配所有
            if (bindMacSplits[i].indexOf("*") != -1) {
                continue;
            }
            //对应的值是否相等。如果不等，则绑定标志置为false。
            if (!requestMacSplits[i].equalsIgnoreCase(bindMacSplits[i])) {
                validBindMac = false;
            }
        }

        return validBindMac;
    }

    public static boolean validBindMac(String[] bindMacs, String requestMac){
        if (bindMacs == null || bindMacs.length == 0) {
            return true;
        }
        if (StringUtils.isEmpty(requestMac)) {
            return false;
        }
        boolean validBindMac = false;
        for (String bindMac : bindMacs) {
            //如果有一个符合，则比较完成，退出循环，返回true。
            if (validBindMac) {
                break;
            }
            validBindMac = IPMacUtil.validBindMac(bindMac, requestMac);
        }
        return validBindMac;
    }

    public static boolean validBindMac(String bindMacsWithSeparator, String requestMac, String separator){
        if (StringUtils.isEmpty(bindMacsWithSeparator)) {
            return true;
        }
        if (StringUtils.isEmpty(requestMac)) {
            return false;
        }
        if (StringUtils.isEmpty(separator)) {
            separator = ",";
        }
        String[] bindMacs = bindMacsWithSeparator.split(separator);
        return IPMacUtil.validBindMac(bindMacs, requestMac);
    }

}
