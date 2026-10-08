package me.piitex.renjava.api.scenes.types;

import me.piitex.engine.ui.color.Color;
import me.piitex.engine.ui.containers.Container;
import me.piitex.engine.ui.containers.ResizableContainer;
import me.piitex.engine.ui.layout.HorizontalLayout;
import me.piitex.engine.ui.layout.Layout;
import me.piitex.engine.ui.layout.StackLayout;
import me.piitex.engine.ui.layout.VerticalLayout;
import me.piitex.engine.ui.overlays.*;
import me.piitex.engine.utils.flags.Nullable;
import me.piitex.renjava.RenJava;
import me.piitex.renjava.api.characters.Character;
import me.piitex.renjava.api.scenes.Scene;
import me.piitex.renjava.gui.StageType;

import java.io.File;
import java.util.List;

public class ImageScene extends Scene {
    @Nullable private final ImageOverlay background;
    @Nullable private Character character;
    @Nullable private String dialogue = "";

    private final RenJava renJava = RenJava.getInstance();

    public ImageScene(String id,
                      @Nullable ImageOverlay background,
                      @Nullable Character character,
                      @Nullable String dialogue) {
        super(id);
        this.background = background;
        this.character = character;
        this.dialogue = dialogue;
    }


    @Override
    public Container build() {
        double width = RenJava.getConfiguration().getWidth();
        double height = RenJava.getConfiguration().getHeight();
        double scale = width / 1920.0;

        Container container = new ResizableContainer(width, height);

        // Render background image or draw black
        if (background != null) {
            container.addElement(background);
        } else {
            container.getStyling().setBackgroundColor(Color.BLACK);
        }


        // Check if dialogue exists to render text box
        if (dialogue != null) {
            File textboxFile = new File(renJava.getGuiDirectory(), "textbox.png");

            // Stack container to stack the background image the the text box
            StackLayout dialogueBox = new StackLayout(container.getWidth(), 177);
            dialogueBox.setClipping(true);
            dialogueBox.setY(container.getHeight() - dialogueBox.getHeight());
            container.addElement(dialogueBox);

            ImageOverlay background = new ImageOverlay(textboxFile);
            dialogueBox.addElement(background);

            // Multi-layer box design
            // First is a vertical layout to separate top-box and bottom-box.
            // Top-box will have the character name and a seperator.
            // Bottom box will have the dialogue.
            VerticalLayout main = new VerticalLayout(dialogueBox.getWidth(), dialogueBox.getHeight());
            main.setAlignment(Layout.Alignment.TOP_CENTER);
            main.setClipping(true);
            main.setPadding(10);
            main.setSpacing(10);
            dialogueBox.addElement(main);

            // This is not aligned to top center. It should be aligned with the bottom box but its too far over by ~400px.
            VerticalLayout topBox = new VerticalLayout(main.getWidth(), 50);
            topBox.setAlignment(Layout.Alignment.TOP_CENTER);
            main.addElement(topBox);

            TextOverlay displayName = new TextOverlay(character.getDisplayName());
            displayName.setTextColor(character.getColor());
            displayName.setFontSize(RenJava.CONFIGURATION.getCharacterTextSize());
            topBox.addElement(displayName);

            SeparatorOverlay separatorOverlay = new SeparatorOverlay(1000);
            separatorOverlay.setLineColor(Color.GRAY);
            separatorOverlay.setOpacity(0.6f);
            topBox.addElement(separatorOverlay);

            VerticalLayout bottomBox = new VerticalLayout(main.getWidth(), 50);
            bottomBox.setAlignment(Layout.Alignment.TOP_CENTER);
            main.addElement(bottomBox);

            TextFlowOverlay text = new TextFlowOverlay(dialogue, 500);
            text.setTextColor(RenJava.CONFIGURATION.getDialogueColor());
            bottomBox.addElement(text);

        }

        // Bottom menu
        container.addElement(buildQuickMenu(width, height, scale));

        // Update/Inputs
        setContainer(container);
        handleInput();

        return container;
    }

    private HorizontalLayout buildQuickMenu(double width, double height, double scale) {
        double menuH = 30 * scale;
        HorizontalLayout quickMenu = new HorizontalLayout(0, height - menuH, width, menuH);
        quickMenu.setAlignment(Layout.Alignment.CENTER);
        quickMenu.setSpacing((float) (15 * scale));

        for (String label : List.of("Back", "History", "Skip", "Auto", "Save", "Q.Save", "Q.Load", "Prefs")) {
            ButtonOverlay button = new ButtonOverlay(label, (float) (21 * scale), Color.WHITE, 70 * scale, menuH);
            button.getStyling().setBackgroundColor(Color.TRANSPARENT);
            button.getStyling().setHoverColor(Color.TRANSPARENT);
            button.getStyling().setBorderThickness(0f);
            button.setTextColor(new Color(0.8f, 0.8f, 0.8f, 1f));
            button.getStyling().setTextHoverColor(Color.WHITE);
            button.onAction(() -> { /* hook into RenJava's action for this label */ });
            quickMenu.addElement(button);
        }
        return quickMenu;
    }
    @Override
    public StageType getStageType() {
        return StageType.IMAGE_SCENE;
    }
}
