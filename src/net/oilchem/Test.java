package net.oilchem;

import net.oilchem.common.utils.JiamiJiemi;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-2-12
 * Time: 上午10:45
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    public static void main(String[] args) throws Exception {

//        URL url = new URL ("http://localhost:8080/user/userRegister.do?cell=13874605899&authCode=1935");
//
//        URLConnection conn = url.openConnection();
//
//        conn.setAllowUserInteraction(false);
//        conn.setDoOutput(true);
//
//        conn.addRequestProperty("sessionId", "c0b8995fa4a34dbe9fd7ed7821e67f2e");
//
//        InputStream response = conn.getInputStream();

//        System.out.println(Md5Util.generatePassword("13233302958"));
//
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd").parse("2013-02-20").getTime());
//
        System.out.println(Character.toString((char) 65));
        System.out.println(Character.toString((char) 97));
        System.out.println(Character.toString((char) 48));
        System.out.println(Character.toString((char) 57));
//
//        System.out.println((int) 'Z');

        String in = "AKSZ0349aDYz";
        System.out.println(in);
        //加密
        String out = JiamiJiemi.encode(in);
        System.out.println(out);
        //解密
        in = JiamiJiemi.decode(out);
        System.out.println(in);

    }



}
