package net.oilchem.notification;

import net.oilchem.common.bean.Config;
import net.oilchem.sms.Sms;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by luowei on 2014/8/11.
 */
public class NotificationSmsPool implements Serializable {

    Map<String,IOSDevice> mobileIOSDeviceMap;

    String certificatePath;
    String certificatePassword;

    public NotificationSmsPool() {
        certificatePath = Config.certificatePath;
        certificatePassword = Config.certificatePassword;
    }

    public static class IOSDevice implements Serializable{
        String deviceToken;
        Boolean sendFlag;
        String sound;

        String phoneNumber;
        Integer badge;
        List<Sms> smsList;

        public IOSDevice() {
        }

        public IOSDevice(String deviceToken, String phoneNumber) {
            this.deviceToken = deviceToken;
            this.phoneNumber = phoneNumber;
        }

        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        public Integer getBadge() {
            if(smsList!=null){
                badge = smsList.size();
            }else {
                badge = 0;
            }
            return badge;
        }

        public void setBadge(Integer badge) {
            this.badge = badge;
        }

        public List<Sms> getSmsList() {
            return smsList;
        }

        public void setSmsList(List<Sms> smsList) {
            this.smsList = smsList;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public Boolean getSendFlag() {
            if(sendFlag==null){
                sendFlag = Config.sendFlag;
            }
            return sendFlag;
        }

        public void setSendFlag(Boolean sendFlag) {
            this.sendFlag = sendFlag;
        }

        public String getSound() {
            if(isBlank(sound)){
                sound = Config.sound;
            }
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }
    }

    public Map<String, IOSDevice> getMobileIOSDeviceMap() {
        return mobileIOSDeviceMap;
    }

    public void setMobileIOSDeviceMap(Map<String, IOSDevice> mobileIOSDeviceMap) {
        this.mobileIOSDeviceMap = mobileIOSDeviceMap;
    }

    public String getCertificatePath() {
        if(isBlank(certificatePath)){
            certificatePath = Config.certificatePath;
        }
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public String getCertificatePassword() {
        if(isBlank(certificatePassword)){
            certificatePassword = Config.certificatePassword;
        }
        return certificatePassword;
    }

    public void setCertificatePassword(String certificatePassword) {
        this.certificatePassword = certificatePassword;
    }
}
