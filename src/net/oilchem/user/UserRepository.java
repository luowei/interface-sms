package net.oilchem.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

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
    void init(){
        setDataSource(dataSource);
    }

}
