package application.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class UpdatesDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UpdatesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ArrayList<ShopUnit> selectUpdates(){
        return (ArrayList<ShopUnit>) jdbcTemplate.query("SELECT * from updates", BeanPropertyRowMapper.newInstance(ShopUnit.class));
    }

    public void insertUpdate(String id, String name, String date, String parent_id, ShopUnitType type, Long price){
        jdbcTemplate.update("INSERT INTO updates (id, name, date, parentid, type, price) VALUES(?,?,?,?,?,?)",
                id, name, date, parent_id, type.name(), price);
    }

    public void deleteUpdate(String id){
        jdbcTemplate.update("DELETE from updates where id=?", id);
    }
}


// create image with an adequate name
// create file of image
// move that file on remote server
// take the image from that file from remote server and run container