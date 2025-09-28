package me.mapacheee.falloutcore.factions.entity;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

public class TpaRequest {
    private final UUID senderId;
    private final UUID targetId;
    private final LocalDateTime createdAt;
    private final long expirationTimeMinutes;

    public TpaRequest(Player sender, Player target, long expirationTimeMinutes) {
        this.senderId = sender.getUniqueId();
        this.targetId = target.getUniqueId();
        this.createdAt = LocalDateTime.now();
        this.expirationTimeMinutes = expirationTimeMinutes;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(createdAt.plusMinutes(expirationTimeMinutes));
    }

    public long getExpirationTimeMinutes() {
        return expirationTimeMinutes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TpaRequest other)) return false;
        return senderId.equals(other.senderId) && targetId.equals(other.targetId);
    }

    @Override
    public int hashCode() {
        return senderId.hashCode() + targetId.hashCode();
    }
}
