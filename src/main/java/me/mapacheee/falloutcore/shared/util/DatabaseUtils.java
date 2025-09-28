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
}
