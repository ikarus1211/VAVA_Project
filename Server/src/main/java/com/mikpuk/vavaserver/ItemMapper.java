package com.mikpuk.vavaserver;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemMapper implements RowMapper<Item> {
    @Override
    public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        item.setAccepted(rs.getBoolean("accepted"));
        item.setDescription(rs.getString("description"));
        item.setLatitude(rs.getLong("latitude"));
        item.setLongtitude(rs.getLong("longtitude"));
        item.setName(rs.getString("name"));
        item.setType_id(rs.getInt("type_id"));

        return item;
    }
}
