package me.mapacheee.falloutcore.bombs.event;

import org.bukkit.event.Event;

public abstract class BombEvent extends Event {

    public BombEvent() {
        super();
    }

    public BombEvent(boolean isAsync) {
        super(isAsync);
    }
}
