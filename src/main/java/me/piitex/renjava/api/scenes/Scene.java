package me.piitex.renjava.api.scenes;

import me.piitex.engine.Window;
import me.piitex.engine.ui.animation.Transition;
import me.piitex.engine.ui.containers.Container;
import me.piitex.renjava.api.stories.Story;
import me.piitex.renjava.events.types.SceneBuildEvent;
import me.piitex.renjava.events.types.SceneEndEvent;
import me.piitex.renjava.events.types.SceneStartEvent;
import me.piitex.renjava.gui.StageType;

import java.util.function.Consumer;

public abstract class Scene {
    private final String id;
    private Container container;
    private Story story;
    private int index;
    private Transition startTransition;
    private Transition endTransition;

    // Event handlers
    private Consumer<SceneStartEvent> startEventConsumer;
    private Consumer<SceneEndEvent> endEventConsumer;
    private Consumer<SceneBuildEvent> buildEventConsumer;

    public Scene(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Transition getStartTransition() {
        return startTransition;
    }

    public void setStartTransition(Transition startTransition) {
        this.startTransition = startTransition;
    }

    public Transition getEndTransition() {
        return endTransition;
    }

    public void setEndTransition(Transition endTransition) {
        this.endTransition = endTransition;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public Consumer<SceneStartEvent> getStartEventConsumer() {
        return startEventConsumer;
    }

    public void onStart(Consumer<SceneStartEvent> startEventConsumer) {
        this.startEventConsumer = startEventConsumer;
    }

    public Consumer<SceneEndEvent> getEndEventConsumer() {
        return endEventConsumer;
    }

    public void onEnd(Consumer<SceneEndEvent> endEventConsumer) {
        this.endEventConsumer = endEventConsumer;
    }

    public Consumer<SceneBuildEvent> getBuildEventConsumer() {
        return buildEventConsumer;
    }

    public void onBuild(Consumer<SceneBuildEvent> buildEventConsumer) {
        this.buildEventConsumer = buildEventConsumer;
    }

    protected void setContainer(Container container) {
        this.container = container;
    }

    public abstract Container build();

    public abstract StageType getStageType();

    public void render(Window window, boolean ui, boolean events) {
        // This is called for when the scene needs to be rendered.
        // The first thing to do is clear the window
        window.getContainers().clear();

        // Next add the container to the window
        window.addContainer(build());
    }
}