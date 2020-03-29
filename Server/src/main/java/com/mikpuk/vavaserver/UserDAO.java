package com.mikpuk.vavaserver;

import javax.sql.DataSource;

public interface UserDAO {

    public void setDataSource(DataSource dataSource);
    public void createUser(String name,String password);
    public User getUser(int id);
    public int checkAvailableCredentials(String username, String password);
}
