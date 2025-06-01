package me.mapacheee.falloutcore.storage;

import me.mapacheee.falloutcore.FalloutCore;
import me.mapacheee.falloutcore.factions.Faction;
import me.mapacheee.falloutcore.utils.DatabaseUtils;
import me.mapacheee.falloutcore.utils.MessageUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class SQLiteStorage {
    private static SQLiteStorage instance;

    private SQLiteStorage() {
        initialize();
    }

    public static SQLiteStorage getInstance() {
        if (instance == null) {
            instance = new SQLiteStorage();
        }
        return instance;
    }

    private void initialize() {
        try (Connection conn = DatabaseUtils.getConnection()) {
            // Tabla de facciones
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS factions (" +
                            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    name TEXT UNIQUE NOT NULL," +
                            "    alias TEXT UNIQUE NOT NULL" +
                            ");"
            );

            // Tabla de jugadores en facciones
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS faction_players (" +
                            "    uuid TEXT PRIMARY KEY," +
                            "    faction_id INTEGER," +
                            "    FOREIGN KEY(faction_id) REFERENCES factions(id)" +
                            ");"
            );
        } catch (SQLException e) {
            FalloutCore.getInstance().getLogger().log(Level.SEVERE, "Error al inicializar la base de datos", e);
        }
    }

    // Métodos para facciones
    public void createFaction(String name, String alias) {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO factions (name, alias) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, alias);
            ps.executeUpdate();
        } catch (SQLException e) {
            FalloutCore.getInstance().getLogger().log(Level.SEVERE, "Error al crear facción", e);
        }
    }

    public void deleteFaction(String name) {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM factions WHERE name = ?")) {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            FalloutCore.getInstance().getLogger().log(Level.SEVERE, "Error al eliminar facción", e);
        }
    }

    public void updateFactionAlias(String name, String newAlias) {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE factions SET alias = ? WHERE name = ?")) {
            ps.setString(1, newAlias);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            FalloutCore.getInstance().getLogger().log(Level.SEVERE, "Error al actualizar alias", e);
        }
    }

    public Map<String, String> loadFactions() {
        Map<String, String> factions = new HashMap<>();
        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, alias FROM factions")) {

            while (rs.next()) {
                factions.put(rs.getString("name"), rs.getString("alias"));
            }
        } catch (SQLException e) {
            FalloutCore.getInstance().getLogger().log(Level.SEVERE, "Error al cargar facciones", e);
        }
        return factions;
    }

    // Métodos para jugadores
    public void setPlayerFaction(UUID playerId, String factionName) {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT OR REPLACE INTO faction_players (uuid, faction_id) " +
                             "VALUES (?, (SELECT id FROM factions WHERE name = ?))")) {

            ps.setString(1, playerId.toString());
            ps.setString(2, factionName);
            ps.executeUpdate();
        } catch (SQLException e) {
            FalloutCore.getInstance().getLogger().log(Level.SEVERE, "Error al asignar jugador a facción", e);
        }
    }

    public void removePlayerFromFaction(UUID playerId) {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM faction_players WHERE uuid = ?")) {

            ps.setString(1, playerId.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            FalloutCore.getInstance().getLogger().log(Level.SEVERE, "Error al remover jugador de facción", e);
        }
    }

    public String getPlayerFaction(UUID playerId) {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT f.name FROM faction_players fp " +
                             "JOIN factions f ON fp.faction_id = f.id " +
                             "WHERE fp.uuid = ?")) {

            ps.setString(1, playerId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            FalloutCore.getInstance().getLogger().log(Level.SEVERE, "Error al obtener facción de jugador", e);
        }
        return null;
    }
}