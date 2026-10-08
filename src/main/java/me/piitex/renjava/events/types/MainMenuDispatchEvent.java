package me.piitex.renjava.events.types;

import me.piitex.engine.ui.containers.Container;
import me.piitex.renjava.events.Event;

public class MainMenuDispatchEvent extends Event {
    private final Container menu;

    public MainMenuDispatchEvent(Container menu) {
        this.menu = menu;
    }

    public Container getMenu() {
        return menu;
    }
}
