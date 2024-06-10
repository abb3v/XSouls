package me.abb3v.xsouls.services;

import me.abb3v.xsouls.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

public class Souls {
    private final Logger logger;
    private final Connection connection;

    public Souls(Logger logger) {
        this.logger = logger;
        this.connection = DatabaseManager.getInstance(logger).getConnection();
    }

    public int getSouls(UUID uuid) {
        String sql = "SELECT souls FROM souls WHERE uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("souls");
            }
        } catch (SQLException e) {
            logger.severe("Could not get souls: " + e.getMessage());
        }
        return 0;
    }

    public void setSouls(UUID uuid, int souls) {
        String sql = "INSERT INTO souls(uuid, souls) VALUES(?, ?) ON CONFLICT(uuid) DO UPDATE SET souls = excluded.souls";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setInt(2, souls);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Could not set souls: " + e.getMessage());
        }
    }

    public void addSouls(UUID uuid, int amount) {
        int currentSouls = getSouls(uuid);
        setSouls(uuid, currentSouls + amount);
    }

    public boolean removeSouls(UUID uuid, int amount) {
        int currentSouls = getSouls(uuid);
        if (currentSouls >= amount) {
            setSouls(uuid, currentSouls - amount);
            return true;
        } else {
            return false;
        }
    }
}
