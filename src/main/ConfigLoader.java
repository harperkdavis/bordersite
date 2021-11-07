package main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class ConfigLoader {

    private final static String configPath = "/Bordersite/config/config.properties";

    public static void loadConfig() {
        String pathString = System.getenv("LOCALAPPDATA") + configPath;
        Path path = Paths.get(pathString);
        if (Files.exists(path)) {
            readConfig(path);
        } else {
            try {
                saveDefaultConfig(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveConfig() {
        String pathString = System.getenv("LOCALAPPDATA") + configPath;
        Path path = Paths.get(pathString);
        try {
            if (Files.exists(path)) {
                setConfig(path);
            } else {
                saveDefaultConfig(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetConfig() {
        String pathString = System.getenv("LOCALAPPDATA") + configPath;
        Path path = Paths.get(pathString);
        try {
            saveDefaultConfig(path);
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readConfig(Path path) {
        Properties prop = getConfig(path);

        if (prop == null) {
            try {
                saveDefaultConfig(path);
                prop = getConfig(path);
                if (prop == null) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        Global.WINDOW_WIDTH = Integer.parseInt(prop.getProperty("display;width", "1920"));
        Global.WINDOW_HEIGHT = Integer.parseInt(prop.getProperty("display;height", "1080"));
        Global.FULLSCREEN = Boolean.parseBoolean(prop.getProperty("display;fullscreen", "true"));

        Global.MOUSE_SENSITIVITY_X = Float.parseFloat(prop.getProperty("input;mouse_sensitivity_x", "1"));
        Global.MOUSE_SENSITIVITY_Y = Float.parseFloat(prop.getProperty("input;mouse_sensitivity_y", "1"));

        Global.MOUSE_INVERT_X = Boolean.parseBoolean(prop.getProperty("input;mouse_invert_x", "false"));
        Global.MOUSE_INVERT_Y = Boolean.parseBoolean(prop.getProperty("input;mouse_invert_x", "false"));

        Global.keybinds.clear();

        Global.keybinds.put("move_forward", prop.getProperty("keybind;move_forward", "w"));
        Global.keybinds.put("move_backward", prop.getProperty("keybind;move_backward", "s"));
        Global.keybinds.put("move_left", prop.getProperty("keybind;move_left", "a"));
        Global.keybinds.put("move_right", prop.getProperty("keybind;move_right", "d"));

        Global.keybinds.put("interact", prop.getProperty("keybind;interact", "f"));

        Global.keybinds.put("jump", prop.getProperty("keybind;jump", "space"));

        Global.keybinds.put("sprint", prop.getProperty("keybind;sprint", "left_shift"));
        Global.keybinds.put("crouch", prop.getProperty("keybind;crouch", "left_control"));

        Global.keybinds.put("shoot", prop.getProperty("keybind;shoot", "mouse0"));
        Global.keybinds.put("aim", prop.getProperty("keybind;aim", "mouse1"));
        Global.keybinds.put("reload", prop.getProperty("keybind;reload", "r"));

        Global.keybinds.put("chat", prop.getProperty("keybind;chat", "y"));

    }

    public static void setConfig(Path path) throws IOException {
        Properties prop = getConfig(path);
        if (prop == null) {
            try {
                saveDefaultConfig(path);
                prop = getConfig(path);
                if (prop == null) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        prop.setProperty("display;width", String.valueOf(Global.WINDOW_WIDTH));
        prop.setProperty("display;height", String.valueOf(Global.WINDOW_HEIGHT));
        prop.setProperty("display;fullscreen", String.valueOf(Global.FULLSCREEN));

        prop.setProperty("input;mouse_sensitivity_x", String.valueOf(Global.MOUSE_SENSITIVITY_X));
        prop.setProperty("input;mouse_sensitivity_y", String.valueOf(Global.MOUSE_SENSITIVITY_Y));

        prop.setProperty("input;mouse_invert_x", String.valueOf(Global.MOUSE_INVERT_X));
        prop.setProperty("input;mouse_invert_x", String.valueOf(Global.MOUSE_INVERT_Y));

        prop.setProperty("keybind;move_forward", Global.keybinds.get("move_forward"));
        prop.setProperty("keybind;move_backward", Global.keybinds.get("move_backward"));
        prop.setProperty("keybind;move_left", Global.keybinds.get("move_left"));
        prop.setProperty("keybind;move_right", Global.keybinds.get("move_right"));

        prop.setProperty("keybind;interact", Global.keybinds.get("interact"));

        prop.setProperty("keybind;jump", Global.keybinds.get("jump"));

        prop.setProperty("keybind;sprint", Global.keybinds.get("sprint"));
        prop.setProperty("keybind;crouch", Global.keybinds.get("crouch"));

        prop.setProperty("keybind;shoot", Global.keybinds.get("shoot"));
        prop.setProperty("keybind;aim", Global.keybinds.get("aim"));
        prop.setProperty("keybind;reload", Global.keybinds.get("reload"));

        prop.setProperty("keybind;chat", Global.keybinds.get("chat"));

        prop.store(new FileOutputStream(path.toFile()), null);
    }

    private static Properties getConfig(Path path) {
        Properties properties = new Properties();
        InputStream inputStream;

        try {
            inputStream = Files.newInputStream(path);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return properties;
    }

    public static void saveDefaultConfig(Path path) throws IOException {
        Properties prop = new Properties();

        prop.setProperty("display;width", "1920");
        prop.setProperty("display;height", "1080");
        prop.setProperty("display;fullscreen", "true");

        prop.setProperty("input;mouse_sensitivity_x", "1");
        prop.setProperty("input;mouse_sensitivity_y", "1");

        prop.setProperty("input;mouse_invert_x", "false");
        prop.setProperty("input;mouse_invert_x", "false");

        prop.setProperty("keybind;move_forward", "w");
        prop.setProperty("keybind;move_backward", "s");
        prop.setProperty("keybind;move_left", "a");
        prop.setProperty("keybind;move_right", "d");

        prop.setProperty("keybind;interact", "f");

        prop.setProperty("keybind;jump", "space");

        prop.setProperty("keybind;sprint", "left_shift");
        prop.setProperty("keybind;crouch", "left_control");

        prop.setProperty("keybind;shoot", "mouse0");
        prop.setProperty("keybind;aim", "mouse1");
        prop.setProperty("keybind;reload", "r");

        prop.setProperty("keybind;chat", "y");

        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            File newFile = path.toFile();
            newFile.createNewFile();
        }
        prop.store(new FileOutputStream(path.toFile()), null);
    }

}
