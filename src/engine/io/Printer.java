package engine.io;

import java.time.Instant;

public class Printer {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    public static void println(Object object) {
        System.out.println("[INFO " + Instant.now().toString() + "] " + object.toString());
    }

    public static void println(int level, Object object) {
        switch (level) {
            case 0 -> {
                System.err.println("[FATAL " + Instant.now().toString() + "] " + object.toString());
            }
            case 1 -> {
                System.err.println("[ERROR " + Instant.now().toString() + "] " + object.toString());
            }
            case 2 -> {
                System.out.println(ANSI_YELLOW + "[WARNING " + Instant.now().toString() + "] " + object.toString() + ANSI_RESET);
            }
            case 3 -> {
                System.out.println("[INFO " + Instant.now().toString() + "] " + object.toString());
            }
            case 4 -> {
                System.out.println("[DEBUG " + Instant.now().toString() + "] " + object.toString());
            }
        }
    }

}
