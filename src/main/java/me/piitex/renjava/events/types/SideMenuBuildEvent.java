package me.piitex.renjava.events.types;

import me.piitex.engine.ui.containers.Container;
import me.piitex.renjava.events.Event;

public class SideMenuBuildEvent extends Event {
    private final Container container;

    public SideMenuBuildEvent(Container container) {
        this.container = container;
    }

    public Container getContainer() {
        return container;
    }
}
