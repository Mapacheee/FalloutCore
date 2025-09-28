package me.mapacheee.falloutcore.shared.util;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class DatabaseUtils {

    private final Plugin plugin;
    private final Logger logger;

    @Inject
    public DatabaseUtils(Plugin plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    public Connection getConnection() throws SQLException {
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
            logger.error("Error al inicializar la base de datos!", e);
        }
    }
}
