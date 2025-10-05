package me.mapacheee.falloutcore.shared.storage;

import com.google.inject.Inject;
import com.thewinterframework.configurate.Container;
import com.thewinterframework.service.annotation.Service;
import com.thewinterframework.service.annotation.lifecycle.OnEnable;
import me.mapacheee.falloutcore.factions.entity.Faction;
import me.mapacheee.falloutcore.shared.config.Config;
import me.mapacheee.falloutcore.shared.util.DatabaseUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SQLiteStorage {

    private final Logger logger;
    private final Container<Config> configContainer;
    private final Plugin plugin;
    private final DatabaseUtils databaseUtils;

    @Inject
    public SQLiteStorage(Logger logger, Container<Config> configContainer, Plugin plugin, DatabaseUtils databaseUtils) {
        this.logger = logger;
        this.configContainer = configContainer;
        this.plugin = plugin;
        this.databaseUtils = databaseUtils;
    }

    private Config config() {
        return configContainer.get();
    }

    @OnEnable
    void initialize() {
        try {
            initializeTables();
            logger.info("database initialized");
        } catch (Exception e) {
            logger.error("error initializing database", e);
            throw new RuntimeException("failed to initialize database", e);
        }
    }

    public void ensureTablesExist() {
        try {
            initializeTables();
        } catch (Exception e) {
            logger.error("error ensuring tables exist", e);
            throw new RuntimeException("Failed to ensure tables exist", e);
        }
    }

    private void initializeTables() {
        try (Connection conn = databaseUtils.getConnection()) {
            logger.info("creating factions table...");
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS factions (" +
                            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    name TEXT UNIQUE NOT NULL," +
                            "    alias TEXT NOT NULL," +
                            "    nexus_points INTEGER DEFAULT 0," +
                            "    base_world TEXT," +
                            "    base_x REAL," +
                            "    base_y REAL," +
                            "    base_z REAL," +
                            "    nexus_world TEXT," +
                            "    nexus_x REAL," +
                            "    nexus_y REAL," +
                            "    nexus_z REAL" +
                            ");");

            logger.info("ccreating faction_players table...");
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS faction_players (" +
                            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    uuid TEXT NOT NULL," +
                            "    faction_id INTEGER NOT NULL," +
                            "    FOREIGN KEY(faction_id) REFERENCES factions(id) ON DELETE CASCADE" +
                            ");");

            logger.info("database tables created successfully");
        } catch (SQLException e) {
            logger.error("failed to initialize database tables", e);
            throw new RuntimeException("database initialization failed", e);
        }
    }

    public void saveFaction(Faction faction) {
        String sql = "INSERT OR REPLACE INTO factions (name, alias, base_x, base_y, base_z, base_world, nexus_x, nexus_y, nexus_z, nexus_world, nexus_points) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, faction.getName());
            stmt.setString(2, faction.getAlias());

            if (faction.getBaseLocation() != null) {
                Location base = faction.getBaseLocation();
                stmt.setDouble(3, base.getX());
                stmt.setDouble(4, base.getY());
                stmt.setDouble(5, base.getZ());
                stmt.setString(6, base.getWorld().getName());
            } else {
                stmt.setNull(3, Types.REAL);
                stmt.setNull(4, Types.REAL);
                stmt.setNull(5, Types.REAL);
                stmt.setNull(6, Types.VARCHAR);
            }

            if (faction.getNexusLocation() != null) {
                Location nexus = faction.getNexusLocation();
                stmt.setDouble(7, nexus.getX());
                stmt.setDouble(8, nexus.getY());
                stmt.setDouble(9, nexus.getZ());
                stmt.setString(10, nexus.getWorld().getName());
            } else {
                stmt.setNull(7, Types.REAL);
                stmt.setNull(8, Types.REAL);
                stmt.setNull(9, Types.REAL);
                stmt.setNull(10, Types.VARCHAR);
            }

            stmt.setInt(11, faction.getNexusPoints());
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("error on save factions: " + faction.getName(), e);
        }
    }

    public void deleteFaction(String factionName) {
        String sql = "DELETE FROM factions WHERE name = ?";

        try (Connection conn = databaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, factionName);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("error on delete factions: " + factionName, e);
        }
    }

    public Map<String, Faction> loadFactions() {
        Map<String, Faction> factions = new HashMap<>();
        String sql = "SELECT * FROM factions";

        try (Connection conn = databaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                String alias = rs.getString("alias");

                Faction faction = new Faction(name, alias);

                double baseX = rs.getDouble("base_x");
                if (!rs.wasNull()) {
                    double baseY = rs.getDouble("base_y");
                    double baseZ = rs.getDouble("base_z");
                    String worldName = rs.getString("base_world");
                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        faction.setBaseLocation(new Location(world, baseX, baseY, baseZ));
                    }
                }

                double nexusX = rs.getDouble("nexus_x");
                if (!rs.wasNull()) {
                    double nexusY = rs.getDouble("nexus_y");
                    double nexusZ = rs.getDouble("nexus_z");
                    String worldName = rs.getString("nexus_world");
                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        faction.setNexusLocation(new Location(world, nexusX, nexusY, nexusZ));
                    }
                }

                faction.setNexusPoints(rs.getInt("nexus_points"));
                factions.put(name.toLowerCase(), faction);

                loadFactionMembers(faction, rs.getInt("id"));
            }

        } catch (SQLException e) {
            logger.error("error on load factions", e);
        }

        return factions;
    }

    private void loadFactionMembers(Faction faction, int factionId) {
        String sql = "SELECT uuid FROM faction_players WHERE faction_id = ?";

        try (Connection conn = databaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, factionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID memberId = UUID.fromString(rs.getString("uuid"));
                    faction.getMembers().add(memberId);
                }
            }

        } catch (SQLException e) {
            logger.error("error on load faction members: " + faction.getName(), e);
        }
    }

    public void savePlayerFaction(UUID playerId, String factionName) {
        String sql = "INSERT OR REPLACE INTO faction_players (uuid, faction_id) VALUES (?, (SELECT id FROM factions WHERE name = ?))";

        try (Connection conn = databaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerId.toString());
            stmt.setString(2, factionName);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("error on save a playe of a faction: " + playerId, e);
        }
    }

    public void removePlayerFromFaction(UUID playerId) {
        String sql = "DELETE FROM faction_players WHERE uuid = ?";

        try (Connection conn = databaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerId.toString());
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("error on remove a player of a faction: {}", playerId, e);
        }
    }
}
