package net.oilchem.user;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 13-2-22
 * Time: 上午9:58
 * To change this template use File | Settings | File Templates.
 */
public class User implements Serializable {

    Integer id;
    String username;
    String password;
    String accessToken;
    String Imei;

    String ipRight;        //ip权限
    String lastIp;         //上次登录ip
    Date lasttime;         //上次登录时间
    Integer loginTimes;   //登录次数


    Date startDate;       //到期起始日
    Date endDate;         //到期结束日

    Integer stopClient;

    public User() {
    }

    public User(String username, String lastIp) {
        this.username = username;
        this.lastIp = lastIp;
    }

    public User(Integer id, String username, String password, String accessToken,
                String lastIp, Date lasttime, Integer loginTimes) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.accessToken = accessToken;
        this.lastIp = lastIp;
        this.lasttime = lasttime;
        this.loginTimes = loginTimes;
    }

    public Integer getId() {
        return id;
    }

    public User setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getImei() {
        return Imei;
    }

    public void setImei(String imei) {
        Imei = imei;
    }

    public String getIpRight() {
        return ipRight;
    }

    public User setIpRight(String ipRight) {
        this.ipRight = ipRight;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public User setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public User setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getLastIp() {
        return lastIp;
    }

    public User setLastIp(String lastIp) {
        this.lastIp = lastIp;
        return this;
    }

    public Date getLasttime() {
        return lasttime;
    }

    public User setLasttime(Date lasttime) {
        this.lasttime = lasttime;
        return this;
    }

    public Integer getLoginTimes() {
        return loginTimes;
    }

    public User setLoginTimes(Integer loginTimes) {
        this.loginTimes = loginTimes;
        return this;
    }

    public Integer getStopClient() {
        return stopClient;
    }

    public void setStopClient(Integer stopClient) {
        this.stopClient = stopClient;
    }
}
