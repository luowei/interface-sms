package com.rootls.suggestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by luowei on 2014/6/27.
 */
@Component
public class SuggestionRepository  extends JdbcDaoSupport {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    void init() {
        setDataSource(dataSource);
    }

    public int addSuggestion(Suggestion suggestion) {
        final Suggestion sugess = suggestion;
        return getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                String sql = " insert into ET_sms_suggestion(author,username,comment,updateTime) " +
                        " values(?,?,?,getdate()) ";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setString(1,sugess.getAuthor());
                pst.setString(2,sugess.getUsername());
                pst.setString(3,sugess.getComment());
                return pst;
            }
        });
    }
}
