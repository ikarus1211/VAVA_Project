package com.mikpuk.vavaserver;

import javax.sql.DataSource;
import java.util.List;

public interface ItemDAO {

    void setDataSource(DataSource dataSource);
    void createItem(String name, String description, double longtitude, double latitude, long user_id, long type_id);
    List<Item> getItemsByUserLimit(long id, long limit_start, long limit_end);
    void updateItem(long id, String name, String description, double longtitude, double latitude, boolean accepted);
    List<Item> getOtherItemsByUserLimit(long id, long limit_start, long limit_end);
    List<Item> getApprovedItemsLimit(long id, long limit_start, long limit_end);
    void setAcceptedItem(long item_id, long user_id);
    void removeAcceptedItem(long item_id);
    void removeItem(long item_id);
}
