package com.mikpuk.vavaserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class ItemJdbcTemplate implements ItemDAO {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger(ItemJdbcTemplate.class);

    //When user completes request we increase his reputation by this value
    private int REPUTATION_INCREASE = 10;

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //Creates new item in DB
    @Override
    public void createItem(String name, String description, double  longitude, double latitude, long user_id, long type_id) {
        String query = "insert into items (name,description,longitude,latitude,user_id,accepted,type_id) values (?,?,?,?,?,?,?)";
        logger.info("Executing query - {} with variables {} {} {} {} {} {} {}",
                query,name,description,longitude,latitude,user_id,false,type_id);
        jdbcTemplate.update(query, name,description,longitude,latitude,user_id,false,type_id);
    }

    //Returns all items created by user
    @Override
    public List<Item> getItemsByUserLimit(long id, long limit_start, long limit_end) {
        String query = "SELECT * from vavaDB.items a INNER JOIN vavaDB.users i ON a.user_id = i.id where a.user_id = ? ORDER BY a.accepted DESC LIMIT ? , ?";
        logger.info("Executing query - {} with variable {} {} {}",query,id,limit_start,limit_end);
        return jdbcTemplate.query(query, new Object[]{id,limit_start,limit_end}, new ItemMapper());
    }

    //TODO
    @Override
    public void updateItem(long id, String name, String description, double longitude, double latitude, boolean accepted) {
        String query = "update items set name = ?,description = ?,longitude = ?,latitude = ?, accepted = ? where id = ?;";
        logger.info("Executing query - {} with variables {} {} {} {} {} {}",
                query,name,description,longitude,latitude,true,id);
        jdbcTemplate.update(query, name,description,longitude,latitude,true,id);
    }

    //Return all items which are not taken and are from other users
    @Override
    public List<Item> getOtherItemsByUserLimit(long id, long limit_start, long limit_end) {
        String query = "SELECT * from vavaDB.items a INNER JOIN vavaDB.users i ON a.user_id = i.id" +
                " where a.user_id != ? and a.accepted = false ORDER BY i.reputation DESC, a.id ASC LIMIT ? , ?";
        logger.info("Executing query - {} with variables {} {} {}",query,id,limit_start,limit_end);
        return jdbcTemplate.query(query, new Object[]{id,limit_start,limit_end}, new ItemMapper());
    }

    //Returns all accepted requests of user
    @Override
    public List<Item> getApprovedItemsLimit(long id, long limit_start, long limit_end) {
        String query = "SELECT * from vavaDB.approved_items a INNER JOIN vavaDB.items i ON a.item_id = i.id INNER JOIN vavaDB.users u ON a.user_id = u.id WHERE a.user_id = ? LIMIT ? , ?;";
        logger.info("Executing query - {} with variables {} {} {}",query,id,limit_start,limit_end);
        return jdbcTemplate.query(query, new Object[]{id,limit_start,limit_end}, new ItemMapper());
    }

    //Sets item as accepted
    @Override
    public void setAcceptedItem(long item_id, long user_id) {
        String query = "insert into approved_items (user_id,item_id) values (?,?)";
        String query2 = "update items set accepted = 1 where id = ?";
        logger.info("Executing query - {} with variable {}",query2,item_id);
        jdbcTemplate.update(query2, item_id);
        logger.info("Executing query - {} with variables {} {}",query,user_id,item_id);
        jdbcTemplate.update(query, user_id,item_id);
    }

    //This removes item when request is succesfully done. Also inreases the reputation of user which borrowed the item
    @Override
    public void removeAcceptedItem(long item_id) {
        String user_getter_query = "select user_id from vavaDB.approved_items WHERE item_id = ?";
        String query = "DELETE FROM vavaDB.approved_items WHERE item_id = ?";
        String query2 = "DELETE FROM vavaDB.items WHERE id = ?";
        String query3 = "UPDATE vavaDB.users SET reputation = reputation + ? WHERE id = ?";
        logger.info("Executing query - {} with variable {}",user_getter_query,item_id);
        long userId = jdbcTemplate.queryForObject(user_getter_query,new Object[]{item_id},Long.class);
        logger.info("Executing query - {} with variable {}",query,item_id);
        jdbcTemplate.update(query,item_id);
        logger.info("Executing query - {} with variable {}",query2,item_id);
        jdbcTemplate.update(query2,item_id);
        logger.info("Executing query - {} with variables {} {}",query3,REPUTATION_INCREASE,userId);
        jdbcTemplate.update(query3,REPUTATION_INCREASE,userId);
    }

    //Removes items
    @Override
    public void removeItem(long item_id) {
        String query = "delete from items where id = ?";
        logger.info("Executing query - {} with variable {}",query,item_id);
        jdbcTemplate.update(query, item_id);
    }

}
