package me.mapacheee.falloutcore.factions.event;

import me.mapacheee.falloutcore.factions.entity.Faction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class FactionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    protected final Faction faction;

    public FactionEvent(Faction faction) {
        this.faction = faction;
    }

    public Faction getFaction() {
        return faction;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
