package application.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class ConnectionsDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ConnectionsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public ArrayList<Connection> selectConnections(){
        return (ArrayList<Connection>) jdbcTemplate.query("SELECT * from connections", BeanPropertyRowMapper.newInstance(Connection.class));
    }

    public void insertConnection(String child_id, String parent_id){
        jdbcTemplate.update("INSERT INTO connections (childid, parentid) VALUES(?,?)",
                child_id, parent_id);
    }

    public void updateConnection(String parent_id, String child_id){
        jdbcTemplate.update("UPDATE connections SET parentId=? WHERE childId=?",
                parent_id, child_id);
    }

    public void deleteConnection(String id){
        jdbcTemplate.update("DELETE from connections where childid=?", id);
    }
}
