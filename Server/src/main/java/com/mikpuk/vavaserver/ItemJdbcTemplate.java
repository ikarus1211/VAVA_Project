package com.mikpuk.vavaserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class ItemJdbcTemplate implements ItemDAO {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    private Logger logger = LoggerFactory.getLogger(ItemJdbcTemplate.class);

    //Hodnota o kolko zvysujeme reputaciu pri prijati ponuky
    private int REPUTATION_INCREASE = 10;

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //Tato funkcia vytvori novy item v DB na zaklade parametrov
    @Override
    public void createItem(String name, String description, double  longitude, double latitude, long user_id, long type_id) {
        String query = "insert into items (name,description,longitude,latitude,user_id,accepted,type_id) values (?,?,?,?,?,?,?)";
        logger.info("Executing query - {} with variables {} {} {} {} {} {} {}",
                query,name,description,longitude,latitude,user_id,false,type_id);
        jdbcTemplate.update(query, name,description,longitude,latitude,user_id,false,type_id);
    }

    //Vrati vsetky itemy, ktore vlastni dany pouzivatel
    @Override
    public List<Item> getItemsByUserLimit(long id, long limit_start, long limit_end) {
        String query = "SELECT * from vavaDB.items a INNER JOIN vavaDB.users i ON a.user_id = i.id where a.user_id = ? ORDER BY a.id ASC LIMIT ? , ?";
        logger.info("Executing query - {} with variable {} {} {}",query,id,limit_start,limit_end);
        return jdbcTemplate.query(query, new Object[]{id,limit_start,limit_end}, new ItemMapper());
    }

    //Toto zatial nepouzivame ale planujeme implementovat
    @Override
    public void updateItem(long id, String name, String description, double longitude, double latitude, boolean accepted) {
        String query = "update items set name = ?,description = ?,longitude = ?,latitude = ?, accepted = ? where id = ?;";
        logger.info("Executing query - {} with variables {} {} {} {} {} {}",
                query,name,description,longitude,latitude,true,id);
        jdbcTemplate.update(query, name,description,longitude,latitude,true,id);
    }

    //Tato funkcia vracia vsetky objekty, ktore nepatria pouzivatelovi a nie su potvrdene
    @Override
    public List<Item> getOtherItemsByUserLimit(long id, long limit_start, long limit_end) {
        String query = "SELECT * from vavaDB.items a INNER JOIN vavaDB.users i ON a.user_id = i.id" +
                " where a.user_id != ? and a.accepted = false ORDER BY a.id DESC LIMIT ? , ?";
        logger.info("Executing query - {} with variables {} {} {}",query,id,limit_start,limit_end);
        return jdbcTemplate.query(query, new Object[]{id,limit_start,limit_end}, new ItemMapper());
    }


    //Toto vrati vsetky itemy z DB, ktore pouzivatel prijal
    @Override
    public List<Item> getApprovedItemsLimit(long id, long limit_start, long limit_end) {
        String query = "SELECT * from vavaDB.approved_items a INNER JOIN vavaDB.items i ON a.item_id = i.id INNER JOIN vavaDB.users u ON a.user_id = u.id WHERE a.user_id = ? LIMIT ? , ?;";
        logger.info("Executing query - {} with variables {} {} {}",query,id,limit_start,limit_end);
        return jdbcTemplate.query(query, new Object[]{id,limit_start,limit_end}, new ItemMapper());
    }

    //Tato funkcia nastavi item ako prijaty
    @Override
    public void setAcceptedItem(long item_id, long user_id) {
        String query = "insert into approved_items (user_id,item_id) values (?,?)";
        String query2 = "update items set accepted = 1 where id = ?";
        logger.info("Executing query - {} with variable {}",query2,item_id);
        jdbcTemplate.update(query2, item_id);
        logger.info("Executing query - {} with variables {} {}",query,user_id,item_id);
        jdbcTemplate.update(query, user_id,item_id);
    }

    //Tato funkcia sa vola, ak pouzivatel potvrdil vykonanie ponuky a teda zmaze itemy a pripise reputaciu
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

    //Tato funkcia vymaze item z databazy
    @Override
    public void removeItem(long item_id) {
        String query = "delete from items where id = ?";
        logger.info("Executing query - {} with variable {}",query,item_id);
        jdbcTemplate.update(query, item_id);
    }

}
