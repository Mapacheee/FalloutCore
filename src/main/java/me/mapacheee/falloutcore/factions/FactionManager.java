package me.mapacheee.falloutcore.factions;

import me.mapacheee.falloutcore.FalloutCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FactionManager {
    private static FactionManager instance;
    private final Map<String, Faction> factions = new HashMap<>();
    private final Map<UUID, Faction> playerFactions = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;

    private FactionManager() {
        loadFactions();
    }

    public static FactionManager getInstance() {
        if (instance == null) {
            instance = new FactionManager();
        }
        return instance;
    }

    public boolean createFaction(String name, String alias) {
        if (factions.containsKey(name.toLowerCase())) {
            return false;
        }
        Faction faction = new Faction(name, alias);
        factions.put(name.toLowerCase(), faction);
        saveFactions();
        return true;
    }

    public boolean deleteFaction(String name) {
        Faction faction = factions.remove(name.toLowerCase());
        if (faction != null) {

            for (UUID memberId : faction.getMembers()) {
                playerFactions.remove(memberId);
            }
            saveFactions();
            return true;
        }
        return false;
    }

    public boolean setFactionAlias(String name, String newAlias) {
        Faction faction = factions.get(name.toLowerCase());
        if (faction != null) {
            faction.setAlias(newAlias);
            saveFactions();
            return true;
        }
        return false;
    }

    public boolean joinFaction(Player player, String factionName) {
        Faction faction = factions.get(factionName.toLowerCase());
        if (faction == null) {
            return false;
        }

        Faction current = playerFactions.get(player.getUniqueId());
        if (current != null) {
            current.removeMember(player);
        }

        faction.addMember(player);
        playerFactions.put(player.getUniqueId(), faction);
        saveFactions();
        return true;
    }

    public boolean forceJoinFaction(Player player, String factionName) {

        Faction current = playerFactions.get(player.getUniqueId());
        if (current != null) {
            current.removeMember(player);
        }

        return joinFaction(player, factionName);
    }

    public boolean kickPlayer(Player player) {
        Faction faction = playerFactions.remove(player.getUniqueId());
        if (faction != null) {
            faction.removeMember(player);
            saveFactions();
            return true;
        }
        return false;
    }

    public void setFactionBase(Player player, Location location) {
        Faction faction = playerFactions.get(player.getUniqueId());
        if (faction != null) {
            faction.setBaseLocation(location);
            saveFactions();
        }
    }

    public boolean isSameFaction(Player player1, Player player2) {
        Faction f1 = playerFactions.get(player1.getUniqueId());
        Faction f2 = playerFactions.get(player2.getUniqueId());
        return f1 != null && f2 != null && f1.equals(f2);
    }

    public Faction getPlayerFaction(Player player) {
        return playerFactions.get(player.getUniqueId());
    }

    public String getFactionName(Player player) {
        Faction faction = getPlayerFaction(player);
        return faction != null ? faction.getName() : "Sin facción";
    }

    public String getPlayerFactionAlias(Player player) {
        Faction faction = getPlayerFaction(player);
        return faction != null ? faction.getAlias() : "";
    }

    private void loadFactions() {
        dataFile = new File(FalloutCore.getInstance().getDataFolder(), "factions.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection factionsSection = dataConfig.getConfigurationSection("factions");
        if (factionsSection != null) {
            for (String factionName : factionsSection.getKeys(false)) {
                ConfigurationSection factionSection = factionsSection.getConfigurationSection(factionName);

                String alias = factionSection.getString("alias", factionName);
                Faction faction = new Faction(factionName, alias);

                List<String> members = factionSection.getStringList("members");
                for (String uuidStr : members) {
                    UUID uuid = UUID.fromString(uuidStr);
                    playerFactions.put(uuid, faction);
                    faction.getMembers().add(uuid);
                }

                if (factionSection.contains("base")) {
                    Location base = (Location) factionSection.get("base");
                    faction.setBaseLocation(base);
                }

                factions.put(factionName.toLowerCase(), faction);
            }
        }
    }

    public void saveFactions() {
        dataConfig.set("factions", null);

        for (Faction faction : factions.values()) {
            String key = "factions." + faction.getName();

            dataConfig.set(key + ".alias", faction.getAlias());

            List<String> members = new ArrayList<>();
            for (UUID uuid : faction.getMembers()) {
                members.add(uuid.toString());
            }
            dataConfig.set(key + ".members", members);

            if (faction.hasBase()) {
                dataConfig.set(key + ".base", faction.getBaseLocation());
            }
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}