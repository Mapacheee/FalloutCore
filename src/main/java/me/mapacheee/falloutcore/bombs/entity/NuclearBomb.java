package me.mapacheee.falloutcore.bombs.entity;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

public class NuclearBomb {

    private final UUID id;
    private final Location location;
    private final Player activator;
    private final LocalDateTime activationTime;
    private final int countdownSeconds;
    private boolean isActive;
    private boolean hasExploded;

    public NuclearBomb(Location location, Player activator, int countdownSeconds) {
        this.id = UUID.randomUUID();
        this.location = location.clone();
        this.activator = activator;
        this.activationTime = LocalDateTime.now();
        this.countdownSeconds = countdownSeconds;
        this.isActive = true;
        this.hasExploded = false;
    }

    public UUID getId() {
        return id;
    }

    public Location getLocation() {
        return location.clone();
    }

    public Player getActivator() {
        return activator;
    }

    public LocalDateTime getActivationTime() {
        return activationTime;
    }

    public int getCountdownSeconds() {
        return countdownSeconds;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean hasExploded() {
        return hasExploded;
    }

    public void setExploded(boolean exploded) {
        this.hasExploded = exploded;
    }

    public long getSecondsUntilDetonation() {
        return activationTime.plusSeconds(countdownSeconds).getSecond() - LocalDateTime.now().getSecond();
    }

    public boolean shouldDetonate() {
        return LocalDateTime.now().isAfter(activationTime.plusSeconds(countdownSeconds));
    }
}
