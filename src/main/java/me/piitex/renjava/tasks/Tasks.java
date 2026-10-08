package me.piitex.renjava.tasks;

import me.piitex.engine.scheduler.Scheduler;
import me.piitex.renjava.RenJava;

public class Tasks {

    // Runs code specifically on the javafx thread.
    public static void runRendererThread(Runnable runnable) {
        Scheduler.runLater(runnable);
    }

    // This will allow code to be executed which doesn't block the javafx thread.
    // If your application is freezing use this function to run the code which causes the freezing.
    // Note you cannot access the javafx-api or Menu api using this call (aka overlays or rendering).
    public static void runAsync(Runnable runnable) {
        if (RenJava.SETTINGS.isMultiThreading()) {
            Thread thread = new Thread(runnable);
            thread.start();
        } else {
            // You can disable multi-threading in the 'settings.properties' file located in renjava directory.
            // On extreme low-end systems, disabling multi-threading can improve stability and in some cases' performance.
            // Typically, if you have a multicore cpu keep it enabled.
            runSync(runnable);
        }
    }

    // This will run code on the main thread.
    public static void runSync(Runnable runnable) {
        runnable.run();
    }
}
