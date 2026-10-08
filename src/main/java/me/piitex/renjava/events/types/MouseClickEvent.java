package me.piitex.renjava.events.types;

import me.piitex.engine.ui.events.MouseEvent;
import me.piitex.renjava.events.Cancellable;
import me.piitex.renjava.events.Event;

public class MouseClickEvent extends Event implements Cancellable {
    private final MouseEvent event;
    private boolean cancelled = false;

    public MouseClickEvent(MouseEvent event) {
        this.event = event;
    }

    public MouseEvent getEvent() {
        return event;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
