package application.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class ItemsDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ItemsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ArrayList<ShopUnit> selectItems(){
        return (ArrayList<ShopUnit>) jdbcTemplate.query("SELECT * from shopunits", BeanPropertyRowMapper.newInstance(ShopUnit.class));
    }

    public void insertItem(String id, String name, String date, String parent_id, ShopUnitType type, Long price){
        jdbcTemplate.update("INSERT INTO shopunits (id, name, date, parentid, type, price) VALUES(?,?,?,?,?,?)",
                id, name, date, parent_id, type.name(), price);
    }

    public void updateItem(String id, String name, String date, String parent_id, ShopUnitType type, Long price){
        jdbcTemplate.update("UPDATE shopunits SET name=?, date=?, parentId=?, type=?, price=? WHERE id=?",
                name, date, parent_id, type.name(), price, id);
    }

    public void deleteItem(String id){
        jdbcTemplate.update("DELETE from shopunits where id=?", id);
    }
}
