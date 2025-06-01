package me.mapacheee.falloutcore.utils;

import me.mapacheee.falloutcore.FalloutCore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class DatabaseUtils {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:plugins/FalloutCore/factions.db";
        return DriverManager.getConnection(url);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS factions (" +
                            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    name TEXT UNIQUE NOT NULL," +
                            "    alias TEXT UNIQUE NOT NULL" +
                            ");"
            );

            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS faction_players (" +
                            "    uuid TEXT PRIMARY KEY," +
                            "    faction_id INTEGER," +
                            "    FOREIGN KEY(faction_id) REFERENCES factions(id)" +
                            ");"
            );
        } catch (SQLException e) {
            FalloutCore.getInstance().getLogger().log(Level.SEVERE, "error at starting db!", e);
        }
    }
}
