package com.mikpuk.vavaserver;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

//Tato classa sluzi na mapovanie vysledkov z databazy do User objektu
public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setReputation(rs.getInt("reputation"));
        user.setUsername(rs.getString("username"));

        return user;
    }
}
