package com.mikpuk.vavaserver;

import javax.sql.DataSource;

public interface UserDAO {

    void setDataSource(DataSource dataSource);
    void createUser(String name, String password);
    User getUserById(long id);
    User getUserByData(String username, String password);
    User getAcceptedUser(long item_id);
    int checkUsername(String username);
}
