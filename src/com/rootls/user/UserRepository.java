package com.rootls.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-23
 * Time: 下午5:12
 * To change this template use File | Settings | File Templates.
 */
@Component
public class UserRepository extends JdbcDaoSupport {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    void init() {
        setDataSource(dataSource);
    }

    @Resource(name = "pcdbJdbcTemplate")
    JdbcTemplate pcdbJdbcTemplate;


    public User findByUsernameAndPassword(String userName, String secret) {

        final String name = userName;
        final String pass = secret;
        List<User> users = getJdbcTemplate().query(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst = con.prepareStatement(
                                " select top 1 * from LZ_SMSUserMobile where user_mobile=? and User_PWD=? order by user_smsid desc ");
                        pst.setString(1, name);
                        pst.setString(2, pass);
                        return pst;
                    }
                },
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int i) throws SQLException {
                        User user = new User(
                                rs.getInt("user_smsid"),
                                rs.getString("user_mobile"),
                                rs.getString("User_PWD"),
                                rs.getString("User_accessToken"),
                                rs.getString("User_LastIP"),
                                rs.getTimestamp("User_LastTime"),
                                rs.getInt("User_LoginTimes")
                        );
                        user.setStopClient(rs.getInt("user_stopClient"));
                        user.setImei(rs.getString("User_IMEI"));
                        user.setRealName(rs.getString("user_mobilelxr"));
                        return user;
                    }
                }
        );
        if (users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    public int updateLoginInfo(User user) {
        String sql = " update LZ_SMSUserMobile set User_accessToken= '" + user.getAccessToken() + "' , User_LastIP='" + user.getLastIp() +
                "',User_IMEI='" + (user.getImei() == null ? "" : user.getImei()) + "' ," +
                "  User_LastTime=GETDATE(),User_LoginTimes=User_LoginTimes+1, user_ViewTime=GETDATE()  ,User_accessTokenbak=null " +
                " where user_mobile='" + user.getUsername() + "' ";
        return getJdbcTemplate().update(sql);
    }

    public int updateLoginInfo2(User user) {
        String sql = " update LZ_SMSUserMobile set User_accessToken= '" + user.getAccessToken() + "' , User_LastIP='" + user.getLastIp() +
                "',User_IMEI='" + (user.getImei() == null ? "" : user.getImei()) + "' ,User_IMEI_Count=User_IMEI_Count+1, " +
                "  User_LastTime=GETDATE(),User_LoginTimes=User_LoginTimes+1, user_ViewTime=GETDATE()  ,User_accessTokenbak=null " +
                " where user_mobile='" + user.getUsername() + "' ";
        return getJdbcTemplate().update(sql);
    }

    public int updateViewtime(User user) {
        String sql = " update LZ_SMSUserMobile set  user_ViewTime=GETDATE() " +
                " where user_mobile='" + user.getUsername() + "' ";
        return getJdbcTemplate().update(sql);
    }

    public int updateToken(User user) {
        String sql = " update LZ_SMSUserMobile set User_accessToken= '" + user.getAccessToken() + "'" +
                " where user_mobile='" + user.getUsername() + "' ";
        return getJdbcTemplate().update(sql);
    }

    public User findByAccessToken(String accessToken) {
        final String token = accessToken;
        List<User> users = getJdbcTemplate().query(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst = con.prepareStatement(
                                " select top 1 * from LZ_SMSUserMobile where User_accessToken=?  order by user_smsid desc ");
                        pst.setString(1, token);
                        return pst;
                    }
                },
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int i) throws SQLException {
                        User user = new User(
                                rs.getInt("user_smsid"),
                                rs.getString("user_mobile"),
                                rs.getString("User_PWD"),
                                rs.getString("User_accessToken"),
                                rs.getString("User_LastIP"),
                                rs.getTimestamp("User_LastTime"),
                                rs.getInt("User_LoginTimes")
                        );
                        user.setImei(rs.getString("User_IMEI"));
                        user.setStopClient(rs.getInt("user_stopClient"));
                        user.setRealName(rs.getString("user_mobilelxr"));
                        user.setAccessTokenbak(rs.getString("User_accessTokenbak"));
                        return user;
                    }
                }
        );
        if (users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    public int register(User user) {
        String sql = " insert into LZ_SMSUSerMobileReg(user_mobile,user_ip,user_regTime)  " +
                " values('" + user.getUsername() + "','" + user.getLastIp() + "',GETDATE()) ";
        return getJdbcTemplate().update(sql);
    }

    public int cleanAccessToken(User user) {
        String sql = " update LZ_SMSUserMobile set User_accessToken='', user_ViewTime=GETDATE()-1 where user_mobile='" + user.getUsername() + "' ";
        return getJdbcTemplate().update(sql);
    }

    public boolean exsitUser(User user) {
        String sql = " select count(*) from LZ_SMSUSerMobileReg where user_mobile='" + user.getUsername() + "' ";
        String mobileSql = " select count(*) from LZ_SMSUserMobile where user_mobile='" + user.getUsername() + "' ";
        return getJdbcTemplate().queryForInt(sql) > 0 || getJdbcTemplate().queryForInt(mobileSql) > 0;
    }


    public User findByAccessTokenBak(String accessToken) {
        final String token = accessToken;
        List<User> users = getJdbcTemplate().query(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst = con.prepareStatement(
                                " select top 1 * from LZ_SMSUserMobile where User_accessTokenbak=? order by user_smsid desc ");
                        pst.setString(1, token);
                        return pst;
                    }
                },
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int i) throws SQLException {
                        User user = new User(
                                rs.getInt("user_smsid"),
                                rs.getString("user_mobile"),
                                rs.getString("User_PWD"),
                                rs.getString("User_accessToken"),
                                rs.getString("User_LastIP"),
                                rs.getTimestamp("User_LastTime"),
                                rs.getInt("User_LoginTimes")
                        );
                        user.setImei(rs.getString("User_IMEI"));
                        user.setStopClient(rs.getInt("user_stopClient"));
                        user.setRealName(rs.getString("user_mobilelxr"));
                        user.setAccessTokenbak(rs.getString("User_accessTokenbak"));
                        return user;
                    }
                }
        );
        if (users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    public int updateAccessTokenbak(String accessToken) {
        String sql = " update LZ_SMSUserMobile set User_accessTokenbak=User_accessToken " +
                " where User_accessToken='" + accessToken + "' ";
        return getJdbcTemplate().update(sql);
    }

    public boolean exsitImei(User user) {
        String sql = " select count(*) from Lz_SMSUserMobile_IMEI " +
                " where user_mobile='" + user.getUsername() + "' and User_IMEI='" + user.getImei() + "' ";
        return getJdbcTemplate().queryForInt(sql) > 0;
    }

    public int addImei(User user, String ipAddr) {
        String sql = " insert into Lz_SMSUserMobile_IMEI(user_mobile,User_IMEI,addtime,addip) " +
                " values('" + user.getUsername() + "','" + user.getImei() + "',getdate(),'" + ipAddr + "' ) ";
        return getJdbcTemplate().update(sql);
    }

    public void updateApplePush(User user, String applePush) {
        String sql = "update LZ_SMSUserMobile set User_push=" + applePush + " where user_mobile='" + user.getUsername() + "'";
        getJdbcTemplate().update(sql);
    }

    public void updateDeviceToken(String username, String deviceToken) {
        String sql = "update LZ_SMSUserMobile set user_deviceToken='"+deviceToken+"' where user_mobile='"+username+"' ";
        getJdbcTemplate().update(sql);
    }
}
