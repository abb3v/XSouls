package me.abb3v.xsouls.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final Logger logger;
    private Connection connection;

    private DatabaseManager(Logger logger) {
        this.logger = logger;
        connect();
        createTable();
    }

    public static synchronized DatabaseManager getInstance(Logger logger) {
        if (instance == null) {
            instance = new DatabaseManager(logger);
        }
        return instance;
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:xsouls.db");
        } catch (SQLException | ClassNotFoundException e) {
            logger.severe("Could not connect to SQLite database: " + e.getMessage());
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS souls (uuid TEXT PRIMARY KEY, souls INTEGER DEFAULT 0)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            logger.severe("Could not create table: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
