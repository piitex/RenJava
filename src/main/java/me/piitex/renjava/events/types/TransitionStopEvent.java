package me.piitex.renjava.events.types;

import me.piitex.engine.ui.animation.Transition;
import me.piitex.renjava.api.scenes.Scene;
import me.piitex.renjava.events.Event;

public class TransitionStopEvent extends Event {
    private final Transition transitions;
    private Scene scene;

    private boolean startTransition = false;

    public TransitionStopEvent(Transition transitions) {
        this.transitions = transitions;
    }

    public TransitionStopEvent(Transition transitions, Scene scene) {
        this.transitions = transitions;
        this.scene = scene;
    }

    public Transition getTransitions() {
        return transitions;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    public boolean isStartTransition() {
        return startTransition;
    }

    public void setStartTransition(boolean startTransition) {
        this.startTransition = startTransition;
    }
}
