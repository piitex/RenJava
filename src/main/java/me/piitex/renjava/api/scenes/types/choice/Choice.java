package me.piitex.renjava.api.scenes.types.choice;


import me.piitex.engine.ui.overlays.ButtonOverlay;

/**
 * The Choice class represents a choice in a ChoiceScene within the RenJava framework.
 * Choices allow players to make decisions that affect the game's progression or narrative.
 * Each choice is associated with an identifier and text.
 *
 * <p>
 * To create a Choice, provide a unique identifier (id) and the text for the choice.
 * The id is used to identify the choice and can be used to retrieve the choice later.
 * The text represents the actual choice text that will be displayed to the player.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * Choice choice = new Choice("choice1", "Option 1");
 * }</pre>
 * </p>
 *
 * @see ChoiceScene
 */
public class Choice {
    private ButtonOverlay buttonOverlay;
    private final String id;
    private String text; // This can be final, but maybe someone wants to modify the text in some way? I don't know but that could be cool to see

    /**
     * Creates a Choice object with the specified identifier and text.
     *
     * @param id   The unique identifier for the choice.
     * @param text The text representing the choice that will be displayed to the player.
     */
    public Choice(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public ButtonOverlay getBuilder() {
        return buttonOverlay;
    }

    public String getId() {
        return id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}