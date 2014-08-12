package net.oilchem.notification;

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import net.oilchem.common.bean.Config;
import net.oilchem.sms.Sms;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
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

    //日期
    Date date = new Date();

    //定时器
    Timer timer = new Timer();

    //启动定时任务
    public void runTask() {
        logger.info("=========== start get call log task ===============");
        if(Config.iOSPushSwitchOpen) {
            timer.schedule(new TargetTask(), 0);
        }
    }
    //停止定时任务
    public void stopTask() {
        logger.info("=========== stop get call log task ==============");
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

//            date.setTime((new Date().getTime()+Config.three_minute));
//            timer.purge();
            timer.schedule(new TargetTask(), Config.three_minute);

            logger.info("========== get call log ==============");

        }
    }

    public NotificationSmsPool getNotify() {
        if (notifyPool == null) {
            notifyPool = new NotificationSmsPool();
        }
        return notifyPool;
    }

    public void setIOSDevice() {
        if (notifyPool != null && notifyPool.getiOSDevices() != null && !notifyPool.getiOSDevices().isEmpty()) {
            return;
        }

//        JdbcTemplate jdbcTemplate = new JdbcTemplate(BasicDataSource.class.cast(getCurrentWebApplicationContext().getBean("dataSource")));
        String sql = " select * from LZ_SMSUserMobile where User_push = 1 order by user_mobile asc,user_smsid desc";
        List<IOSDevice> devices = getJdbcTemplate().query(sql, new RowMapper<IOSDevice>() {
            @Override
            public IOSDevice mapRow(ResultSet rs, int i) throws SQLException {
                return new IOSDevice(
                        rs.getString("User_IMEI"),
                        rs.getString("user_mobile")
                );
            }
        });
        List<IOSDevice> iOSDevices = new ArrayList<IOSDevice>();
        Set<String> mobileSet = new HashSet<String>();
        for (IOSDevice device : devices) {
            if (!mobileSet.contains(device.getPhoneNumber())) {
                mobileSet.add(device.getPhoneNumber());
                iOSDevices.add(device);
            }
        }
        System.out.println("-------init NotificationSmsPool,iOSDevices size is:" + iOSDevices.size());

        notifyPool.setiOSDevices(iOSDevices);
    }

    public NotificationSmsPool setIOSDeviceSmsList(Map<String, List<Sms>> smsMap) {
        if (notifyPool.getiOSDevices() == null) {
            setIOSDevice();
        }
        for (IOSDevice iosDevice : notifyPool.getiOSDevices()) {
            List<Sms> smsList = smsMap.get(iosDevice.getPhoneNumber());
            if (smsList != null) {
                iosDevice.setSmsList(smsList);
            }
        }
        return notifyPool;
    }

    public Map<String, List<Sms>> getSmsMap() {
        String sql = "select sms_id,sms_phone,sms_sendMsg_ID,sms_time,sms_message,sms_GroupId FROM ET_sms " +
                "where sms_phone in (select user_mobile from LZ_SMSUSerMobile where user_push=1) order by sms_phone";
        List<Sms> smsList = getJdbcTemplate().query(sql, new RowMapper<Sms>() {
            @Override
            public Sms mapRow(ResultSet resultSet, int i) throws SQLException {
                return null;
            }
        });
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

    public static NotificationResult getNotifyResult() {
        if (notifyResult == null) {
            notifyResult = new NotificationResult();
        }
        return notifyResult;
    }


    public void pushNotification() {
        try {
            setIOSDeviceSmsList(getSmsMap());

            PushNotificationManager pushManager = new PushNotificationManager();
            //true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl(notifyPool.getCertificatePath(), notifyPool.getCertificatePassword(), false));
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();

            for (IOSDevice iosDevice : notifyPool.getiOSDevices()) {

                if (iosDevice.getSmsList() == null || iosDevice.getSmsList().get(0) != null) {
                    continue;
                }
                PushNotificationPayload payLoad = new PushNotificationPayload();
                payLoad.addAlert(iosDevice.getSmsList().get(0).getContent()); // 消息内容
                payLoad.addBadge(iosDevice.getBadge()); // iphone应用图标上小红圈上的数值
                payLoad.addCustomDictionary("smsList", iosDevice.getSmsList());
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

}
