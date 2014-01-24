package net.oilchem.sms;

import net.oilchem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-24
 * Time: 上午8:55
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SmsRepository  extends JdbcDaoSupport{

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    void init(){
        setDataSource(dataSource);
    }


    public List<Sms> list(User user) {

        String sql = " select sms_id,sms_message,sms_time,sms_GroupId ET_sms where sms_phone='"+user.getUsername()+"'";

        return null;
    }

    public List<Group> listGroup(User user) {
        String sql = " select sendList_GroupID,SendList_Push LZ_SMSSendList ";

        return null;
    }

    public List<Sms> getMessages(User user) {
        String sql = " select sms_id,sms_message,sms_time,sms_GroupId ET_sms where sms_phone='"+user.getUsername()+"'";

        String backSql = " select sms_id,sms_message,sms_time,sms_GroupId Et_Sms_Backup where sms_phone='"+user.getUsername()+"'";

        return null;
    }
}
