package me.mapacheee.falloutcore.bombs.event;

import me.mapacheee.falloutcore.bombs.entity.NuclearBomb;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class BombDetonateEvent extends BombEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final NuclearBomb bomb;
    private final Player activator;
    private boolean cancelled = false;

    public BombDetonateEvent(NuclearBomb bomb, Player activator) {
        this.bomb = bomb;
        this.activator = activator;
    }

    public NuclearBomb getBomb() {
        return bomb;
    }

    public Player getActivator() {
        return activator;
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
