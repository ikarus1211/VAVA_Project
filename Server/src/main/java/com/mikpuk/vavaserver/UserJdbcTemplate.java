package com.mikpuk.vavaserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

//Tata classa sluzi na volanie SQL queries. Datasource je dany v beans.xml
public class UserJdbcTemplate implements UserDAO {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private Logger logger = LoggerFactory.getLogger(UserJdbcTemplate.class);

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void createUser(String name,String password) {
        String query = "insert into users (username,password,reputation) values (?,?,?)";
        logger.info("Executing query {} with variables {} {}",query,name,password);
        jdbcTemplate.update(query, name,password,0);
    }

    @Override
    public User getUserById(long id) {
        String query = "select * from users where id = ?";
        logger.info("Executing query - {} with variable {}",query,id);
        return jdbcTemplate.queryForObject(query, new Object[]{id}, new UserMapper());
    }

    @Override
    public User getUserByData(String username,String password) {
        String query = "select * from users where username = ? and password = ?";
        logger.info("Executing query - {} with variables {} {}",query,username,password);
        return jdbcTemplate.queryForObject(query, new Object[]{username,password}, new UserMapper());
    }


}
