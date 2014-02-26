package net.oilchem.sms;

import net.oilchem.user.User;
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
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sql = " select  sms_id,sms_time,sms_message,sms_GroupId from ET_sms with (NOLOCK)  " +
                " where sms_GroupId > 0 and sms_phone='" + user.getUsername() + "' ";

        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_YEAR);
        int day = 0;
        String ts = "";

        if(sms.getTime()!=null){
            cal.setTime(sms.getTime());
            day = cal.get(Calendar.DAY_OF_YEAR);
            ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(sms.getTime());
            sql = sql + " and sms_time >= '"+ts+"'";
        }

        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_YEAR,-7);

        if(day!=0 && day != today){
            String sevenDayStr = sdf.format(cal2.getTime());
            sql = " select top 10 sms_id,sms_time,sms_message,sms_GroupId from Et_Sms_Backup with (NOLOCK)  " +
                    " where sms_phone='" + user.getUsername() + "' and sms_time>='"+sevenDayStr+"' and  sms_time >= '"+ts+"' ";
        }

        if(isNotBlank(sms.getGroupIds())){
            sql = sql + " and sms_GroupId in ("+sms.getGroupIds()+")";
        }

        sql = sql + " order by sms_GroupId,sms_time desc ";
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_YEAR);
        String todayStr = sdf.format(cal.getTime());

        int day = 0;
        String ts = "";
        if(sms.getTime()!=null){
            cal.setTime(sms.getTime());
            day = cal.get(Calendar.DAY_OF_YEAR);
            ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(sms.getTime());
        }

        if(day != today){


            String sql = " select sms_id,sms_message,sms_time,sms_GroupId from ET_sms with (NOLOCK)  " +
                    " where sms_GroupId > 0 and sms_phone='" + user.getUsername() + "'";

            Calendar cal2 = Calendar.getInstance();
            cal2.add(Calendar.DAY_OF_YEAR,-6);
            String sevenDayStr = sdf.format(cal2.getTime());
            String sql2 = " select sms_id,sms_message,sms_time,sms_GroupId from Et_Sms_Backup with (NOLOCK)  " +
                    " where sms_phone='" + user.getUsername() + "' and sms_time>='"+sevenDayStr+"' ";

            if(sms.getTime()!=null){
                sql = sql + " and sms_time >= '"+ts+"' ";
                sql2 = sql2 + " and sms_time >= '"+ts+"' ";
            }

            if(isNotBlank(sms.getGroupIds())){
                sql = sql + " and sms_GroupId in ("+sms.getGroupIds()+") ";
                sql2 = sql2 + " and sms_GroupId in ("+sms.getGroupIds()+") ";
            }
            sql = "select * from (" + sql + " union " +sql2 + ") a ";
            sql = sql + " order by sms_time desc ";

            System.out.println("===========:"+sql);

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

        } else {
            String sql3 = " select sms_id,sms_message,sms_time,sms_GroupId from ET_sms with (NOLOCK)  " +
                    " where sms_GroupId > 0 and sms_phone='" + user.getUsername() + "'";
            if(sms.getTime()!=null){
                sql3 = sql3 + " and sms_time >= '"+ts+"' ";
            }

            if(isNotBlank(sms.getGroupIds())){
                sql3 = sql3 + " and sms_GroupId in ("+sms.getGroupIds()+") ";
            }
            sql3 = sql3 + " order by sms_time desc ";

            return getJdbcTemplate().query(sql3,new RowMapper<Sms>() {
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

        String sql = " update LZ_SMSSendList set SendList_Push='" + group.getAllowPush() + "'" +
                " where sendList_mobile='" + user.getUsername() + "' and sendList_GroupID= "+group.getGroupId();

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
                " from Et_Sms_Backup with (NOLOCK)  where sms_GroupId > 0 " +
                "and sms_time < '"+day+"' and sms_time > '"+monthDay+"' ";
        if(isNotBlank(key)){
            sql = sql + " and sms_message like '%"+key+"%' ";
        }
        sql = sql + " order by sms_GroupId,sms_time desc ";

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
