package me.piitex.renjava.events.types;

import me.piitex.renjava.api.scenes.Scene;
import me.piitex.renjava.events.Event;

/**
 * Called every time a new scene is displayed.
 */
public class SceneStartEvent extends Event {
    private final Scene renScene;

    public SceneStartEvent(Scene renScene) {
        this.renScene = renScene;
    }

    public Scene getScene() {
        return renScene;
    }
}
