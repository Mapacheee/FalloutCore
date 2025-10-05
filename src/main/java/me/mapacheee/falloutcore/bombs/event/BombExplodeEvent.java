package me.mapacheee.falloutcore.bombs.event;

import me.mapacheee.falloutcore.bombs.entity.NuclearBomb;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class BombExplodeEvent extends BombEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final NuclearBomb bomb;
    private boolean cancelled = false;

    public BombExplodeEvent(NuclearBomb bomb) {
        this.bomb = bomb;
    }

    public NuclearBomb getBomb() {
        return bomb;
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
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
