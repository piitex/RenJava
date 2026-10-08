package me.piitex.renjava;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import me.piitex.engine.Engine;
import me.piitex.engine.Window;
import me.piitex.engine.WindowOptions;
import me.piitex.engine.io.AppEnvironment;
import me.piitex.engine.ui.animation.FadeTransition;
import me.piitex.engine.ui.color.Color;
import me.piitex.engine.ui.color.RainbowColor;
import me.piitex.engine.ui.image.ImageLoader;
import me.piitex.engine.ui.overlays.ImageOverlay;
import me.piitex.renjava.api.characters.Character;
import me.piitex.renjava.api.scenes.types.ImageScene;
import me.piitex.renjava.api.stories.Story;
import me.piitex.renjava.configuration.Game;
import me.piitex.renjava.configuration.Configuration;
import me.piitex.renjava.configuration.InfoFile;
import me.piitex.renjava.configuration.RenJavaConfiguration;
import me.piitex.renjava.gui.GuiLoader;
import me.piitex.renjava.loggers.ApplicationLogger;
import me.piitex.renjava.loggers.RenLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.stream.Collectors;

public class Launch {
    private static final Logger log = LogManager.getLogger(Launch.class);
    private static long start;

    public static void main(String[] args) {
        // Rough execution time (not accurate)
        start = System.currentTimeMillis();

        // Initializes the Ren logger which is separated from the application logger.
        RenLogger.init();

        InfoFile buildInfo = new InfoFile(new File(System.getProperty("user.dir") + "/renjava/build.info"), false);
        if (buildInfo.containsKey("main")) {
            String mainClass = buildInfo.getString("main");
            Class<?> clazz;
            try {
                clazz = Class.forName(mainClass);
                loadClass(clazz, args, buildInfo);
            } catch (ClassNotFoundException e) {
                RenLogger.LOGGER.error("Failed to load class: {}", e.getMessage());
            }
        } else {
            RenLogger.LOGGER.error("Build info not found. Scanning for RenJava class. This will have noticeable performance impact on low end computers.");
            // Scans for all classes in all packages. (We need to do all packages because this allows the author the freedom to do their own package scheme.)
            Collection<URL> allPackagePrefixes = Arrays.stream(Package.getPackages())
                    .map(Package::getName)
                    .map(s -> s.split("\\.")[0])
                    .distinct()
                    .map(ClasspathHelper::forPackage)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            ConfigurationBuilder config = new ConfigurationBuilder().addUrls(allPackagePrefixes)
                    .addScanners(Scanners.SubTypes);
            Reflections reflections = new Reflections(config);

            // Detect any classes that extend RenJava
            // Get last execution class
            // Yea this makes no sense. Why can't you call a 'get' on a set??? What's the point of a set being a set if I can't get what's inside the set?
            Set<Class<? extends RenJava>> c1 = reflections.getSubTypesOf(RenJava.class);
            c1.forEach(aClass -> {
                RenLogger.LOGGER.info("Possible execute: " + aClass.getName());
            });

            // A subclass is compiled as a separate class with a '$'. In this example it would be 'me.pittex.renjava.Launch$1'.
            // It will try to find a class which isn't a subclass else it will load a subclass.
            Class<?> c = c1.stream().filter(aClass -> !aClass.getName().contains("$")).findAny().orElse(c1.stream().findFirst().orElse(null));
            // Should never be null because of the default execute.
            loadClass(c, args, buildInfo);
        }
    }

    private static void loadClass(Class<?> clazz, String[] args, InfoFile infoFile) {
        RenLogger.LOGGER.info("Running: '{}'", clazz.getName());
        try {

            // Creates a new instance of the application and executes the constructor.
            Object o = clazz.getDeclaredConstructor().newInstance();
            RenJava renJava = (RenJava) o;

            if (renJava.getClass().isAnnotationPresent(Game.class)) {
                Game game = renJava.getClass().getAnnotation(Game.class);
                renJava.name = game.name();
                renJava.author = game.author();
                renJava.version = game.version();
            } else {
                RenLogger.LOGGER.error("Please annotate your main class with 'Game'.");
                renJava.name = "Error";
                renJava.author = "Error";
                renJava.version = "Error";
            }


            renJava.environment = AppEnvironment.builder(renJava.name)
                    .devDataDirectory(Path.of("ren-game/"))
                    .dataDirectory(Path.of(renJava.name + "/"))
                    .build();

            // Double check base dir
            renJava.getBaseDirectory().mkdirs();
            File file = new File(renJava.getBaseDirectory(), "/renjava/");
            file.mkdirs();

            // Try to create info file if it didn't exist.
            if (!infoFile.exists()) {

                // Try to re-create
                infoFile = new InfoFile(new File(renJava.getBaseDirectory(), "/renjava/build.info"), true);
                if (!infoFile.exists()) {
                    RenLogger.LOGGER.error("Could not create the 'build.info' file. This might be a first time setup. Once the application opens please exit and relaunch.");
                    return; // This will exit the application.
                }
            }

            try {
                String jarPath = Launch.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
                        .getPath();

                RenLogger.LOGGER.info("Jar path: {}", jarPath);

                String[] split = jarPath.split("/");
                String fileName = split[split.length - 1];

                infoFile.write("main", clazz.getName());
                infoFile.write("file", fileName);

            } catch (URISyntaxException e) {
                RenLogger.LOGGER.error("Could retrieve runtime information.", e);
            }

            // Build configuration
            if (renJava.getClass().isAnnotationPresent(Configuration.class)) {
                Configuration conf = renJava.getClass().getAnnotation(Configuration.class);
                File icon = renJava.environment.getDataPath("game/images/gui/window_icon.png").toFile();
                log.info("Icon: {}", icon.getAbsolutePath());
                RenJavaConfiguration configuration = new RenJavaConfiguration(conf.title().replace("{version}", renJava.version).replace("{name}", renJava.name).replace("{author}", renJava.author),
                        conf.width(),
                        conf.height(),
                        ImageLoader.load(renJava.environment.getDataPath("game/images/gui/window_icon.png").toFile()));
                renJava.setConfiguration(configuration);
            } else {
                RenLogger.LOGGER.error("Configuration annotation not found. Please annotate your main class with 'Configuration'");
                RenJavaConfiguration configuration = new RenJavaConfiguration("Error", 1920, 1080, ImageLoader.load("gui/window_icon.png"));
                renJava.setConfiguration(configuration);
            }

            // Initialize the application logger
            ApplicationLogger applicationLogger = new ApplicationLogger(clazz);
            renJava.setLogger(applicationLogger.LOGGER);

            renJava.getLogger().info("Initialized logger...");
            renJava.preEnabled();
            renJava.init(); // Initialize game
            start(renJava);
        } catch (IOException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            RenLogger.LOGGER.error("Could initialize the RenJava framework: {}", e.getMessage());
        }
    }

    public static void start(RenJava renJava) {
        Window window = new Window(new WindowOptions(renJava.name + " v" + renJava.version).setDimensions(RenJava.getConfiguration().getWidth(), RenJava.getConfiguration().getHeight()));
        window.setBackgroundColor(Color.BLACK);
        new GuiLoader(window, RenJava.getInstance());

        long end = System.currentTimeMillis();
        long time = end - start;
        DateFormat format = new SimpleDateFormat("ss.SS");

        String s = format.format(time);
        // I hate that it displays the leading 0: 01.26s
        // Fix
        if (s.startsWith("0")) {
            s = s.replaceFirst("0", "");
        }
        log.info("Loaded in " + s + "s");

        new Engine().start(window);
    }

    /**
     * This is just a default execute for testing purposes only.
     */
    @Game(name = "Default Execute", author = "piitex", version = "1.0")
    @Configuration(title = "{name}", width = 1280, height = 720)
    private static class DefaultExecute extends RenJava {

        public DefaultExecute() {
            RenLogger.LOGGER.error("No game execute was found. Creating a default testing execute...");
            File dir = new File(System.getProperty("user.dir") + "/test/");
            dir.mkdirs();
        }

        @Override
        public void preEnabled() {
            System.out.println("Loading pre data...");
            RenJavaConfiguration configuration = getConfiguration();
            configuration.setDefaultFont(new File(getFontsDirectory(), "Roboto-Regular.ttf"));
            configuration.setUiFont(new File(getFontsDirectory(), "Roboto-Regular.ttf"));
            configuration.setStoreLocalSaves(false);
        }

        @Override
        public void createBaseData() {
            registerCharacter(new Character("ren", "Ren", new RainbowColor()) {
            });
        }

        @Override
        public Window buildSplashScreen() {
            return null;
        }

        @Override
        public void createStory() {

        }

        @Override
        public void start() {
            Story story = new Story("story") {
                @Override
                public void init() {
                    ImageOverlay background = new ImageOverlay(new File(getImagesDirectory(), "image.png"));

                    ImageScene first = new ImageScene("1", background, getCharacter("ren"), "Some text.");
                    first.setStartTransition(new FadeTransition(3f, FadeTransition.Direction.IN));
                    first.setEndTransition(new FadeTransition(3f, FadeTransition.Direction.OUT));
                    addScene(first);

                    ImageScene second = new ImageScene("2", background, getCharacter("ren"), "Even more text.");
                    second.setStartTransition(new FadeTransition(3f, FadeTransition.Direction.IN));
                    addScene(second);
                }
            };
            story.start();
        }
    }
}
