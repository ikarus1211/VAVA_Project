package com.mikpuk.vavaserver;

import javax.sql.DataSource;

public interface UserDAO {

    public void setDataSource(DataSource dataSource);
    public void createUser(String name,String password);
    public User getUserById(int id);
    public User getUserByData(String username,String password);
}
