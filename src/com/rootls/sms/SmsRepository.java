package com.rootls.sms;

import com.rootls.common.bean.Config;
import com.rootls.notification.IOSPush;
import com.rootls.common.utils.EHCacheUtil;
import com.rootls.user.User;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
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
        String sql = " select  sms_id,sms_sendMsg_ID,sms_time,sms_message,sms_GroupId from ET_sms with (NOLOCK)  " +
                " where sms_GroupId > 0 and sms_phone='" + user.getUsername() + "' ";

        Calendar cal = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        int day = 0;
        String ts = "";

        if (sms.getTime() != null) {
            cal.setTime(sms.getTime());
            day = cal.get(Calendar.DAY_OF_YEAR);
            ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(sms.getTime());
            sql = sql + " and sms_time > '" + ts + "'";
        } else {
            sql = sql + " and sms_time > '" + today + "'";
        }

//        Calendar cal2 = Calendar.getInstance();
//        cal2.add(Calendar.DAY_OF_YEAR, -7);
//
//        if (day != 0 && day != today) {
//            String sevenDayStr = sdf.format(cal2.getTime());
//            sql = " select top 10 sms_id,sms_time,sms_message,sms_GroupId from Et_Sms_Backup with (NOLOCK)  " +
//                    " where sms_phone='" + user.getUsername() + "' and sms_time>='" + sevenDayStr + "' and  sms_time >= '" + ts + "' ";
//        }

//        if (isNotBlank(sms.getGroupIds())) {
//            sql = sql + " and sms_GroupId in (" + sms.getGroupIds() + ")";
//        }
        Boolean hasPushGroups = EHCacheUtil.<Boolean>getValue("userGroupPush", user.getUsername());
        if (hasPushGroups!=null && hasPushGroups) {
            String groups = EHCacheUtil.<String>getValue("userGroups", user.getUsername());
            sql = sql + " and sms_GroupId in (" + (isBlank(groups) ? getPushGroupsStr(user) : groups) + ")";
        }

        sql = sql + " order by sms_time desc ";

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
                        sms.setMsgId(String.valueOf(rs.getInt("sms_sendMsg_ID")));
                        return sms;
                    }
                }
        );

        return list;
    }


    public List<Sms> getMessages(User user, Sms sms) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_YEAR);
        String todayStr = sdf.format(cal.getTime());

        int day = 0;
        String ts = "";
        //没传日期
        if (sms.getTime() != null) {
            cal.setTime(sms.getTime());
            day = cal.get(Calendar.DAY_OF_YEAR);
            ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(sms.getTime());
        }

        //如果没传或不等于today,取包括今天以及七天内的数据
        if (day != today) {


            String sql = " select sms_id,sms_sendMsg_ID,sms_message,sms_time,sms_GroupId from ET_sms with (NOLOCK)  " +
                    " where sms_GroupId > 0 and sms_phone='" + user.getUsername() + "'";

            Calendar cal2 = Calendar.getInstance();
            cal2.add(Calendar.DAY_OF_YEAR, -6);
            String sevenDayStr = sdf.format(cal2.getTime());
            String sql2 = " select sms_id,sms_sendMsg_ID,sms_message,sms_time,sms_GroupId from Et_Sms_Backup with (NOLOCK)  " +
                    " where sms_phone='" + user.getUsername() + "' and sms_time>='" + sevenDayStr + "' ";

            if (sms.getTime() != null) {
                sql = sql + " and sms_time >= '" + ts + "' ";
                sql2 = sql2 + " and sms_time >= '" + ts + "' ";
            }

            if (isNotBlank(sms.getGroupIds())) {
                sql = sql + " and sms_GroupId in (" + sms.getGroupIds() + ") ";
                sql2 = sql2 + " and sms_GroupId in (" + sms.getGroupIds() + ") ";
            }
            sql = "select * from (" + sql + " union " + sql2 + ") a ";
            sql = sql + " order by sms_time asc ";


            return getJdbcTemplate().query(sql, new RowMapper<Sms>() {
                @Override
                public Sms mapRow(ResultSet rs, int i) throws SQLException {
                    Sms sms = new Sms(
                            rs.getInt("sms_id"),
                            rs.getTimestamp("sms_time"),
                            rs.getString("sms_message"),
                            rs.getInt("sms_GroupId")
                    );
                    sms.setMsgId(String.valueOf(rs.getInt("sms_sendMsg_ID")));
                    return sms;
                }
            });

        } else {
            String sql3 = " select sms_id,sms_sendMsg_ID,sms_message,sms_time,sms_GroupId from ET_sms with (NOLOCK)  " +
                    " where sms_GroupId > 0 and sms_phone='" + user.getUsername() + "'";
            if (sms.getTime() != null) {
                sql3 = sql3 + " and sms_time >= '" + ts + "' ";
            }

            if (isNotBlank(sms.getGroupIds())) {
                sql3 = sql3 + " and sms_GroupId in (" + sms.getGroupIds() + ") ";
            }
            sql3 = sql3 + " order by sms_time asc ";

            return getJdbcTemplate().query(sql3, new RowMapper<Sms>() {
                @Override
                public Sms mapRow(ResultSet rs, int i) throws SQLException {
                    Sms sms = new Sms(
                            rs.getInt("sms_id"),
                            rs.getTimestamp("sms_time"),
                            rs.getString("sms_message"),
                            rs.getInt("sms_GroupId")
                    );
                    sms.setMsgId(String.valueOf(rs.getInt("sms_sendMsg_ID")));
                    return sms;
                }
            });
        }

    }

    public List<Group> getCategories(User user) {

        EHCacheUtil.<String>setValue("userGroups", user.getUsername(), getPushGroupsStr(user));

        String sql = " select sendList_GroupID,SendList_Push from LZ_SMSSendList " +
                "where sendList_isRecv = 1 and sendList_mobile='" + user.getUsername() + "'";
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

    public List<Group> getPushGroups(User user) {
        String sql = " select sendList_GroupID,SendList_Push from LZ_SMSSendList " +
                " where sendList_mobile='" + user.getUsername() + "' and SendList_Push=1";

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

    public List<Group> getAllGroups(User user) {
        String sql = " select sendList_GroupID,SendList_Push from LZ_SMSSendList " +
                " where sendList_mobile='" + user.getUsername() + "' ";

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

    public String getPushGroupsStr(User user) {
        List<Group> list = getAllGroups(user);
        String groups = "";

        if (list == null || list.isEmpty()) {
            return Config.global_groups;
        }

        Boolean isNotAllPush = false;
        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).getPush().equals(1)) {
                if (groups.equals("")) {
                    groups = list.get(i).getGroupId();
                } else {
                    groups = groups + "," + list.get(i).getGroupId();
                }
            }

            if (list.get(i).getPush().equals(0)) {
                isNotAllPush = true;
            }
        }

        EHCacheUtil.<Boolean>setValue("userGroupPush", user.getUsername(), isNotAllPush);
        if (isBlank(groups)) {
            return Config.global_groups;
        } else {
            groups = isBlank(Config.global_groups) ? groups : groups + "," + Config.global_groups;
            return groups;
        }
    }

    public boolean isAllPush(User user) {
        String sql = " select count(*) where sendList_mobile='" + user.getUsername() + "' and SendList_Push=0";
        return getJdbcTemplate().queryForInt(sql) <= 0;
    }


    public int updateCategories(User user, Group group) {

        String sql = null;

        if (group.getGroupId().equals("0") || group.getGroupId().equals("")) {
            sql = " update LZ_SMSSendList set SendList_Push='" + group.getAllowPush() + "'" +
                    " where sendList_mobile='" + user.getUsername() + "' ";
        } else {
            sql = " update LZ_SMSSendList set SendList_Push='" + group.getAllowPush() + "'" +
                    " where sendList_mobile='" + user.getUsername() + "' and sendList_GroupID= " + group.getGroupId();
        }

        int ret = getJdbcTemplate().update(sql);

        EHCacheUtil.<String>setValue("userGroups", user.getUsername(), getPushGroupsStr(user));

        return ret;
    }


    public List<Sms> getMessageTrial(String key, String ts) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal.add(Calendar.DAY_OF_YEAR, -3);
        cal2.add(Calendar.MONTH, -1);

        String day = sdf.format(cal.getTime());
        String monthDay = sdf.format(cal2.getTime());

        String sql = " select top " + Config.pageSizeWhileSearchingLocalSMS + " sendMsg_ID,sendMsg_content,sendMsg_AddTime,sendMsg_GroupID " +
                " from ET_SMS_SendMsg with (NOLOCK)  where sendMsg_GroupID is not null " +
                "and sendMsg_AddTime < '" + day + "' and sendMsg_AddTime > '" + monthDay + "' ";
        if (isNotBlank(key)) {
            sql = sql + " and sendMsg_content like '%" + key + "%' ";
        }
        sql = sql + " order by sendMsg_AddTime desc ";

        return getJdbcTemplate().query(sql, new RowMapper<Sms>() {
            @Override
            public Sms mapRow(ResultSet rs, int i) throws SQLException {
                Sms sms = new Sms();
                sms.setMsgId(String.valueOf(rs.getInt("sendMsg_ID")));
                sms.setContent(rs.getString("sendMsg_content"));
                sms.setTime(rs.getTimestamp("sendMsg_AddTime"));
                return sms;
            }
        });
    }

    public List<Reply> getReplies(String msgId, User user) {
        String sql = " select * from ET_sms_reply where msgId=" + msgId + " and username='" + user.getUsername() + "'";
        return getJdbcTemplate().query(sql, new RowMapper<Reply>() {
            @Override
            public Reply mapRow(ResultSet rs, int i) throws SQLException {
                Reply reply = new Reply(
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("msgId")),
                        rs.getString("username"),
                        rs.getString("reply")
                );
                Timestamp time = rs.getTimestamp("replyTime");
                reply.setReplyTime(String.valueOf(time.getTime()));
                return reply;
            }
        });
    }

    public Reply pushReply(Reply reply) {
        Date time = new Date();
        String replyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(time);
        String sql = " insert into ET_sms_reply(msgId,username,reply,replyTime)" +
                " values("+reply.getMsgId()+",'"+reply.getUsername()+"','"+reply.getReply()+"','"+replyTime+"') ";
        getJdbcTemplate().update(sql);
        String msgSql = " update ET_SMS_SendMsg set sendMsg_replyCount=sendMsg_replyCount+1,sendMsg_lastReplyTime=GETDATE() " +
                " where sendMsg_ID="+reply.getMsgId();
        getJdbcTemplate().update(msgSql);
        reply.setReplyTime(String.valueOf(time.getTime()));
        return reply;
    }

    public List<Sms> getIOSPushSmsList(User user, IOSPush iosPush) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        if(Config.lastPushTime==0L){
            Config.lastPushTime = new Date().getTime()-10000;
        }

        Long startTime = null,endTime=null;
        //当allFlag为null或者为0时查询所有，与getMessages无异
        Integer allFlag = null;
        if(iosPush!=null){
            startTime = iosPush.getStartTime();
            endTime = iosPush.getEndTime();
            allFlag = iosPush.getAllFlag();
        }

        String lastPushTimeStr = sdf.format(Config.lastPushTime);
        if(startTime!=null && startTime > 0L){
            lastPushTimeStr = sdf.format(startTime);
        }

        String sql = "select sms_id,sms_phone,sms_sendMsg_ID,sms_time,sms_message,sms_GroupId FROM ET_sms " +
                "where 1=1 and sms_GroupId > 0  and sms_time >'"+lastPushTimeStr+"' " +
                " and sms_phone in  (select user_mobile from LZ_SMSUSerMobile where user_push=1) ";

        if(endTime!=null && endTime > 0L){
            sql = sql + " and sms_time <= '"+sdf.format(endTime)+"' ";
        }
        if(user!=null && StringUtils.isNotBlank(user.getUsername())){
            sql = sql+" and sms_phone='" + user.getUsername() + "' ";

            if(allFlag==null || !allFlag.equals(1)) {
                Boolean hasPushGroups = EHCacheUtil.<Boolean>getValue("userGroupPush", user.getUsername());
                if (hasPushGroups != null && hasPushGroups) {
                    String groups = EHCacheUtil.<String>getValue("userGroups", user.getUsername());
                    sql = sql + " and sms_GroupId in (" + (isBlank(groups) ? getPushGroupsStr(user) : groups) + ")";
                }
            }
        }else if(user==null && (allFlag==null || !allFlag.equals(1)) ){
            sql = sql + " and (sms_GroupId in (select sendList_GroupID from LZ_SMSSendList where SendList_Push = 1 ) ";
            if(StringUtils.isNotBlank(Config.global_groups)){
                sql = sql + " or sms_GroupId in("+Config.global_groups+") ";
            }
            sql = sql + " ) ";
        }


        sql = sql + " order by sms_phone asc, sms_time desc ";

        return getJdbcTemplate().query(sql, new RowMapper<Sms>() {
            @Override
            public Sms mapRow(ResultSet rs, int i) throws SQLException {
                Sms sms = new Sms(
                        rs.getInt("sms_id"),
                        rs.getTimestamp("sms_time"),
                        rs.getString("sms_message"),
                        rs.getInt("sms_GroupId")
                );
                sms.setMsgId(String.valueOf(rs.getInt("sms_sendMsg_ID")));
                sms.setMobile(rs.getString("sms_phone"));
                return sms;
            }
        });
    }
}
