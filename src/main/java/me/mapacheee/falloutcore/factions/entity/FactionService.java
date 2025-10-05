package me.mapacheee.falloutcore.factions.entity;

import com.google.inject.Inject;
import com.thewinterframework.configurate.Container;
import com.thewinterframework.service.annotation.Service;
import com.thewinterframework.service.annotation.lifecycle.OnEnable;
import me.mapacheee.falloutcore.shared.config.Config;
import me.mapacheee.falloutcore.shared.storage.SQLiteStorage;
import me.mapacheee.falloutcore.factions.event.FactionCreateEvent;
import me.mapacheee.falloutcore.factions.event.FactionDeleteEvent;
import me.mapacheee.falloutcore.factions.event.FactionJoinEvent;
import me.mapacheee.falloutcore.factions.event.FactionLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.util.*;

@Service
public class FactionService {
    private final Logger logger;
    private final Container<Config> configContainer;
    private final SQLiteStorage storage;

    private final Map<String, Faction> factions = new HashMap<>();
    private final Map<UUID, Faction> playerFactions = new HashMap<>();

    @Inject
    public FactionService(Logger logger, Container<Config> configContainer, SQLiteStorage storage) {
        this.logger = logger;
        this.configContainer = configContainer;
        this.storage = storage;
    }

    private Config config() {
        return configContainer.get();
    }

    @OnEnable
    void initialize() {
        storage.ensureTablesExist();
        loadFactions();
        logger.info("factions iniciado con {} faccciones", factions.size());
    }

    public boolean createFaction(String name, String alias) {
        if (factions.containsKey(name.toLowerCase())) {
            return false;
        }

        if (factions.size() >= config().faction().maxFactions()) {
            return false;
        }

        Faction faction = new Faction(name, alias);

        FactionCreateEvent event = new FactionCreateEvent(faction);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        factions.put(name.toLowerCase(), faction);
        saveFactions();

        logger.info("Faction '{}' created with alias '{}'", name, alias);
        return true;
    }

    public boolean deleteFaction(String name) {
        Faction faction = factions.get(name.toLowerCase());
        if (faction == null) {
            return false;
        }

        FactionDeleteEvent event = new FactionDeleteEvent(faction);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        List<UUID> members = new ArrayList<>(faction.getMembers());
        for (UUID memberId : members) {
            playerFactions.remove(memberId);
        }

        factions.remove(name.toLowerCase());
        storage.deleteFaction(name);

        logger.info("faction '{}' deleted", name);
        return true;
    }

    public boolean setFactionAlias(String name, String newAlias) {
        Faction faction = factions.get(name.toLowerCase());
        if (faction != null) {
            String oldAlias = faction.getAlias();
            faction.setAlias(newAlias);
            saveFactions();
            logger.info("faction '{}' alias changed from '{}' to '{}'", name, oldAlias, newAlias);
            return true;
        }
        return false;
    }

    public boolean joinFaction(Player player, String factionName) {
        Faction faction = factions.get(factionName.toLowerCase());
        if (faction == null) {
            return false;
        }

        if (faction.getMembers().size() >= config().faction().maxMembersPerFaction()) {
            return false;
        }

        Faction currentFaction = playerFactions.get(player.getUniqueId());

        if (currentFaction != null) {
            FactionLeaveEvent leaveEvent = new FactionLeaveEvent(player, currentFaction);
            Bukkit.getPluginManager().callEvent(leaveEvent);

            if (leaveEvent.isCancelled()) {
                return false;
            }

            currentFaction.removeMember(player);
        }

        FactionJoinEvent joinEvent = new FactionJoinEvent(player, faction);
        Bukkit.getPluginManager().callEvent(joinEvent);

        if (joinEvent.isCancelled()) {
            if (currentFaction != null) {
                currentFaction.addMember(player);
                playerFactions.put(player.getUniqueId(), currentFaction);
            }
            return false;
        }

        faction.addMember(player);
        playerFactions.put(player.getUniqueId(), faction);
        saveFactions();

        logger.info("player '{}' joined faction '{}'", player.getName(), faction.getName());
        return true;
    }

    public boolean forceJoinFaction(Player player, String factionName) {
        Faction faction = factions.get(factionName.toLowerCase());
        if (faction == null) {
            return false;
        }

        Faction currentFaction = playerFactions.get(player.getUniqueId());

        if (currentFaction != null) {
            currentFaction.removeMember(player);
        }

        faction.addMember(player);
        playerFactions.put(player.getUniqueId(), faction);
        saveFactions();

        logger.info("player '{}' was force-joined to faction '{}'", player.getName(), faction.getName());
        return true;
    }

    public boolean kickPlayer(Player player) {
        Faction faction = playerFactions.get(player.getUniqueId());
        if (faction == null) {
            return false;
        }

        FactionLeaveEvent leaveEvent = new FactionLeaveEvent(player, faction);
        Bukkit.getPluginManager().callEvent(leaveEvent);

        if (leaveEvent.isCancelled()) {
            return false;
        }

        faction.removeMember(player);
        playerFactions.remove(player.getUniqueId());
        saveFactions();

        logger.info("player '{}' was kicked from faction '{}'", player.getName(), faction.getName());
        return true;
    }

    public void setFactionBase(Player player, Location location) {
        Faction faction = playerFactions.get(player.getUniqueId());
        if (faction != null) {
            faction.setBaseLocation(location);
            saveFactions();
            logger.info("faction '{}' base location set", faction.getName());
        }
    }

    public boolean setFactionBaseByName(String factionName, Location location) {
        Faction faction = factions.get(factionName.toLowerCase());
        if (faction != null) {
            faction.setBaseLocation(location);
            saveFactions();
            logger.info("faction '{}' base location set by admin", faction.getName());
            return true;
        }
        return false;
    }

    public boolean isSameFaction(Player player1, Player player2) {
        Faction f1 = playerFactions.get(player1.getUniqueId());
        Faction f2 = playerFactions.get(player2.getUniqueId());
        return f1 != null && f1.equals(f2);
    }

    public Faction getPlayerFaction(Player player) {
        return playerFactions.get(player.getUniqueId());
    }

    public Faction getPlayerFaction(UUID playerId) {
        return playerFactions.get(playerId);
    }

    public String getFactionName(Player player) {
        Faction faction = getPlayerFaction(player);
        return faction != null ? faction.getName() : null;
    }

    public String getPlayerFactionAlias(Player player) {
        Faction faction = getPlayerFaction(player);
        return faction != null ? faction.getAlias() : null;
    }

    public Map<String, Faction> getFactions() {
        return Collections.unmodifiableMap(factions);
    }

    public boolean factionExists(String name) {
        return factions.containsKey(name.toLowerCase());
    }

    private void loadFactions() {
        Map<String, Faction> loadedFactions = storage.loadFactions();
        factions.putAll(loadedFactions);

        for (Faction faction : factions.values()) {
            for (UUID memberId : faction.getMembers()) {
                playerFactions.put(memberId, faction);
            }
        }

        logger.info("loaded {} factions from storage", factions.size());
    }

    private void saveFactions() {
        for (Faction faction : factions.values()) {
            storage.saveFaction(faction);
        }

        for (Map.Entry<UUID, Faction> entry : playerFactions.entrySet()) {
            storage.savePlayerFaction(entry.getKey(), entry.getValue().getName());
        }
    }
}
