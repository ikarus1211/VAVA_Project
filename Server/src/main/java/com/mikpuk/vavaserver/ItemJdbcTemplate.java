package com.mikpuk.vavaserver;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class ItemJdbcTemplate implements ItemDAO {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void createItem(String name, String description, double  longitude, double latitude, long user_id, long type_id) {
        String query = "insert into items (name,description,longitude,latitude,user_id,accepted,type_id) values (?,?,?,?,?,?,?)";
        jdbcTemplate.update(query, name,description,longitude,latitude,user_id,false,type_id);
        System.out.println("Created Item " + name);
    }

    @Override
    public Item getItem(long id) {
        String query = "SELECT * from vavaDB.items a INNER JOIN vavaDB.users i ON a.user_id = i.id WHERE a.id = ?;";
        return jdbcTemplate.queryForObject(query, new Object[]{id}, new ItemMapper());
    }

    @Override
    public List<Item> getItemsByUser(long id) {
        String query = "SELECT * from vavaDB.items a INNER JOIN vavaDB.users i ON a.user_id = i.id where a.user_id = ?";
        return jdbcTemplate.query(query, new Object[]{id}, new ItemMapper());
    }

    @Override
    public void updateItem(long id, String name, String description, double longitude, double latitude, boolean accepted) {
        //UPDATE table_name SET field1 = new-value1, field2 = new-value2
        String query = "update items set name = ?,description = ?,longitude = ?,latitude = ?, accepted = ? where id = ?;";
        jdbcTemplate.update(query, name,description,longitude,latitude,true,id);
        System.out.println("Updated Item " + name);
    }

    @Override
    public List<Item> getOtherItems(long id) {
        String query = "SELECT * from vavaDB.items a INNER JOIN vavaDB.users i ON a.user_id = i.id" +
                " where a.user_id != ? and a.accepted = false ORDER BY a.id DESC";
        return jdbcTemplate.query(query, new Object[]{id}, new ItemMapper());
    }

    @Override
    public List<Item> getApprovedItems(long id) {
        String query = "SELECT * from vavaDB.approved_items a INNER JOIN vavaDB.items i ON a.id = i.id WHERE a.user_id = ?;";
        return jdbcTemplate.query(query, new Object[]{id}, new ItemMapper());
    }
}
