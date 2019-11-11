package ru.itpark.util;

import ru.itpark.exception.DataStoreException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {
    public JdbcTemplate() {
    }

    private <T> T executeInternal(DataSource dataSource, String sql, PreparedStatementExecutor<T> executor) {
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
                ) {
            return executor.execute(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataStoreException(e);
        }
    }

    private <T> List<T> executeQuery(DataSource dataSource, String sql, PreparedStatementSetter preparedStatementSetter, RowMapper<T> mapper) {
        return executeInternal(dataSource, sql, stmt -> {
            try (ResultSet resultSet = preparedStatementSetter.setValues(stmt).executeQuery()) {
                List<T> result = new ArrayList<>();
                while (resultSet.next()) {
                    result.add(mapper.map(resultSet));
                }
                return result;
            }
        });
    }

    public <T> List<T> executeQuery(DataSource dataSource, String sql, RowMapper<T> mapper) {
        return executeQuery(dataSource, sql, stmt -> stmt, mapper);
    }

    public int executeUpdate(DataSource dataSource, String sql, PreparedStatementSetter preparedStatementSetter) {
       return executeInternal(dataSource, sql, stmt ->
                preparedStatementSetter.setValues(stmt).executeUpdate());
    }

    public int executeUpdate(DataSource dataSource, String sql) {
       return executeUpdate(dataSource, sql, stmt -> stmt);
    }



}
