package me.piitex.renjava.events.types;

import me.piitex.engine.ui.containers.Container;
import me.piitex.renjava.api.scenes.Scene;
import me.piitex.renjava.events.Event;

public class SceneBuildEvent extends Event {
    private final Scene scene;
    private final Container container;

    public SceneBuildEvent(Scene scene, Container container) {
        this.scene = scene;
        this.container = container;
    }

    public Scene getScene() {
        return scene;
    }

    public Container getContainer() {
        return container;
    }
}
