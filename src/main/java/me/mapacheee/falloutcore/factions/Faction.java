package me.mapacheee.falloutcore.factions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Faction {
    private final String name;
    private String alias;
    private Location baseLocation;
    private final Set<UUID> members = new HashSet<>();

    public Faction(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public void addMember(Player player) {
        members.add(player.getUniqueId());
    }

    public void removeMember(Player player) {
        members.remove(player.getUniqueId());
    }

    public boolean isMember(Player player) {
        return members.contains(player.getUniqueId());
    }

    public void setBaseLocation(Location location) {
        this.baseLocation = location;
    }

    public Location getBaseLocation() {
        return baseLocation;
    }

    public boolean hasBase() {
        return baseLocation != null;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public List<Player> getOnlineMembers() {
        List<Player> online = new ArrayList<>();
        for (UUID uuid : members) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                online.add(player);
            }
        }
        return online;
    }
}
