package me.piitex.renjava.api.stories;

import me.piitex.engine.Window;
import me.piitex.engine.ui.animation.Transition;
import me.piitex.renjava.RenJava;
import me.piitex.renjava.api.scenes.Scene;
import me.piitex.renjava.events.types.SceneStartEvent;
import me.piitex.renjava.events.types.StoryEndEvent;
import me.piitex.renjava.events.types.StoryStartEvent;
import me.piitex.renjava.loggers.RenLogger;
import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

public abstract class Story {
    private final String id;

    private final LinkedHashMap<String, Scene> scenes = new LinkedHashMap<>(); // Linked maps should order by insertion.
    private final TreeMap<Integer, Scene> sceneIndexMap = new TreeMap<>();

    private Consumer<StoryStartEvent> startEventConsumer;
    private Consumer<StoryEndEvent> endEventConsumer;

    private final Logger logger = RenLogger.LOGGER;

    /**
     * Creates a base story line. This can also be referred to character events or even chapters.
     * @param id Used to get the scene later.
     */
    public Story(String id) {
        this.id = id;
        // Global var

        RenLogger.LOGGER.info("Registering story '{}'", id);
        RenJava.PLAYER.addStory(this); // Registers the story.
    }

    /**
     * Sets a handler for when the story ends.
     * @param consumer Handler for the event.
     * @return The modified Story.
     */
    public Story onEnd(Consumer<StoryEndEvent> consumer) {
        this.endEventConsumer = consumer;
        return this;
    }

    /**
     * Sets a handler for when the story starts.
     * @param consumer Handler for the event.
     * @return The modified Story.
     */
    public Story onStart(Consumer<StoryStartEvent> consumer) {
        this.startEventConsumer = consumer;
        return this;
    }

    public Consumer<StoryStartEvent> getStartEventConsumer() {
        return startEventConsumer;
    }

    public Consumer<StoryEndEvent> getEndEventConsumer() {
        return endEventConsumer;
    }

    /**
     * Abstracted initialization method. Used for adding scenes to the story.
     */
    public abstract void init();

    public String getId() {
        return id;
    }

    /**
     * Displays the first scene of the story and begins the story route.
     */
    public void start() {
        // Update RenJava Player BEFORE the scenes are added
        RenJava.PLAYER.setCurrentStory(this.getId());

        refresh();
        StoryStartEvent storyStartEvent = new StoryStartEvent(this);
        if (getStartEventConsumer() != null) {
            getStartEventConsumer().accept(storyStartEvent);
            RenJava.getEventHandler().callEvent(storyStartEvent);
        }


        Scene scene = getScene(0); // Gets the first scene index.

        if (scene == null) {
            RenLogger.LOGGER.error("Story has no scenes in index. Is the story empty?");
            return;
        }

        RenLogger.LOGGER.debug("Rendering first scene...");
        SceneStartEvent startEvent = new SceneStartEvent(scene);
        RenJava.getEventHandler().callEvent(startEvent);
        displayScene(scene, false, true);
    }

    /**
     * Clears the existing scenes and initializes the story again by calling the `init()` method.
     * This method is useful when you want to reset and update variables.
     * Every time a story starts, it is refreshed.
     */
    public void refresh() {
        clear();
        init();
    }

    /**
     * Clears all {@link Scene} mappings.
     */
    public void clear() {
        scenes.clear();
        sceneIndexMap.clear();
    }

    /**
     * Scenes are ordered the same way they are created. The first scene in a story is the first scene that was created.
     * @param scene Scene to add the story.
     */
    public void addScene(Scene scene) {
        scene.setStory(this);
        scenes.put(scene.getId(), scene);
        int index = sceneIndexMap.size();
        sceneIndexMap.put(index, scene);
        scene.setIndex(index);
    }

    /**
     * Scenes are ordered the same way they are created. The first scene in a story is the first scene that was created.
     * @param scenes All the scenes to add to the story.
     */
    public void addScenes(Scene... scenes) {
        for (Scene scene : scenes) {
            addScene(scene);
        }
    }

    /**
     * Gets the current index of the scene. Index represents the numeric order of the scenes. The first scene in a story has an index of 0.
     * @param scene The scene you want the index of.
     * @return Index of the scene provided. This will return -1 if the scene was not found.
     */
    public int getSceneIndex(Scene scene) {
        return scene.getIndex();
    }

    /**
     * Gets the last index of all the scenes.
     * @return The last index.
     */
    public int getLastIndex() {
        return scenes.size() - 1; // Always subtract one because indexing is 0 based. First entry of the index is 0 while the size will be one.
    }

    /**
     * Gets the scene based on the index
     * @param index The index of the scene you want to get.
     * @return {@link Scene} or null if the index does not exist.
     */
    public Scene getScene(int index) {
        return sceneIndexMap.get(index);
    }

    /**
     * Gets a scene by its string id.
     * @param id of the Scene
     * @return the {@link Scene} of the id or null if none found.
     */
    public Scene getScene(String id) {
        return scenes.get(id);
    }

    /**
     * Gets the next scene based on the current scene id.
     * @param id ID of the next scene.
     * @return Returns the next {@link Scene} or null.
     */
    public Scene getNextScene(String id) {
        Scene scene = scenes.get(id);
        if (scene == null) {
            return null;
        }
        int index = scene.getIndex() + 1;
        return sceneIndexMap.get(index);
    }

    /**
     * Gets the next from the current scene.
     * @return Returns the next {@link Scene} or null.
     */
    public Scene getNextSceneFromCurrent() {
        if (RenJava.PLAYER.getCurrentScene() != null) {
            return getNextScene(RenJava.PLAYER.getCurrentScene().getId());
        }
        return null;
    }

    /**
     * @return The current tracked {@link Scene} or null.
     */
    public Scene getCurrentScene() {
        return RenJava.PLAYER.getCurrentScene();
    }

    /**
     * Gets the next scene based on the current scene id.
     * @param id ID of the previous scene.
     * @return Returns the previous {@link Scene} or null.
     */
    public Scene getPreviousSceneFromID(String id) {
        Scene scene = scenes.get(id);
        int index = scene.getIndex() - 1;
        return sceneIndexMap.get(index);
    }

    /**
     * Gets the previous scene from the current scene.
     * @return Returns the previous {@link Scene} or null.
     */
    public Scene getPreviousSceneFromCurrent() {
        if (RenJava.PLAYER.getCurrentScene() != null) {
            return getPreviousSceneFromID(RenJava.PLAYER.getCurrentScene().getId());
        }
        return null;
    }

    /**
     * Renders the scene at the index.
     * @param index Index of the {@link Scene}
     */
    public void displayScene(int index) {
        Scene scene = getScene(index);
        displayScene(scene);
    }

    /**
     * Renders the scene from the id.
     * @param id Of the {@link Scene}.
     */
    public void displayScene(String id) {
        Scene scene = getScene(id);
        displayScene(scene);
    }


    /**
     * Renders the set scene.
     * @param scene The {@link Scene} to be rendered.
     */
    public void displayScene(Scene scene) {
        displayScene(scene, false);
    }

    /**
     * Renders the set scene.
     * @param scene The {@link Scene} to be rendered.
     * @param rollback If the event was being roll backed.
     */
    public void displayScene(Scene scene, boolean rollback) {
        displayScene(scene, rollback, true);
    }

    /**
     * Renders the set scene.
     * @param scene The {@link Scene} to be rendered.
     * @param rollback If the event was being roll backed.
     * @param events If the scene events should be called.
     */
    public void displayScene(Scene scene, boolean rollback, boolean events) {
        long estTime = System.currentTimeMillis();
        Window window = RenJava.getInstance().getGameWindow();

        scene.render(window, true, events);

        // Next play the transition after the scene is set and rendered. (Should be fast enough to not flicker, depends on hardware.)
        Transition transition = scene.getStartTransition();
        if (transition != null && !transition.isFinished()) {
            scene.getContainer().playTransition(transition);
        }

        RenJava.PLAYER.setCurrentStageType(scene.getStageType());
        if (!rollback) {
            // 0,1,2,3,
            RenJava.PLAYER.getViewedScenes().put(RenJava.PLAYER.getViewedScenes().size() + 1, Map.entry(scene.getId(), this.getId()));
            RenJava.PLAYER.getRolledScenes().put(RenJava.PLAYER.getRolledScenes().size() + 1, Map.entry(scene.getId(), this.getId()));
        }

        long endTime = System.currentTimeMillis() - estTime;
        DateFormat format = new SimpleDateFormat("SSSS");
        RenLogger.LOGGER.debug("Rendered scene '{}' in {}ms", scene.getId(), format.format(endTime).replaceFirst("^0*", ""));
    }

    /**
     * Renders the next {@link Scene} from the current.
     */
    public void displayNextScene() {
        Scene scene = getNextSceneFromCurrent();
        displayScene(scene);
    }

    public LinkedHashMap<String, Scene> getScenes() {
        return scenes;
    }

    public TreeMap<Integer, Scene> getSceneIndexMap() {
        return sceneIndexMap;
    }
}