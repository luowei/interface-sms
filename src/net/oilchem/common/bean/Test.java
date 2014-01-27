package net.oilchem.common.bean;

import java.io.IOException;
import java.util.UUID;

import static net.oilchem.common.utils.Md5Util.generatePassword;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-24
 * Time: 下午4:54
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    public static void main(String[] args) throws IOException {

//        Map map = new HashMap();
//
//        map.put(1,"aaaaaa");
//        map.put(2,"bbbbb");
//
//        ObjectMapper om = new ObjectMapper();
//        System.out.println(om.writeValueAsString(map));

        System.out.println(generatePassword("13233302958"));

        System.out.println("============:"+ UUID.randomUUID().toString().replace("-",""));

    }

}
