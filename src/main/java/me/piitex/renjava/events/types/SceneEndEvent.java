package me.piitex.renjava.events.types;

import me.piitex.renjava.api.scenes.Scene;
import me.piitex.renjava.events.Event;

public class SceneEndEvent extends Event {
    private final Scene scene;
    private boolean autoPlayNextScene = true;

    public Scene getScene() {
        return scene;
    }

    public SceneEndEvent(Scene scene) {
        this.scene = scene;
    }

    public void setAutoPlayNextScene(boolean autoPlayNextScene) {
        this.autoPlayNextScene = autoPlayNextScene;
    }

    public boolean isAutoPlayNextScene() {
        return autoPlayNextScene;
    }
}
