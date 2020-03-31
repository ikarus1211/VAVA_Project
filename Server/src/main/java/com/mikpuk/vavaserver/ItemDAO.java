package com.mikpuk.vavaserver;

import javax.sql.DataSource;
import java.util.List;

public interface ItemDAO {

    public void setDataSource(DataSource dataSource);
    public void createItem(String name, String description, double longtitude, double latitude, long user_id,long type_id);
    public Item getItem(long id);
    public List<Item> getItemsByUser(long id);
    public void updateItem(long id, String name, String description, double longtitude, double latitude, boolean accepted);
    public List<Item> getOtherItems(long id);
    public List<Item> getApprovedItems(long id);
}
