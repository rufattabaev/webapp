package ru.itpark.service;

import ru.itpark.domain.Auto;
import ru.itpark.util.JdbcTemplate;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.util.List;
import java.util.UUID;

public class AutoService {
    private final DataSource ds;
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public AutoService() throws NamingException {
        var context = new InitialContext();
        ds = (DataSource) context.lookup("java:comp/env/db.sqlite");
        String sql = "CREATE TABLE IF NOT EXISTS autos (id TEXT PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL, image TEXT);";
        jdbcTemplate.executeUpdate(ds, sql);

    }

    public List<Auto> getAll() {
        String sql = "SELECT id, name, description, image FROM autos;";
        return jdbcTemplate.executeQuery(ds, sql, rs ->
                new Auto(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("image")
                ));
    }


    public void create(String name, String description, String image) {
        String sql = "INSERT INTO autos(id, name, description, image) VALUES(?,?,?,?)";
        jdbcTemplate.executeUpdate(ds, sql, stmt -> {
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, name);
            stmt.setString(3, description);
            stmt.setString(4, image);
            return stmt;
        });
    }
}

