package net.oilchem.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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


    public User findByUsernameAndPassword(String userName, String secret) {

        final String name = userName;
        final String pass = secret;
        List<User> users = getJdbcTemplate().query(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst = con.prepareStatement(
                                " select * from LZ_SMSUserMobile where user_mobile=? and User_PWD=? ");
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
                "' , User_LastTime==GETDATE(),User_LoginTimes=User_LoginTimes+1 " +
                " where user_mobile='" + user.getUsername() + "' and User_PWD='" + user.getPassword() + "' ";
        return getJdbcTemplate().update(sql);
    }

    public User findByAccessToken(String accessToken) {
        final String token = accessToken;
        List<User> users = getJdbcTemplate().query(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst = con.prepareStatement(
                                " select * from LZ_SMSUserMobile where User_accessToken=?  ");
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
                        return user;
                    }
                }
        );
        if (users.size() > 0) {
            return users.get(0);
        }
        return null;
    }
}
