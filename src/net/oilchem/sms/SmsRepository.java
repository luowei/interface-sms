package net.oilchem.sms;

import net.oilchem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-24
 * Time: 上午8:55
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SmsRepository extends JdbcDaoSupport {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    void init() {
        setDataSource(dataSource);
    }


    public List<Sms> list(User user) {
        String sql = " select sms_id,sms_time,sms_message,sms_GroupId from ET_sms where sms_phone='" + user.getUsername() + "'";
        getJdbcTemplate().query(sql,
                new RowMapper<Sms>() {
                    @Override
                    public Sms mapRow(ResultSet rs, int i) throws SQLException {
                        Sms sms = new Sms(
                                rs.getInt("sms_id"),
                                rs.getTimestamp("sms_time"),
                                rs.getString("sms_message"),
                                rs.getInt("sms_GroupId")
                        );
                        return sms;
                    }
                });

        return null;
    }


    public List<Sms> getMessages(User user) {

        String sql = " select sms_id,sms_message,sms_time,sms_GroupId from ET_sms where sms_phone='" + user.getUsername() + "'";

        String backSql = " select sms_id,sms_message,sms_time,sms_GroupId from Et_Sms_Backup where sms_phone='" + user.getUsername() + "'";

        return null;
    }

    public List<Group> getCategories(User user) {
        String sql = " select sendList_GroupID,SendList_Push from LZ_SMSSendList where sendList_mobile='" + user.getUsername() + "'";

        return getJdbcTemplate().query(sql, new RowMapper<Group>() {
            @Override
            public Group mapRow(ResultSet rs, int i) throws SQLException {
                Group group = new Group(
                        rs.getInt("sendList_GroupID"),
                        rs.getInt("SendList_Push")
                );
                return group;
            }
        });
    }

    public int updateCategories(User user, Group group) {

        String sql = " update LZ_SMSSendList set SendList_Push='" + group.getAllowPush() + "' where sendList_mobile='" + user.getUsername() + "' ";

        return getJdbcTemplate().update(sql);
    }

    public List<Group> listGroup() {
        String sql = " select GroupID,GroupReadme ET_SmsUserGroup ";

        return getJdbcTemplate().query(sql, new RowMapper<Group>() {
            @Override
            public Group mapRow(ResultSet rs, int i) throws SQLException {
                Group group = new Group(
                        rs.getInt("GroupID"),
                        rs.getString("GroupReadme")
                );
                return group;
            }
        });
    }

}
