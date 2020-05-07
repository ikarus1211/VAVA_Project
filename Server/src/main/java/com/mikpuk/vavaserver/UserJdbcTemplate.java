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

    //Tato funkcia zapisu pouzivatela do databazy
    @Override
    public void createUser(String name,String password) {
        String query = "insert into users (username,password,reputation) values (?,?,?)";
        logger.info("Executing query {} with variables {} {}",query,name,password);
        jdbcTemplate.update(query, name,password,0);
    }

    //Toto sa mozno bude hodit v buducnosti
    @Override
    public User getUserById(long id) {
        String query = "select * from users where id = ?";
        logger.info("Executing query - {} with variable {}",query,id);
        return jdbcTemplate.queryForObject(query, new Object[]{id}, new UserMapper());
    }

    //Tato funkcia vracia pouzivatela, ktory sa chce prihlasit
    @Override
    public User getUserByData(String username,String password) {
        String query = "select * from users where username = ? and password = ?";
        logger.info("Executing query - {} with variables {} {}",query,username,password);
        return jdbcTemplate.queryForObject(query, new Object[]{username,password}, new UserMapper());
    }

    //Tato funkcia vrati pocet danych pouzivatelskych mien z databazy
    @Override
    public int checkUsername(String username) {
        String query = "SELECT COUNT(*) FROM vavaDB.users where username = ?";
        logger.info("Executing query - {} with variables {} ",query,username);
        return jdbcTemplate.queryForObject(query,new Object[]{username},Integer.class);
    }

}
