package me.mapacheee.falloutcore.radiation.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RadiationExitEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final int previousLevel;
    private boolean cancelled = false;

    public RadiationExitEvent(@NotNull Player player, int previousLevel) {
        this.player = player;
        this.previousLevel = previousLevel;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public int getPreviousLevel() {
        return previousLevel;
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


