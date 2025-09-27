package me.mapacheee.falloutcore.factions.entity;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Faction {
    private final String name;
    private String alias;
    private Location baseLocation;
    private Location nexusLocation;
    private int nexusPoints = 0;
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

    public void setNexusLocation(Location location) {
        this.nexusLocation = location;
    }

    public Location getNexusLocation() {
        return nexusLocation;
    }

    public boolean hasNexus() {
        return nexusLocation != null;
    }

    public int getNexusPoints() {
        return nexusPoints;
    }

    public void setNexusPoints(int points) {
        this.nexusPoints = points;
    }

    public void addNexusPoints(int points) {
        this.nexusPoints += points;
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

    public Set<UUID> getMembersReadOnly() {
        return Collections.unmodifiableSet(members);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Faction faction = (Faction) obj;
        return Objects.equals(name, faction.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Faction{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", members=" + members.size() +
                ", nexusPoints=" + nexusPoints +
                '}';
    }
}
