package com.mikpuk.vavaserver;

import javax.sql.DataSource;
import java.util.List;

public interface ItemDAO {

    public void setDataSource(DataSource dataSource);
    public void createItem(String name, String description, double longtitude, double latitude, long user_id,long type_id);
    public Item getItem(long id);
    public List<Item> getItemsByUser(long id);
    public List<Item> getItemsByUserLimit(long id,long limit_start,long limit_end);
    public void updateItem(long id, String name, String description, double longtitude, double latitude, boolean accepted);
    public List<Item> getOtherItems(long id);
    public List<Item> getOtherItemsByUserLimit(long id,long limit_start,long limit_end);
    public List<Item> getApprovedItems(long id);
    public List<Item> getApprovedItemsLimit(long id,long limit_start,long limit_end);
    public void setAcceptedItem(long item_id, long user_id);
    public void removeAcceptedItem(long item_id);
    public void removeItem(long item_id);
    public int checkUsername(String username);
}
