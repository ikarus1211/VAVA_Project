package com.mikpuk.vavaserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class UserJdbcTemplate implements UserDAO {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(UserJdbcTemplate.class);

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //Creates user account
    @Override
    public void createUser(String name,String password) {
        String query = "insert into users (username,password,reputation) values (?,?,?)";
        logger.info("Executing query {} with variables {} {}",query,name,password);
        jdbcTemplate.update(query, name,password,0);
    }

    //Returns user
    @Override
    public User getUserById(long id) {
        String query = "select * from users where id = ?";
        logger.info("Executing query - {} with variable {}",query,id);
        return jdbcTemplate.queryForObject(query, new Object[]{id}, new UserMapper());
    }

    //Get user by credentials
    @Override
    public User getUserByData(String username,String password) {
        String query = "select * from users where username = ? and password = ?";
        logger.info("Executing query - {} with variables {} {}",query,username,password);
        return jdbcTemplate.queryForObject(query, new Object[]{username,password}, new UserMapper());
    }

    @Override
    public User getAcceptedUser(long item_id) {
        try {
            String user_getter_query = "select user_id from vavaDB.approved_items WHERE item_id = ?";
            logger.info("Executing query - {} with variables {}", user_getter_query, item_id);
            long userId = jdbcTemplate.queryForObject(user_getter_query, new Object[]{item_id}, Long.class);
            return getUserById(userId);
        }catch (Exception e) {
            logger.error("Exception in getAcceptedUser",e);
            return null;
        }
    }

    //Check if username exists
    @Override
    public int checkUsername(String username) {
        String query = "SELECT COUNT(*) FROM vavaDB.users where username = ?";
        logger.info("Executing query - {} with variables {} ",query,username);
        return jdbcTemplate.queryForObject(query,new Object[]{username},Integer.class);
    }

}
