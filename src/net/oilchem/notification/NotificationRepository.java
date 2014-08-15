package net.oilchem.notification;

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import net.oilchem.common.bean.Config;
import net.oilchem.sms.Sms;
import net.oilchem.sms.SmsRepository;
import net.oilchem.user.User;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static net.oilchem.notification.NotificationSmsPool.IOSDevice;

/**
 * Created by luowei on 2014/8/11.
 */
@Component
public class NotificationRepository extends JdbcDaoSupport {

    @Autowired
    SmsRepository smsRepository;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    void init() {
        setDataSource(dataSource);
    }

    private static NotificationSmsPool notifyPool;
    private static NotificationResult notifyResult;

    public NotificationRepository() {
        notifyPool = new NotificationSmsPool();
        notifyResult = new NotificationResult();
    }

    public static NotificationResult getNotifyResult() {
        if (notifyResult == null) {
            notifyResult = new NotificationResult();
        }
        return notifyResult;
    }

    //日期
    Date date = new Date();

    //定时器
    Timer timer = new Timer();

    //启动定时任务
    public void runTask() {
        logger.info("=========== start get push log task ===============");
        if (Config.iOSPushSwitchOpen) {
            timer.schedule(new TargetTask(), 0);
        }
    }

    //停止定时任务
    public void stopTask() {
        logger.info("=========== stop get push log task ==============");
        timer.cancel();
    }

    private class TargetTask extends TimerTask {
        @Override
        public void run() {

            try {
                pushNotification();
            } catch (Exception e) {
                e.printStackTrace();
            }

//            date.setTime((new Date().getTime()+Config.iOSPushIdle));
//            timer.purge();
            timer.schedule(new TargetTask(), Config.iOSPushIdle);

            logger.info("========== get push log ==============");

        }
    }

    public NotificationSmsPool getNotify() {
        if (notifyPool == null) {
            notifyPool = new NotificationSmsPool();
        }
        return notifyPool;
    }

    public void addAppleDevice(User user) {
        IOSDevice device = new IOSDevice(user.getImei(), user.getUsername());

        List<Sms> smsList = smsRepository.getIOSPushSmsList(user, null);
        updateLastPushTime();

        device.setSmsList(smsList);

        //把这个设备的信息添加到通知池中
        notifyPool.getMobileIOSDeviceMap().put(user.getUsername(), device);

    }


    private Long getNowtime() {
        SqlRowSet rowSet = getJdbcTemplate().queryForRowSet("select getdate() as nowTime");
        if (rowSet.next()) {
            return rowSet.getTimestamp("nowTime").getTime();
        }
        return new Date().getTime();
    }

    private void updateLastPushTime() {
        Config.lastPushTime = getNowtime();
        Config.setConfig("lastPushTime", String.valueOf(Config.lastPushTime), null);
    }


    public void setIOSDevice() {
        if (notifyPool != null && notifyPool.getMobileIOSDeviceMap() != null && !notifyPool.getMobileIOSDeviceMap().isEmpty()) {
            return;
        }

        //找出所有的设备构建一个设备池
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(BasicDataSource.class.cast(getCurrentWebApplicationContext().getBean("dataSource")));
        String sql = " select * from LZ_SMSUserMobile where User_push = 1 order by user_mobile asc,user_smsid desc";
        List<IOSDevice> devices = getJdbcTemplate().query(sql, new RowMapper<IOSDevice>() {
            @Override
            public IOSDevice mapRow(ResultSet rs, int i) throws SQLException {
                return new IOSDevice(
                        rs.getString("user_deviceToken"),
                        rs.getString("user_mobile")
                );
            }
        });
        Map<String, IOSDevice> mobileDeviceMap = new HashMap<String, IOSDevice>();
        for (IOSDevice device : devices) {
            if (!mobileDeviceMap.containsKey(device.getPhoneNumber())) {
                mobileDeviceMap.put(device.getPhoneNumber(), device);
            }
        }
        System.out.println("-------init NotificationSmsPool,iOSDevices size is:" + mobileDeviceMap.size());

        notifyPool.setMobileIOSDeviceMap(mobileDeviceMap);
    }

    public NotificationSmsPool setIOSDeviceSmsList(Map<String, List<Sms>> smsMap) {
        if (notifyPool.getMobileIOSDeviceMap() == null) {
            setIOSDevice();
        }
        for (String mobile : notifyPool.getMobileIOSDeviceMap().keySet()) {
            List<Sms> smsList = smsMap.get(mobile);
            if (smsList != null) {
                notifyPool.getMobileIOSDeviceMap().get(mobile).setSmsList(smsList);
            }
        }
        return notifyPool;
    }


    public static Long endTime = null;

    public synchronized void pushNotification() {
        try {
            setIOSDeviceSmsList(getSmsMap());

            PushNotificationManager pushManager = new PushNotificationManager();
            //true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl(notifyPool.getCertificatePath(), notifyPool.getCertificatePassword(), Config.push_production));
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();

            for (IOSDevice iosDevice : notifyPool.getMobileIOSDeviceMap().values()) {

                if (iosDevice.getSmsList() == null || iosDevice.getSmsList().get(0) != null) {
                    continue;
                }
                PushNotificationPayload payLoad = new PushNotificationPayload();
                payLoad.addAlert(iosDevice.getSmsList().get(0).getContent()); // 消息内容
                payLoad.addBadge(iosDevice.getBadge()); // iphone应用图标上小红圈上的数值

                payLoad.addCustomDictionary("startTime", Config.lastPushTime);
                endTime = getNowtime();
                payLoad.addCustomDictionary("endTime", endTime);

//                payLoad.addCustomDictionary("smsList", iosDevice.getSmsList());
                if (!StringUtils.isBlank(iosDevice.getSound())) {
                    payLoad.addSound(iosDevice.getSound());//铃音
                }

                // 发送push消息
                if (iosDevice.getSendFlag()) {
                    Device device = new BasicDevice(iosDevice.getDeviceToken());
                    PushedNotification notification = pushManager.sendNotification(device, payLoad, true);
                    notifications.add(notification);
                } else {
                    List<Device> device = new ArrayList<Device>();
                    device.add(new BasicDevice(iosDevice.getDeviceToken()));
                    notifications = pushManager.sendNotifications(payLoad, device);
                }

                List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
                List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);

                getNotifyResult().setFailedNotifications(failedNotifications);
                getNotifyResult().setSuccessfulNotifications(successfulNotifications);
//              int failed = failedNotifications.size();
//              int successful = successfulNotifications.size();

                if (failedNotifications.size() > 0) {
                    for (PushedNotification noti : failedNotifications) {
                        System.out.println("=====notify apple failed notification:" + new ObjectMapper().writeValueAsString(noti.getPayload().getPayload()));
                    }
                }

            }
            pushManager.stopConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得所有的推送信息
     */
    public Map<String, List<Sms>> getSmsMap() {

        List<Sms> smsList = smsRepository.getIOSPushSmsList(null, null);
        updateLastPushTime();

        Map<String, List<Sms>> smsMap = new HashMap<String, List<Sms>>();
        for (Sms sms : smsList) {
            if (!smsMap.containsKey(sms.getMobile())) {
                smsMap.get(sms.getMobile()).add(sms);
            } else {
                List<Sms> smses = new ArrayList<Sms>();
                smses.add(sms);
                smsMap.put(sms.getMobile(), smses);
            }
        }
        return smsMap;
    }

}
