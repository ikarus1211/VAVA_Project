package com.mikpuk.vavaserver;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

//Tata classa sluzi na volanie SQL queries. Datasource je dany v beans.xml
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
    public User getUserById(int id) {
        String query = "select * from users where id = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{id}, new UserMapper());
    }

    @Override
    public User getUserByData(String username,String password) {
        String query = "select * from users where username = ? and password = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{username,password}, new UserMapper());
    }


}
