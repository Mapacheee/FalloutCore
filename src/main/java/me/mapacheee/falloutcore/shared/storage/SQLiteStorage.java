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
    private final Container<Config> config;
    private final Plugin plugin;

    @Inject
    public SQLiteStorage(Logger logger, Container<Config> config, Plugin plugin) {
        this.logger = logger;
        this.config = config;
        this.plugin = plugin;
    }

    @OnEnable
    void initialize() {
        try (Connection conn = DatabaseUtils.getConnection()) {
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS factions (" +
                            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    name TEXT UNIQUE NOT NULL," +
                            "    alias TEXT UNIQUE NOT NULL," +
                            "    base_x REAL," +
                            "    base_y REAL," +
                            "    base_z REAL," +
                            "    base_world TEXT," +
                            "    nexus_x REAL," +
                            "    nexus_y REAL," +
                            "    nexus_z REAL," +
                            "    nexus_world TEXT," +
                            "    nexus_points INTEGER DEFAULT 0" +
                            ");"
            );

            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS faction_players (" +
                            "    uuid TEXT PRIMARY KEY," +
                            "    faction_id INTEGER," +
                            "    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "    FOREIGN KEY(faction_id) REFERENCES factions(id)" +
                            ");"
            );

            logger.info("db iniciada!");
        } catch (SQLException e) {
            logger.error("error al inicializar la base de datos", e);
        }
    }

    public void saveFaction(Faction faction) {
        String sql = "INSERT OR REPLACE INTO factions (name, alias, base_x, base_y, base_z, base_world, nexus_x, nexus_y, nexus_z, nexus_world, nexus_points) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtils.getConnection();
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
            logger.error("Error al guardar facción: " + faction.getName(), e);
        }
    }

    public void deleteFaction(String factionName) {
        String sql = "DELETE FROM factions WHERE name = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, factionName);
            stmt.executeUpdate();

            String sqlPlayers = "DELETE FROM faction_players WHERE faction_id = (SELECT id FROM factions WHERE name = ?)";
            try (PreparedStatement stmtPlayers = conn.prepareStatement(sqlPlayers)) {
                stmtPlayers.setString(1, factionName);
                stmtPlayers.executeUpdate();
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar facción: " + factionName, e);
        }
    }

    public Map<String, Faction> loadFactions() {
        Map<String, Faction> factions = new HashMap<>();
        String sql = "SELECT * FROM factions";

        try (Connection conn = DatabaseUtils.getConnection();
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
            logger.error("Error al cargar facciones", e);
        }

        return factions;
    }

    private void loadFactionMembers(Faction faction, int factionId) {
        String sql = "SELECT uuid FROM faction_players WHERE faction_id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, factionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID memberId = UUID.fromString(rs.getString("uuid"));
                    faction.getMembers().add(memberId);
                }
            }

        } catch (SQLException e) {
            logger.error("Error al cargar miembros de facción: " + faction.getName(), e);
        }
    }

    public void savePlayerFaction(UUID playerId, String factionName) {
        String sql = "INSERT OR REPLACE INTO faction_players (uuid, faction_id) VALUES (?, (SELECT id FROM factions WHERE name = ?))";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerId.toString());
            stmt.setString(2, factionName);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error al guardar jugador en facción: " + playerId, e);
        }
    }

    public void removePlayerFromFaction(UUID playerId) {
        String sql = "DELETE FROM faction_players WHERE uuid = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerId.toString());
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("error al remover jugador de facción: " + playerId, e);
        }
    }
}
