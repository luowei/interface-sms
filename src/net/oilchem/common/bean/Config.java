package net.oilchem.common.bean;

import com.jolbox.bonecp.BoneCPDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.util.*;

import static org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-1-11
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */
public class Config implements ServletContextListener {

    public static JdbcTemplate jdbcTemplate;

    public static Map<String,Long> loginCache;
    public static Integer PAGE_SIZE = 20;
    public static Integer PAGEBAR_SIZE = 10;

    public static String properties_fileName = getConfigPath();
    public static Integer SESSION_MAX_INTERVAL = 300;
//    public static String JSON_MSG = "{\"code\":%d,\"msg\":\"%s\"}";
    public static String JSON_MSG = null;
    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static Integer FAILD_RET_CODE = 0;
    public static String NODATA_RET_MSG = " no data !";
    public static Integer DEFAULT_PAGESIZE = 5000;
    public static Integer DEFAULT_CURRENT = 1;
    public static long LOGIN_IDLE;

//    public static String UP = "up";
//    public static String DOWN = "down";
//
//    public static Integer PAGE_SIZE = 20;
//    public static Integer PAGEBAR_SIZE = 10;


    public static JdbcTemplate getJdbcTemplate(){
        if(jdbcTemplate==null){
            jdbcTemplate = new JdbcTemplate(BoneCPDataSource.class.cast(getCurrentWebApplicationContext().getBean("interfaceDataSource")));
        }
        return jdbcTemplate;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        loginCache = new HashMap<String, Long>();

        Map<String, String> map = PropertiesUtil.getAllProperty();
        if (map == null || map.isEmpty()) {
            return;
        }

        Integer session_max_interval = Integer.valueOf(map.get("SESSION_MAX_INTERVAL"));
        SESSION_MAX_INTERVAL = (session_max_interval != null && session_max_interval.intValue() >= 0) ? session_max_interval : SESSION_MAX_INTERVAL;

        String json_msg = map.get("JSON_MSG");
        JSON_MSG = (json_msg != null && json_msg !="")?json_msg:JSON_MSG;

        String date_format = map.get("DATE_FORMAT");
        DATE_FORMAT = (date_format != null && date_format !="")?date_format:DATE_FORMAT;

        Integer faild_ret_code = Integer.valueOf(map.get("FAILD_RET_CODE"));
        FAILD_RET_CODE = (faild_ret_code != null) ? faild_ret_code : FAILD_RET_CODE;

        String nodata_ret_msg = map.get("NODATA_RET_MSG");
        NODATA_RET_MSG = (nodata_ret_msg != null && nodata_ret_msg !="")?nodata_ret_msg:NODATA_RET_MSG;

        Integer default_pagesize = Integer.valueOf(map.get("DEFAULT_PAGESIZE"));
        DEFAULT_PAGESIZE = (default_pagesize != null) ? default_pagesize : DEFAULT_PAGESIZE;

        Integer default_current = Integer.valueOf(map.get("DEFAULT_CURRENT"));
        DEFAULT_CURRENT = (default_current != null) ? default_current : DEFAULT_CURRENT;

        Integer login_idle = Integer.valueOf(map.get("LOGIN_IDLE"));
        LOGIN_IDLE = (login_idle != null) ? login_idle : LOGIN_IDLE;

//        String up = map.get("UP");
//        UP = (up != null && up != "") ? up : UP;
//
//        String down = map.get("DOWN");
//        DOWN = (down != null && down != "") ? down : DOWN;
//
//        Integer page_size = Integer.valueOf(map.get("PAGE_SIZE"));
//        PAGE_SIZE = (page_size != null && page_size.intValue() >= 0) ? page_size : PAGE_SIZE;
//
//        Integer pagebar_size = Integer.valueOf(map.get("PAGEBAR_SIZE"));
//        PAGEBAR_SIZE = (pagebar_size != null && pagebar_size.intValue() >= 0) ? pagebar_size : PAGEBAR_SIZE;
    }


    private static String getConfigPath() {
        String configFilePath = Config.class.getClassLoader().getResource("config.properties").getPath().substring(1);
        // 判断系统 linux，windows
        if ("\\".equals(File.separator)) {
            configFilePath = configFilePath.replace("%20", " ");
        } else if ("/".equals(File.separator)) {
            configFilePath = "/" + configFilePath.replace("%20", " ");
        }
        return configFilePath;
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    /**
     * 读取properties配置文件.
     * User: luowei
     * Date: 13-2-5
     * Time: 下午2:39
     * To change this template use File | Settings | File Templates.
     */
    public abstract static class PropertiesUtil {

        static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

        private static Properties props;
        private static String fileName = properties_fileName;

        private static void readProperties() {
            FileInputStream fis = null;
            try {
                if (props == null) {
                    props = new Properties();
                }
                fis = new FileInputStream(fileName);
                props.load(fis);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }

        /**
         * 获取某个属性
         */
        public static String getProperty(String key) {
            readProperties();
            return props.getProperty(key);
        }

        /**
         * 获取所有属性，返回一个map,不常用
         * 可以试试props.putAll(t)
         */
        public static Map<String, String> getAllProperty() {
            Map<String, String> map = new HashMap<String, String>();
            readProperties();
            Enumeration enu = props.propertyNames();
            while (enu.hasMoreElements()) {
                String key = (String) enu.nextElement();
                String value = props.getProperty(key);
                map.put(key, value);
            }
            return map;
        }

        /**
         * 在控制台上打印出所有属性，调试时用。
         */
        public static void printProperties() {
            props.list(System.out);
        }

        /**
         * 写入properties信息
         */
        public static Boolean writeProperties(String key, String value) {
            OutputStream fos = null;
            try {
                fos = new FileOutputStream(fileName);
                props.setProperty(key, value);
                // 将此 Properties 表中的属性列表（键和元素对）写入输出流
                props.store(fos, "『comments』Update key：" + key);
                return true;
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return false;
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
        //    public static void main(String[] args) {
        //        PropertiesUtil util=new PropertiesUtil("config.properties");
        //        System.out.println("ip=" + util.getProperty("ip"));
        //        util.writeProperties("key", "value0");
        //    }
    }
}
