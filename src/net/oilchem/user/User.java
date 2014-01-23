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

    String ipRight;        //ip权限
    String lastIp;         //上次登录ip
    Date lasttime;         //上次登录时间
    Integer loginTimes;   //登录次数
    Integer priceFlag;    //0表示用没有权限查看价格，表示用户有权限查看价格

    Date startDate;       //用户可查看数据的启始时间
    Date endDate;         //用户可查看数据的结束时间

    String startYearMonth;  //用户可查看进出口数据的启始年月
    String endYearMonth;     //用户可查看进出口数据的结束年月

    Integer configId;

    public User() {
    }

    public User(Integer id, Integer configId, String username, String password,
                String ipRight, String lastIp, Date lasttime,
                Integer loginTimes, Integer priceFlag, Date startDate, Date endDate) {
        this.id = id;
        this.configId = configId;
        this.username = username;
        this.password = password;
        this.ipRight = ipRight;
        this.lastIp = lastIp;
        this.lasttime = lasttime;
        this.loginTimes = loginTimes;
        this.priceFlag = priceFlag;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public Integer getPriceFlag() {
        return priceFlag;
    }

    public User setPriceFlag(Integer priceFlag) {
        this.priceFlag = priceFlag;
        return this;
    }

    public String getStartYearMonth() {
        return startYearMonth;
    }

    public User setStartYearMonth(String startYearMonth) {
        this.startYearMonth = startYearMonth;
        return this;
    }

    public String getEndYearMonth() {
        return endYearMonth;
    }

    public User setEndYearMonth(String endYearMonth) {
        this.endYearMonth = endYearMonth;
        return this;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }
}
