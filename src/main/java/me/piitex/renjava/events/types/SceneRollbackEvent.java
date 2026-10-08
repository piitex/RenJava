package me.piitex.renjava.events.types;

import me.piitex.renjava.api.scenes.Scene;
import me.piitex.renjava.events.Event;

public class SceneRollbackEvent extends Event {
    private final Scene toScene;
    private final Scene fromScene;

    public SceneRollbackEvent(Scene toScene, Scene fromScene) {
        this.toScene = toScene;
        this.fromScene = fromScene;
    }

    public Scene getToScene() {
        return toScene;
    }

    public Scene getFromScene() {
        return fromScene;
    }
}
