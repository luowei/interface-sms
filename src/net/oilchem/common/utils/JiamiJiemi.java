package net.oilchem.common.utils;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-3-3
 * Time: 下午2:38
 * To change this template use File | Settings | File Templates.
 */
public class JiamiJiemi {

    public static String encode(String in) {
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

}
