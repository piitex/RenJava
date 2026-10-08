package me.piitex.renjava.events.types;

import me.piitex.engine.ui.overlays.ButtonOverlay;
import me.piitex.renjava.events.Event;

public class ButtonClickEvent extends Event {
    private final ButtonOverlay button;

    public ButtonClickEvent(ButtonOverlay button) {
        this.button = button;
    }

    public ButtonOverlay getButton() {
        return button;
    }
}