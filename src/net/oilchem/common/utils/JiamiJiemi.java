package net.oilchem.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-3-3
 * Time: 下午2:38
 * To change this template use File | Settings | File Templates.
 */
public class JiamiJiemi {

    /**
     * 加密方法
     * @param in
     * @return
     */
    public static String jiami(String in) {
        if(in==null){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return encode(in + sdf.format(new Date()));
    }

    /**
     * 解密方法
     * @param out
     * @return
     */
    public static String jiemi(String out) {
        if(out==null){
            return null;
        }
        return decode(out.substring(8,out.length()));
    }

    /**
     * 根据传入的加密字符串获得日期
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date getJiemiDate(String str) throws ParseException {
        if(str==null){
            return null;
        }
        return new SimpleDateFormat("yyyyMMdd").parse(decode(str).substring(0, 8));
    }


    public static String encode(String in) {
        if(in==null){
            return null;
        }
        String out = "";
        for (int i = in.length() - 1; i >= 0; i--) {
            if (in.charAt(i) > 57 && in.charAt(i) <= 65) {
                out = out + "Z";
            } else if (in.charAt(i) > 90 && in.charAt(i) <= 97) {
                out = out + "z";
            } else if (in.charAt(i) <= 48) {
                out = out + "9";
            } else {
                out = out + Character.toString((char) (in.charAt(i) - 1));
            }

        }
        return out;
    }

    public static String decode(String out) {
        if(out==null){
            return null;
        }
        String in;
        in = "";
        for (int i = out.length() - 1; i >= 0; i--) {
            if (out.charAt(i) >= 57 && out.charAt(i) < 65) {
                in = in + "0";
            } else if (out.charAt(i) >= 90 && out.charAt(i) < 97) {
                in = in + "A";
            } else if (out.charAt(i) >= 122) {
                in = in + "a";
            } else {
                in = in + Character.toString((char) (out.charAt(i) + 1));
            }

        }
        return in;
    }


    public static void main(String[] args){
        String in = "AKSZ0349aDYz";
        System.out.println(in);
        //加密
        String out = JiamiJiemi.jiami(in);
        System.out.println(out);
        //解密
        in = JiamiJiemi.jiemi(out);
        System.out.println(in);
    }
}
