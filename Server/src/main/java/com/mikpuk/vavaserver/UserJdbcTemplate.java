package com.mikpuk.vavaserver;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class UserJdbcTemplate implements UserDAO {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void createUser(String name,String password) {
        String query = "insert into users (username,password,reputation) values (?,?,?)";
        jdbcTemplate.update(query, name,password,0);
        System.out.println("Created User " + name);
    }

    @Override
    public User getUser(int id) {
        String query = "select * from users where id = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{id}, new UserMapper());
    }

    @Override
    public int checkAvailableCredentials(String username, String password) {
        String query = "select COUNT(*) from users where username = ? and password = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{username,password}, Integer.class);
    }
}
