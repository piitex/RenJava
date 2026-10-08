package me.piitex.renjava.gui;

import me.piitex.engine.Window;
import me.piitex.engine.WindowStyle;
import me.piitex.engine.scheduler.Scheduler;
import me.piitex.engine.ui.containers.Container;
import me.piitex.engine.ui.image.ImageLoader;
import me.piitex.renjava.RenJava;
import me.piitex.renjava.gui.menus.DefaultMainMenu;
import me.piitex.renjava.gui.menus.MainMenu;
import me.piitex.renjava.loggers.RenLogger;
import me.piitex.renjava.configuration.RenJavaConfiguration;
import me.piitex.renjava.events.types.*;
import me.piitex.renjava.tasks.Tasks;
import me.piitex.renjava.utils.MDUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loader class for loading the GUI. Starts with the splash screen first.
 */
public class GuiLoader {
    private final RenJava renJava;
    private final Window window;

    public GuiLoader(Window window, RenJava renJava) {
        this.renJava = renJava;
        this.window = window;
        buildSplashScreen();
    }

    private void buildSplashScreen() {
        RenLogger.LOGGER.info("Creating Splash screen...");
        window.setStyle(WindowStyle.BORDERLESS);
        // Update Stage
        RenJava.PLAYER.setCurrentStageType(StageType.MAIN_MENU);

        Window window = renJava.buildSplashScreen();
        if (window == null) {
            RenLogger.LOGGER.warn("No splash screen was rendered..");
            renJavaFrameworkBuild();
            buildMainMenu();
            return; // Don't create a splash screen if one wasn't set.
        }

        Tasks.runAsync(this::renJavaFrameworkBuild);

        Scheduler.after(3f, () -> {
            window.hide();
            buildMainMenu();
        });
    }

    private void renJavaFrameworkBuild() {
        RenLogger.LOGGER.info("Creating base data...");
        renJava.createBaseData();
        RenLogger.LOGGER.info("Creating story...");
        renJava.createStory();
        postProcess();
    }

    private void buildMainMenu() {
        // Gonna put some default checks here.

        RenJavaConfiguration configuration = RenJava.CONFIGURATION;
        if (configuration.getDefaultFont() == null) {
            RenLogger.LOGGER.error("Default font not set.");
//            RenJava.CONFIGURATION.setDefaultFont(new FontLoader("Arial", 24));
        }
        if (configuration.getUiFont() == null) {
            RenLogger.LOGGER.error("UI font not set.");
//            RenJava.CONFIGURATION.setUiFont(new FontLoader("Arial", 26));
        }
        if (configuration.getCharacterDisplayFont() == null) {
            RenLogger.LOGGER.warn("Character display font not set.");
//            RenJava.CONFIGURATION.setCharacterDisplayFont(new FontLoader("Arial", 26));
        }
        if (configuration.getDialogueFont() == null) {
            RenLogger.LOGGER.warn("Dialogue font not set.");
//            RenJava.CONFIGURATION.setDialogueFont(new FontLoader("Arial", 26));
        }
        if (configuration.getChoiceButtonFont() == null) {
            RenLogger.LOGGER.warn("Choice button font not set.");
//            RenJava.CONFIGURATION.setChoiceButtonFont(new FontLoader("Arial", 28));
        }

        // Preset width and height
        RenJava.CONFIGURATION.setCurrentWindowWidth(RenJava.CONFIGURATION.getWidth());
        RenJava.CONFIGURATION.setCurrentWindowHeight(RenJava.CONFIGURATION.getHeight());

        RenLogger.LOGGER.info("Rendering main menu...");
        // When building title screen create a new window and eventually store the window for easy access
        //Window newWindow = new Window(RenJava.CONFIGURATION.getGameTitle(), StageStyle.DECORATED, new ImageLoader("gui/window_icon.png"));
        // Specifically for the gameWindow it is needed to setup the shutdown events.

        // TODO: Update window path
        window.getWindowOptions().setStyle(WindowStyle.STANDARD)
                .setIcon(ImageLoader.load(new File(RenJava.getInstance().getBaseDirectory(), "game/images/gui/window_icon.png")));
        window.onClose(() -> {
            ShutdownEvent shutdownEvent = new ShutdownEvent();
            RenJava.getEventHandler().callEvent(shutdownEvent);

            RenJava.ADDONLOADER.disable();

            // Transfer saves to localsaves
            File localSaves = new File(System.getenv("APPDATA") + "/RenJava/" + renJava.getID() + "/saves/");
            for (File file : renJava.getSaves()) {
                File newDirFile = new File(localSaves, file.getName());

                // If the save file already exists check to see if the saves are different. If they are different, replace.
                if (newDirFile.exists()) {
                    String localSaveChecksum = MDUtils.getFileCheckSum(newDirFile);
                    String currentSaveChecksum = MDUtils.getFileCheckSum(file);
                    RenLogger.LOGGER.debug("Local Save : {}", localSaveChecksum);
                    RenLogger.LOGGER.debug("Current Save: {}", currentSaveChecksum);
                    if (localSaveChecksum.equalsIgnoreCase(currentSaveChecksum)) continue;
                    newDirFile.delete();
                }
                try {
                    Files.copy(Path.of(file.getPath()), Path.of(newDirFile.getPath()));
                    RenLogger.LOGGER.info("Copied '{}' to local saves.", file.getName());
                } catch (IOException ignored) {
                    // If caught ignore and let the application close.
                }
            }
        });


        renJava.setGameWindow(window);

        MainMenu menu = renJava.getMainMenu();


        // Check if the menu is null
        if (menu == null) {
            RenLogger.LOGGER.error("No title screen was found. Please customize your own title screen for better user experience.");
            RenLogger.LOGGER.warn("Building RenJava default title screen...");
            menu = new DefaultMainMenu();
            renJava.setMainMenu(menu);
        }

        // Render main menu
        Container container = menu.mainMenu(false);
        MainMenuBuildEvent event = new MainMenuBuildEvent(container);
        RenJava.getEventHandler().callEvent(event);
        window.addContainer(container);

        Container sideMenu = menu.sideMenu(false);

        container.addElement(sideMenu);

        MainMenuDispatchEvent dispatchEvent = new MainMenuDispatchEvent(container);
        RenJava.getEventHandler().callEvent(dispatchEvent);

        window.setMaximized(configuration.isMaximizedGameWindow());

        MainMenuRenderEvent renderEvent = new MainMenuRenderEvent(container, false);
        RenJava.getEventHandler().callEvent(renderEvent);

        RenJava.PLAYER.setCurrentStageType(StageType.MAIN_MENU);

    }


    private void postProcess() {
        RenJava.ADDONLOADER.load();
    }
}
