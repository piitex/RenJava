package me.piitex.renjava.events.types;

import me.piitex.renjava.api.scenes.Scene;
import me.piitex.renjava.events.Event;

public class SceneAnimationStartEvent extends Event {
    private final Scene scene;

    public SceneAnimationStartEvent(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }
}
