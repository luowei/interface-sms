package net.oilchem.sms;

import net.oilchem.user.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static net.oilchem.common.bean.Config.pageSizeWhileSearchingLocalSMS;

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

    @Resource(name = "pcdbJdbcTemplate")
    JdbcTemplate pcdbJdbcTemplate;


    public List<Sms> getPushSMS(User user, Sms sms) {
        String sql = " select sms_id,sms_time,sms_message,sms_GroupId from ET_sms " +
                " where sms_phone='" + user.getUsername() + "' ";
        if(StringUtils.isNotBlank(sms.getGroupIds())){
              sql = sql + " and sms_GroupId in ("+sms.getGroupIds()+")";
        }
        List<Sms> list = getJdbcTemplate().query(sql,
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

        return list;
    }


    public List<Sms> getMessages(User user, Sms sms) {

        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_YEAR);
        int day = 0;
        String sql = " select sms_id,sms_message,sms_time,sms_GroupId from ET_sms " +
                " where sms_GroupId > 0 and sms_phone='" + user.getUsername() + "'";
        if(sms.getTime()==null){
            cal.setTime(sms.getTime());
            day = cal.get(Calendar.DAY_OF_YEAR);
        }
        if(day!=0 && day != today){
             sql = " select sms_id,sms_time,sms_message,sms_GroupId from Et_Sms_Backup " +
                     " where sms_phone='" + user.getUsername() + "' and DATEDIFF(day,sms_time,GETDATE()) < 7 ";
        }

        return getJdbcTemplate().query(sql,new RowMapper<Sms>() {
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


    public List<Sms> getMessageTrial(String key, String ts) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal.add(Calendar.DAY_OF_YEAR,-3);
        cal2.add(Calendar.MONTH,-1);

        String day = sdf.format(cal.getTime());
        String monthDay = sdf.format(cal2.getTime());

        String sql = " select top "+ pageSizeWhileSearchingLocalSMS+" sms_id,sms_message,sms_time,sms_GroupId " +
                " from Et_Sms_Backup  where sms_GroupId > 0 " +
                "and sms_time < '"+day+"' and sms_time='"+monthDay+"' and sms_message like '%"+key+"%'";
        return getJdbcTemplate().query(sql,new RowMapper<Sms>() {
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
    }
}
