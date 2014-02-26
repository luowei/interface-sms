package net.oilchem.common.bean;

import net.oilchem.sms.Group;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.oilchem.common.bean.Config.inerCache_expire;
import static org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-26
 * Time: 上午11:32
 * To change this template use File | Settings | File Templates.
 */
public class InerCache {

    Map<Integer, Group> groupIdMap;
    Long initTime;
    static InerCache inerCache;

    public synchronized void init() {
        if (inerCache == null) {
            inerCache = new InerCache();
        }

        try {
            String sql = " select GroupID,GroupShowName from ET_SmsUserGroup ";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(BasicDataSource.class.cast(getCurrentWebApplicationContext().getBean("dataSource")));
            List<Group> uidNameList = jdbcTemplate.query(sql, new RowMapper<Group>() {
                @Override
                public Group mapRow(ResultSet rs, int i) throws SQLException {
                    Group group = new Group(
                            rs.getInt("GroupID"),
                            rs.getString("GroupShowName")
                    );
                    return group;
                }
            });

            inerCache.groupIdMap = new HashMap<Integer, Group>(1000);
            for (Group user : uidNameList) {
                inerCache.groupIdMap.put(user.getId(), user);
            }
            inerCache.initTime = System.currentTimeMillis();
            System.out.println("################# group's (id,group) map size :" + groupIdMap.size() + " #################");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Map<Integer, Group> getGroupMap() {
        if (inerCache == null) {
            inerCache = new InerCache();
        }
        Long userMapTime = System.currentTimeMillis() - inerCache.initTime;
        if (inerCache.groupIdMap == null || inerCache.groupIdMap.isEmpty() ||
                userMapTime > inerCache_expire) {
            inerCache.init();
        }
        return inerCache.groupIdMap;
    }

    public static Map<Integer, Group> getUserMapByFlag(String flag) {
        if (inerCache == null) {
            inerCache = new InerCache();
        }
        Long userMapTime = System.currentTimeMillis() - inerCache.initTime;
        if (inerCache.groupIdMap == null || inerCache.groupIdMap.isEmpty() ||
                userMapTime > inerCache_expire || (flag != null && "true".equals(flag))) {
            inerCache.init();
        }
        return inerCache.groupIdMap;
    }

    private InerCache() {
        initTime = System.currentTimeMillis();
    }


    public static void clearCache() {
        inerCache = null;
    }
}