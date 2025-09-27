package me.mapacheee.falloutcore.radiation.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RadiationEnterEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final int radiationLevel;
    private boolean cancelled = false;

    public RadiationEnterEvent(@NotNull Player player, int radiationLevel) {
        this.player = player;
        this.radiationLevel = radiationLevel;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public int getRadiationLevel() {
        return radiationLevel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

