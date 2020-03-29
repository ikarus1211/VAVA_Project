package com.mikpuk.vavaserver;

import javax.sql.DataSource;
import java.util.List;

public interface ItemDAO {

    public void setDataSource(DataSource dataSource);
    public void createItem(String name, String description, float longtitude, float latitude, long user_id,long type_id);
    public Item getItem(long id);
    public List<Item> getItemsByUser(long id);
    public void updateItem(long id, String name, String description, float longtitude, float latitude, boolean accepted);
}
