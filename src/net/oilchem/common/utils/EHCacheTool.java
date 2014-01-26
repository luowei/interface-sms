package net.oilchem.common.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-26
 * Time: 下午5:16
 * To change this template use File | Settings | File Templates.
 */
public class EHCacheTool {

    static Cache cache = null;

    public static  <T>  boolean  setValue(String key, T value) {
        Boolean flag = false;
        try {
            if (cache == null) {
                CacheManager myManager = CacheManager.create();
                cache = myManager.getCache("defaut");
                cache.put(new Element(key, value));
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return flag;
    }

    public static <T> T getValue(String key) {
        try {
            if (cache == null) {
                return null;
            }
            return (T)cache.get(key).getValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String getConfigPath() {
        String configFilePath = EHCacheTool.class.getClassLoader().getResource("ehcache.xml").getPath().substring(1);
        // 判断系统 linux，windows
        if ("\\".equals(File.separator)) {
            configFilePath = configFilePath.replace("%20", " ");
        } else if ("/".equals(File.separator)) {
            configFilePath = "/" + configFilePath.replace("%20", " ");
        }
        return configFilePath;
    }

}
