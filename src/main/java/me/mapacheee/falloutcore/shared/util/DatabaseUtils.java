package me.mapacheee.falloutcore.shared.util;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DatabaseUtils {

    @Inject
    private static Plugin plugin;

    @Inject
    private Logger logger;

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:" + plugin.getDataFolder().getPath() + "/factions.db";
        return DriverManager.getConnection(url);
    }

    public void initializeDatabase() {
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

            logger.info("Base de datos inicializada correctamente");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al inicializar la base de datos!", e);
        }
    }
}
