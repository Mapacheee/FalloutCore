package me.mapacheee.falloutcore.radiation.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class RadiationEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    protected final Player player;
    protected final int radiationLevel;

    public RadiationEvent(Player player, int radiationLevel) {
        this.player = player;
        this.radiationLevel = radiationLevel;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRadiationLevel() {
        return radiationLevel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
