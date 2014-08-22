package com.rootls.common.utils;

import com.rootls.common.bean.Config;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-26
 * Time: 下午5:16
 * To change this template use File | Settings | File Templates.
 */
public class EHCacheUtil {

    public static int MAXELEMENTSINMEMORY = 300000;

    public synchronized static Cache initCache(String cacheName) {
        try {
            CacheManager myManager = CacheManager.create();
            Cache myCache = myManager.getCache(cacheName);
            if (myCache == null) {
                myCache = new Cache(
                        new CacheConfiguration(cacheName, MAXELEMENTSINMEMORY)
                                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
                                .overflowToDisk(false)
                                .eternal(false)
                                .timeToLiveSeconds(Config.inerCache_expire)
                                .timeToIdleSeconds(Config.inerCache_expire/2)
                                .diskPersistent(false)
                                .diskExpiryThreadIntervalSeconds(0));
                    myManager.addCache(myCache);

            }
            return myCache;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static <T> boolean  setValue(String cacheName, String key, T value) {
        Boolean flag = false;
        try {
            CacheManager myManager = CacheManager.create();
            Cache myCache = myManager.getCache(cacheName);
            if (myCache == null) {
                myCache = initCache(cacheName);
                //myManager.addCache(cacheName);
                //myCache=myManager.getCache(cacheName);
            }
            Element element = new Element(key, value);
//			element.updateAccessStatistics();
            myCache.put(element);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return flag;
    }

    public static <T> boolean setValue(String cacheName, String key, T value,
                                   Integer timeToLiveSeconds) {
        Boolean flag = false;
        try {
            CacheManager myManager = CacheManager.create();
            Cache myCache = myManager.getCache(cacheName);
            if (myCache == null) {
                initCache(cacheName);
                myCache = myManager.getCache(cacheName);
            }
            myCache.put(new Element(key, value, false,
                    150, timeToLiveSeconds));
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return flag;
    }

    public static <T> T getValue(String cacheName, String key) {
        try {
            CacheManager myManager = CacheManager.create();
            Cache myCache = myManager.getCache(cacheName);
            if (myCache == null) {
                myCache = initCache(cacheName);
            }
            Element element = myCache.get(key);
            if(element!=null){
                return (T)element.getValue();
            }else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean removeElment(String cacheName, String key) {
        Boolean flag = false;
        try {
            CacheManager myManager = CacheManager.create();
            Cache myCache = myManager.getCache(cacheName);
            if (myCache != null) {
                myCache.remove(key);
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return flag;
    }

    public static boolean removeAllEhcache(String cacheName) {
        Boolean flag = false;
        try {
            CacheManager myManager = CacheManager.create();
            myManager.removalAll();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return flag;
    }

}
