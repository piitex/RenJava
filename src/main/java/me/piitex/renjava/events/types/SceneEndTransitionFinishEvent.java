package me.piitex.renjava.events.types;

import me.piitex.engine.ui.animation.Transition;
import me.piitex.renjava.api.scenes.Scene;
import me.piitex.renjava.events.Event;

public class SceneEndTransitionFinishEvent extends Event {
    private final Scene scene;
    private final Transition transitions;
    private boolean skipped;

    public SceneEndTransitionFinishEvent(Scene scene, Transition transitions) {
        this.scene = scene;
        this.transitions = transitions;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    public Scene getScene() {
        return scene;
    }

    public Transition getTransitions() {
        return transitions;
    }
}
