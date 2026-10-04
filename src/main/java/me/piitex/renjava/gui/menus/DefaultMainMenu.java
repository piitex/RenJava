package me.piitex.renjava.gui.menus;

import me.piitex.engine.ui.color.Color;
import me.piitex.engine.ui.containers.Container;
import me.piitex.engine.ui.containers.ResizableContainer;
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

import java.io.File;

public class DefaultMainMenu implements MainMenu {
    private final RenJava renJava = RenJava.getInstance();
    private final RenJavaConfiguration configuration = RenJava.getConfiguration();

    @Override
    public Container mainMenu(boolean rightClick) {
        double width = renJava.getGameWindow().getWindowOptions().getWidth();
        double height = renJava.getGameWindow().getWindowOptions().getWidth();
        Container container = new ResizableContainer(width, height);

        // Background image
        ImageOverlay background = new ImageOverlay(new File(renJava.getGuiDirectory(), "main_menu.png"));
        background.setWidth(width);
        background.setHeight(height);
        container.addElement(background);

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
        Container container = new Container(300, configuration.getHeight());
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

        ButtonOverlay load = new ButtonOverlay("Load", 100, 50);
        applyStyling(load);
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
    public Container loadMenu(boolean rightClick, int page, boolean loadMenu) {
        return null;
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
    public ButtonOverlay savePreview(Save save, int page, int index) {
        return null;
    }
}
