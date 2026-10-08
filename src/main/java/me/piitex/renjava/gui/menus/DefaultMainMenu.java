package me.piitex.renjava.gui.menus;

import me.piitex.engine.ui.Anchor;
import me.piitex.engine.ui.color.Color;
import me.piitex.engine.ui.containers.Container;
import me.piitex.engine.ui.containers.ResizableContainer;
import me.piitex.engine.ui.layout.GridLayout;
import me.piitex.engine.ui.layout.Layout;
import me.piitex.engine.ui.layout.VerticalLayout;
import me.piitex.engine.ui.overlays.ButtonOverlay;
import me.piitex.engine.ui.overlays.ImageOverlay;
import me.piitex.engine.ui.overlays.TextOverlay;
import me.piitex.renjava.RenJava;
import me.piitex.renjava.api.saves.Save;
import me.piitex.renjava.configuration.RenJavaConfiguration;
import me.piitex.renjava.events.types.GameStartEvent;
import me.piitex.renjava.loggers.RenLogger;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class DefaultMainMenu implements MainMenu {
    private final RenJava renJava = RenJava.getInstance();
    private final RenJavaConfiguration configuration = RenJava.getConfiguration();
    double width = renJava.getGameWindow().getWindowOptions().getWidth();
    double height = renJava.getGameWindow().getWindowOptions().getHeight();

    @Override
    public Container mainMenu(boolean rightClick) {
        Container container = new ResizableContainer(width, height);

        // Background image
        ImageOverlay background = new ImageOverlay(new File(renJava.getGuiDirectory(), "main_menu.png"));
        background.setWidth(width);
        background.setHeight(height);
        container.addElement(background);

        container.addElement(sideMenu(rightClick));

        VerticalLayout layout = new VerticalLayout(600, 200);
        layout.getStyling().setBackgroundColor(Color.TRANSPARENT);
        layout.getStyling().setBorderColor(Color.TRANSPARENT);
        layout.setAlignment(Layout.Alignment.CENTER_RIGHT);
        layout.setSpacing(5);
        layout.setPosition(configuration.getWidth() - 620, configuration.getHeight() - 180);
        container.addElement(layout);

        TextOverlay title = new TextOverlay(renJava.getName());
        title.setFontFile(configuration.getUiFont());
        title.setFontSize(48);
        title.setTextColor(Color.BLUE);
        layout.addElement(title);

        TextOverlay version = new TextOverlay(renJava.getVersion());
        version.setFontFile(configuration.getUiFont());
        version.setFontSize(32);
        version.setTextColor(Color.BLUE);
        version.setTextAlignment(Layout.Alignment.CENTER_RIGHT);
        layout.addElement(version);

        return container;
    }

    @Override
    public Container sideMenu(boolean rightClick) {
        Container container = new Container(300, height);
        container.getStyling().setBackgroundColor(Color.TRANSPARENT);
        container.getStyling().setBorderColor(Color.TRANSPARENT);

        ImageOverlay background = new ImageOverlay(new File(renJava.getGuiDirectory(), "overlay/main_menu.png"));
        background.setWidth(1280);
        container.addElement(background);

        VerticalLayout items = new VerticalLayout(container.getWidth() - 50, 800);
        items.getStyling().setBackgroundColor(Color.TRANSPARENT);
        items.getStyling().setBorderColor(Color.TRANSPARENT);
        items.setAlignment(Layout.Alignment.CENTER);
        items.setSpacing(5);
        container.addElement(items);

        ButtonOverlay start = new ButtonOverlay("Start", 100, 50);
        applyStyling(start);
        start.onMouseClick(_ -> {
            RenLogger.LOGGER.info("Creating new game...");
            RenJava.PLAYER.resetSession();
            renJava.createBaseData();
            renJava.createStory();

            // Call GameStartEvent
            GameStartEvent event = new GameStartEvent(renJava);
            RenJava.getEventHandler().callEvent(event);

            renJava.start();
        });
        items.addElement(start);

        ButtonOverlay load = new ButtonOverlay((rightClick ? "Save" : "Load"), 100, 50);
        applyStyling(load);
        load.onAction(() -> {
            System.out.println("Displaying load menu...");
            renJava.getGameWindow().clear();
            renJava.getGameWindow().addContainer(loadMenu(rightClick, 1));
        });
        items.addElement(load);

        ButtonOverlay options = new ButtonOverlay("Options", 100, 50);
        applyStyling(options);
        items.addElement(options);

        ButtonOverlay about = new ButtonOverlay("About", 100, 50);
        applyStyling(about);
        items.addElement(about);

        ButtonOverlay help = new ButtonOverlay("Help", 100, 50);
        applyStyling(help);
        items.addElement(help);

        ButtonOverlay quit = new ButtonOverlay("Quit", 100, 50);
        applyStyling(quit);
        quit.onMouseClick(_ -> {
            // FIXME: Should hook into a shutdown event to properly clean before exiting
            System.exit(0);
        });
        items.addElement(quit);

        return container;
    }

    private void applyStyling(ButtonOverlay button) {
        button.setFontFile(configuration.getUiFont());
        button.setFontSize(24);
        button.setTextAlignment(Layout.Alignment.CENTER_LEFT);
        button.getStyling().setBackgroundColor(Color.TRANSPARENT);
        button.getStyling().setBorderColor(Color.TRANSPARENT);
        button.getStyling().setHoverColor(Color.TRANSPARENT);
        button.getStyling().setTextHoverColor(configuration.getHoverColor());
        button.setTextColor(configuration.getTextColor());
    }

    @Override
    public Container loadMenu(boolean rightClick, int page) {
        Container root = new ResizableContainer(width, height);

        ImageOverlay background = new ImageOverlay(new File(renJava.getGuiDirectory(), "main_menu.png"));
        background.setWidth(width);
        background.setHeight(height);
        root.addElement(background);

        Container sideMenu = sideMenu(rightClick);
        sideMenu.setAnchor(Anchor.TOP_LEFT);
        root.addElement(sideMenu);

        GridLayout grid = new GridLayout(4, root.getWidth() - 350, root.getHeight());
        grid.getStyling().setBackgroundColor(new Color(0.2f, 0.2f, 0.2f, 0.6f));
        grid.setAnchor(Anchor.CENTER, 140, 0); // Offset to account for the sidemenu.
        root.addElement(grid);

        // FIXME: Remove, for testing only
        // Get all the save files
        for (int i = 0; i < 10; i++) {
            Save save = Save.getSave(page, i);;
            if (!rightClick) {
                // Loading only, no creation
                if (save == null) {
                    continue;
                }

                ImageOverlay preview = savePreview(save, page, i);

                // TODO: Wrap this in a box with creation time and save name if custom.
                grid.addElement(preview);
            } else {
                // If the save is null add a template overlay for creating the save file.
                // Only works in the save menu, not the load menu
                System.out.println("Adding to grid...");
                grid.addElement(savePreview(save, page, i));
            }
        }

        return root;
    }

    @Override
    public Container settingMenu(boolean rightClick) {
        return null;
    }

    @Override
    public Container aboutMenu(boolean rightClick) {
        return null;
    }

    @Override
    public ImageOverlay savePreview(Save save, int page, int slot) {
        if (save != null) {
            return save.buildPreview();
        } else {
            // Basic black/gray box template (dependent on theme of course.)
            ImageOverlay image = new ImageOverlay(new File(renJava.getGuiDirectory(), "button/slot_idle_background.png"), 256, 256);
            // TODO: Engine limitations (no ability to set hover images). Will add this asap.
            image.onMouseClick(event -> {
                if (event.getAction() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    System.out.println("Creating save...");
                    Save.createSave(page, slot);
                }
            });
            return image;
        }
    }
}
