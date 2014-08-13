package net.oilchem.common.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-1-11
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */
public class Config implements ServletContextListener {



    public static String properties_fileName = getConfigPath();
    public static Integer session_max_interval = 300;
    public static String json_format =  "{\"stat\":\"%s\",\"error\":\"%s\",\"data\":{ %s } }";
    public static String latestAppVersion = "1.0";
    public static String customerServicename = "隆众客服";
    public static String customerServiceNumber = "0533-2591688";
    public static String registrationNumber = "400-658-1688";
    public static Integer pageSizeWhileSearchingLocalSMS = 10;
    public static String appDownload = "http://www.oilchem.net/download/android/sms.apk";
    public static Integer getPushTimeInterval = 60;

    public static String login_success = "登录成功";
    public static String login_faild = "用户名或密码错误";
    public static String authentication_faild = "验证失败";
    public static String login_stop = "账号已停用";

    public static String global_groups = "";

    public static Long inerCache_expire = 24 * 3600 * 1000L;

    public static Boolean sendFlag = true;

    public static String sound = "default";

    public static String certificatePath = "certificate.p12";

    public static String certificatePassword = "11223344";
    public static Long three_minute = 3*60*1000L;
    public static boolean iOSPushSwitchOpen = true;
    public static boolean push_production=true;
    public static Long lastPushTime=0L;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {


        Map<String, String> map = PropertiesUtil.getAllProperty();
        if (map == null || map.isEmpty()) {
            return;
        }

        Integer _session_max_interval = Integer.valueOf(map.get("session_max_interval"));
        session_max_interval = (_session_max_interval != null && _session_max_interval >= 0) ? _session_max_interval : session_max_interval;

        String _json_format = map.get("json_format");
        json_format = (_json_format != null && _json_format !="")?_json_format:json_format;

        String _latestAppVersion = map.get("latestAppVersion");
        latestAppVersion = (_latestAppVersion != null && _latestAppVersion !="")?_latestAppVersion:latestAppVersion;

        String _customerServicename = map.get("customerServicename");
        customerServicename = (_customerServicename != null && _customerServicename !="")?_customerServicename:customerServicename;

        String _customerServiceNumber = map.get("customerServiceNumber");
        customerServiceNumber = (_customerServiceNumber != null && _customerServiceNumber !="")?_customerServiceNumber:customerServiceNumber;

        String _registrationNumber = map.get("registrationNumber");
        registrationNumber = (_registrationNumber != null && _registrationNumber !="")?_registrationNumber:registrationNumber;

        Integer _pageSizeWhileSearchingLocalSMS = Integer.valueOf(map.get("pageSizeWhileSearchingLocalSMS"));
        pageSizeWhileSearchingLocalSMS = (_pageSizeWhileSearchingLocalSMS != null && _pageSizeWhileSearchingLocalSMS >= 0) ? _pageSizeWhileSearchingLocalSMS : pageSizeWhileSearchingLocalSMS;

        String _appDownload = map.get("appDownload");
        appDownload = (_appDownload != null && _appDownload !="")?_appDownload:appDownload;

        String _global_groups = map.get("global_groups");
        global_groups = (_global_groups != null && _global_groups !="")?_global_groups:global_groups;

        Integer _getPushTimeInterval = Integer.valueOf(map.get("getPushTimeInterval"));
        getPushTimeInterval = (_getPushTimeInterval != null && _getPushTimeInterval >= 0) ? _getPushTimeInterval : getPushTimeInterval;

        String _login_success = map.get("login_success");
        login_success = (_login_success != null && _login_success !="")?_login_success:login_success;

        String _login_faild = map.get("login_faild");
        login_faild = (_login_faild != null && _login_faild !="")?_login_faild:login_faild;

        String _authentication_faild = map.get("authentication_faild");
        authentication_faild = (_authentication_faild != null && _authentication_faild !="")?_authentication_faild:authentication_faild;

        String _login_stop = map.get("login_stop");
        login_stop = (_login_stop != null && _login_stop !="")?_login_stop:login_stop;

        Long _inerCache_expire = Long.valueOf(map.get("inerCache_expire"));
        inerCache_expire = (_inerCache_expire != null && _inerCache_expire >= 0) ? _inerCache_expire : inerCache_expire;

        Boolean _sendFlag = Boolean.valueOf(map.get("sendFlag"));
        sendFlag = (_sendFlag != null)?_sendFlag:sendFlag;

        String _sound = map.get("sound");
        sound = (_sound != null && _sound !="")?_sound:sound;

        String _certificatePath = map.get("certificatePath");
        certificatePath = (_certificatePath != null && _certificatePath !="")?getConfigPath(_certificatePath):getConfigPath(certificatePath);

        String _certificatePassword = map.get("certificatePassword");
        certificatePassword = (_certificatePassword != null && _certificatePassword !="")?_certificatePassword:certificatePassword;

        Long _three_minute = Long.valueOf(map.get("three_minute"));
        three_minute = (_three_minute != null && _three_minute >= 0) ? _three_minute : three_minute;

        Boolean _iOSPushSwitchOpen = Boolean.valueOf(map.get("iOSPushSwitchOpen"));
        iOSPushSwitchOpen = (_iOSPushSwitchOpen != null) ? _iOSPushSwitchOpen : iOSPushSwitchOpen;

        Boolean _push_production = Boolean.valueOf(map.get("push_production"));
        push_production = (_push_production != null) ? _push_production : push_production;

        Long _lastPushTime = Long.valueOf(map.get("lastPushTime"));
        lastPushTime = (_lastPushTime != null && _lastPushTime >= 0) ? _three_minute : lastPushTime;
    }

    public static void setConfig(String key,String value,String comment){
        PropertiesUtil.writeProperties(key,value,comment);
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

    private String getConfigPath(String fileName) {
        String configFilePath = Config.class.getClassLoader().getResource(fileName).getPath().substring(1);
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
                InputStreamReader is = new InputStreamReader(fis,"UTF-8");
                props.load(is);
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
                map.put(key, (value!=null?value.trim():"") );
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
        public static Boolean writeProperties(String key, String value,String comment) {
            OutputStream fos = null;
            try {
                fos = new FileOutputStream(fileName);
                props.setProperty(key, value);
                // 将此 Properties 表中的属性列表（键和元素对）写入输出流
                if(comment!=null) {
                    props.store(fos, comment + " \n " + key);
                }else{
                    props.store(fos,"");
                }
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
