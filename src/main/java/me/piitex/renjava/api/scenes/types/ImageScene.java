package me.piitex.renjava.api.scenes.types;

import me.piitex.engine.ui.color.Color;
import me.piitex.engine.ui.containers.Container;
import me.piitex.engine.ui.image.ImageLoader;
import me.piitex.engine.ui.layout.HorizontalLayout;
import me.piitex.engine.ui.layout.Layout;
import me.piitex.engine.ui.layout.VerticalLayout;
import me.piitex.engine.ui.overlays.ButtonOverlay;
import me.piitex.engine.ui.overlays.ImageOverlay;
import me.piitex.engine.ui.overlays.TextFlowOverlay;
import me.piitex.engine.ui.overlays.TextOverlay;
import me.piitex.engine.utils.flags.Nullable;
import me.piitex.renjava.RenJava;
import me.piitex.renjava.api.characters.Character;
import me.piitex.renjava.api.scenes.Scene;
import me.piitex.renjava.gui.StageType;
import org.jetbrains.skia.Image;

import java.io.File;
import java.util.List;

public class ImageScene extends Scene {
    @Nullable private final ImageOverlay background;
    @Nullable private Character character;
    @Nullable private String dialogue;

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

        Container container = new Container(width, height);

        // Render background image or draw black
        if (background != null) {
            container.addElement(background);
        } else {
            container.getStyling().setBackgroundColor(Color.BLACK);
        }


        // Check if dialogue exists to render text box
        if (dialogue != null) {
            File textboxFile = new File(renJava.getGuiDirectory(), "textbox.png");
            Image image = ImageLoader.load(textboxFile);
            double boxW = width;
            double boxH = image.getHeight() * (width / image.getWidth());
            double boxY = height - boxH;

            ImageOverlay textBox = new ImageOverlay(textboxFile, boxW, boxH);
            textBox.setPosition(0, boxY);
            container.addElement(textBox);

            String characterName = character.getDisplayName();
            if (characterName != null) {
                TextOverlay name = new TextOverlay(characterName, (float) (45 * scale), character.getColor());
                name.setPosition(360 * scale, boxY);
                container.addElement(name);
            }

            TextFlowOverlay text = new TextFlowOverlay(dialogue, 1116 * scale, (float) (33 * scale), Color.WHITE);
            text.setPosition(402 * scale, boxY + 75 * scale);
            container.addElement(text);
        }

        // Bottom menu
        container.addElement(buildQuickMenu(width, height, scale));

        setContainer(container);

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
