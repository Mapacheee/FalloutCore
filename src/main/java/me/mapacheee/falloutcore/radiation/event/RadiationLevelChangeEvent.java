package me.mapacheee.falloutcore.radiation.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RadiationLevelChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final int oldLevel;
    private final int newLevel;
    private final int oldHeight;
    private final int newHeight;

    public RadiationLevelChangeEvent(int oldLevel, int newLevel, int oldHeight, int newHeight) {
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.oldHeight = oldHeight;
        this.newHeight = newHeight;
    }
    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public int getOldHeight() {
        return oldHeight;
    }

    public int getNewHeight() {
        return newHeight;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}


