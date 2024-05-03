package ua.larr4k.bedwars.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class DatabaseConnection {

    private final Connection connection;
    private final ExecutorService executor;

    public DatabaseConnection(Database dataBase, int threads) {
        try {
            connection = DriverManager.getConnection(dataBase.getConnectionString(), dataBase.getUser(), dataBase.getPassword());
            executor = Executors.newFixedThreadPool(threads);
        } catch (SQLException e) {
            throw new RuntimeException("Error establishing database connection.", e);
        }
    }

    public void prepareStatementUpdate(String query, Object... objects) {
        executor.submit(() -> executeUpdate(query, objects));
    }

    private void executeUpdate(String query, Object... objects) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < objects.length; i++) {
                statement.setObject(i + 1, objects[i]);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error executing update.", e);
        }
    }

    public Future<ResultSet> prepareStatement(String query, Object... objects) {
        return executor.submit(() -> executeQuery(query, objects));
    }

    private ResultSet executeQuery(String query, Object... objects) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < objects.length; i++) {
                statement.setObject(i + 1, objects[i]);
            }
            return statement.executeQuery();
        }
    }

    public void close() {
        try {
            connection.close();
            executor.shutdown();
        } catch (SQLException e) {
            throw new RuntimeException("Error closing database connection.", e);
        }
    }
}
