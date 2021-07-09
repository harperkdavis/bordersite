package main;

import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class Global {

    public final static boolean BUILD_MODE = false;
    public static boolean RENDER_WIREFRAME = false;

    public final static Map<String, Integer> bindNames = new HashMap<>();
    public final static Map<Integer, String> typeLowercase = new HashMap<>(), typeUppercase = new HashMap<>();
    public final static Map<String, String> keybinds = new HashMap<>();

    public static void init() {

        keybinds.put("move_forward", "w");
        keybinds.put("move_backward", "s");
        keybinds.put("move_left", "a");
        keybinds.put("move_right", "d");

        keybinds.put("jump", "space");

        keybinds.put("shoot", "mouse0");
        keybinds.put("aim", "mouse1");

        keybinds.put("chat", "y");

        bindNames.put("a", GLFW.GLFW_KEY_A);
        bindNames.put("b", GLFW.GLFW_KEY_B);
        bindNames.put("c", GLFW.GLFW_KEY_C);
        bindNames.put("d", GLFW.GLFW_KEY_D);
        bindNames.put("e", GLFW.GLFW_KEY_E);
        bindNames.put("f", GLFW.GLFW_KEY_F);
        bindNames.put("g", GLFW.GLFW_KEY_G);
        bindNames.put("h", GLFW.GLFW_KEY_H);
        bindNames.put("i", GLFW.GLFW_KEY_I);
        bindNames.put("j", GLFW.GLFW_KEY_J);
        bindNames.put("k", GLFW.GLFW_KEY_K);
        bindNames.put("l", GLFW.GLFW_KEY_L);
        bindNames.put("m", GLFW.GLFW_KEY_M);
        bindNames.put("n", GLFW.GLFW_KEY_N);
        bindNames.put("o", GLFW.GLFW_KEY_O);
        bindNames.put("p", GLFW.GLFW_KEY_P);
        bindNames.put("q", GLFW.GLFW_KEY_Q);
        bindNames.put("r", GLFW.GLFW_KEY_R);
        bindNames.put("s", GLFW.GLFW_KEY_S);
        bindNames.put("t", GLFW.GLFW_KEY_T);
        bindNames.put("u", GLFW.GLFW_KEY_U);
        bindNames.put("v", GLFW.GLFW_KEY_V);
        bindNames.put("w", GLFW.GLFW_KEY_W);
        bindNames.put("x", GLFW.GLFW_KEY_X);
        bindNames.put("y", GLFW.GLFW_KEY_Y);
        bindNames.put("z", GLFW.GLFW_KEY_Z);
        bindNames.put("space", GLFW.GLFW_KEY_SPACE);
        bindNames.put("caps_lock", GLFW.GLFW_KEY_CAPS_LOCK);
        bindNames.put("left_shift", GLFW.GLFW_KEY_LEFT_SHIFT);
        bindNames.put("right_shift", GLFW.GLFW_KEY_RIGHT_SHIFT);
        bindNames.put("tab", GLFW.GLFW_KEY_TAB);
        bindNames.put("left_control", GLFW.GLFW_KEY_LEFT_CONTROL);
        bindNames.put("left_alt", GLFW.GLFW_KEY_LEFT_ALT);
        bindNames.put("right_control", GLFW.GLFW_KEY_RIGHT_CONTROL);
        bindNames.put("right_alt", GLFW.GLFW_KEY_RIGHT_ALT);
        bindNames.put("-", GLFW.GLFW_KEY_MINUS);
        bindNames.put("=", GLFW.GLFW_KEY_EQUAL);
        bindNames.put("[", GLFW.GLFW_KEY_LEFT_BRACKET);
        bindNames.put("]", GLFW.GLFW_KEY_RIGHT_BRACKET);
        bindNames.put(",", GLFW.GLFW_KEY_COMMA);
        bindNames.put(".", GLFW.GLFW_KEY_PERIOD);
        bindNames.put(";", GLFW.GLFW_KEY_SEMICOLON);
        bindNames.put("'", GLFW.GLFW_KEY_APOSTROPHE);
        bindNames.put("/", GLFW.GLFW_KEY_SLASH);
        bindNames.put("\\", GLFW.GLFW_KEY_BACKSLASH);
        bindNames.put("`", GLFW.GLFW_KEY_GRAVE_ACCENT);
        bindNames.put("esc", GLFW.GLFW_KEY_ESCAPE);
        bindNames.put("backspace", GLFW.GLFW_KEY_BACKSPACE);
        bindNames.put("arrow_left", GLFW.GLFW_KEY_LEFT);
        bindNames.put("arrow_right", GLFW.GLFW_KEY_RIGHT);
        bindNames.put("arrow_up", GLFW.GLFW_KEY_UP);
        bindNames.put("arrow_down", GLFW.GLFW_KEY_DOWN);
        bindNames.put("1", GLFW.GLFW_KEY_1);
        bindNames.put("2", GLFW.GLFW_KEY_2);
        bindNames.put("3", GLFW.GLFW_KEY_3);
        bindNames.put("4", GLFW.GLFW_KEY_4);
        bindNames.put("5", GLFW.GLFW_KEY_5);
        bindNames.put("6", GLFW.GLFW_KEY_6);
        bindNames.put("7", GLFW.GLFW_KEY_7);
        bindNames.put("8", GLFW.GLFW_KEY_8);
        bindNames.put("9", GLFW.GLFW_KEY_9);
        bindNames.put("0", GLFW.GLFW_KEY_0);
        bindNames.put("f1", GLFW.GLFW_KEY_F1);
        bindNames.put("f2", GLFW.GLFW_KEY_F2);
        bindNames.put("f3", GLFW.GLFW_KEY_F3);
        bindNames.put("f4", GLFW.GLFW_KEY_F4);
        bindNames.put("f5", GLFW.GLFW_KEY_F5);
        bindNames.put("f6", GLFW.GLFW_KEY_F6);
        bindNames.put("f7", GLFW.GLFW_KEY_F7);
        bindNames.put("f8", GLFW.GLFW_KEY_F8);
        bindNames.put("f9", GLFW.GLFW_KEY_F9);
        bindNames.put("f10", GLFW.GLFW_KEY_F10);
        bindNames.put("f11", GLFW.GLFW_KEY_F11);
        bindNames.put("f12", GLFW.GLFW_KEY_F12);
        bindNames.put("mouse0", 1000);
        bindNames.put("mouse1", 1001);
        bindNames.put("mouse2", 1002);
        bindNames.put("mouse3", 1003);
        bindNames.put("mouse4", 1004);
        bindNames.put("mouse5", 1005);
        bindNames.put("mouse_wheel_up", 1006);
        bindNames.put("mouse_wheel_down", 1007);

        typeLowercase.put(GLFW.GLFW_KEY_A, "a");
        typeLowercase.put(GLFW.GLFW_KEY_B, "b");
        typeLowercase.put(GLFW.GLFW_KEY_C, "c");
        typeLowercase.put(GLFW.GLFW_KEY_D, "d");
        typeLowercase.put(GLFW.GLFW_KEY_E, "e");
        typeLowercase.put(GLFW.GLFW_KEY_F, "f");
        typeLowercase.put(GLFW.GLFW_KEY_G, "g");
        typeLowercase.put(GLFW.GLFW_KEY_H, "h");
        typeLowercase.put(GLFW.GLFW_KEY_I, "i");
        typeLowercase.put(GLFW.GLFW_KEY_J, "j");
        typeLowercase.put(GLFW.GLFW_KEY_K, "k");
        typeLowercase.put(GLFW.GLFW_KEY_L, "l");
        typeLowercase.put(GLFW.GLFW_KEY_M, "m");
        typeLowercase.put(GLFW.GLFW_KEY_N, "n");
        typeLowercase.put(GLFW.GLFW_KEY_O, "o");
        typeLowercase.put(GLFW.GLFW_KEY_P, "p");
        typeLowercase.put(GLFW.GLFW_KEY_Q, "q");
        typeLowercase.put(GLFW.GLFW_KEY_R, "r");
        typeLowercase.put(GLFW.GLFW_KEY_S, "s");
        typeLowercase.put(GLFW.GLFW_KEY_T, "t");
        typeLowercase.put(GLFW.GLFW_KEY_U, "u");
        typeLowercase.put(GLFW.GLFW_KEY_V, "v");
        typeLowercase.put(GLFW.GLFW_KEY_W, "w");
        typeLowercase.put(GLFW.GLFW_KEY_X, "x");
        typeLowercase.put(GLFW.GLFW_KEY_Y, "y");
        typeLowercase.put(GLFW.GLFW_KEY_Z, "z");
        typeLowercase.put(GLFW.GLFW_KEY_SPACE, " ");
        typeLowercase.put(GLFW.GLFW_KEY_MINUS, "-");
        typeLowercase.put(GLFW.GLFW_KEY_EQUAL, "=");
        typeLowercase.put(GLFW.GLFW_KEY_LEFT_BRACKET, "[");
        typeLowercase.put(GLFW.GLFW_KEY_RIGHT_BRACKET, "]");
        typeLowercase.put(GLFW.GLFW_KEY_COMMA, ",");
        typeLowercase.put(GLFW.GLFW_KEY_PERIOD, ".");
        typeLowercase.put(GLFW.GLFW_KEY_SEMICOLON, ";");
        typeLowercase.put(GLFW.GLFW_KEY_APOSTROPHE, "'");
        typeLowercase.put(GLFW.GLFW_KEY_SLASH, "/");
        typeLowercase.put(GLFW.GLFW_KEY_BACKSLASH, "\\");
        typeLowercase.put(GLFW.GLFW_KEY_GRAVE_ACCENT, "`");
        typeLowercase.put(GLFW.GLFW_KEY_1, "1");
        typeLowercase.put(GLFW.GLFW_KEY_2, "2");
        typeLowercase.put(GLFW.GLFW_KEY_3, "3");
        typeLowercase.put(GLFW.GLFW_KEY_4, "4");
        typeLowercase.put(GLFW.GLFW_KEY_5, "5");
        typeLowercase.put(GLFW.GLFW_KEY_6, "6");
        typeLowercase.put(GLFW.GLFW_KEY_7, "7");
        typeLowercase.put(GLFW.GLFW_KEY_8, "8");
        typeLowercase.put(GLFW.GLFW_KEY_9, "9");
        typeLowercase.put(GLFW.GLFW_KEY_0, "0");

        typeUppercase.put(GLFW.GLFW_KEY_A, "A");
        typeUppercase.put(GLFW.GLFW_KEY_B, "B");
        typeUppercase.put(GLFW.GLFW_KEY_C, "C");
        typeUppercase.put(GLFW.GLFW_KEY_D, "D");
        typeUppercase.put(GLFW.GLFW_KEY_E, "E");
        typeUppercase.put(GLFW.GLFW_KEY_F, "F");
        typeUppercase.put(GLFW.GLFW_KEY_G, "G");
        typeUppercase.put(GLFW.GLFW_KEY_H, "H");
        typeUppercase.put(GLFW.GLFW_KEY_I, "I");
        typeUppercase.put(GLFW.GLFW_KEY_J, "J");
        typeUppercase.put(GLFW.GLFW_KEY_K, "K");
        typeUppercase.put(GLFW.GLFW_KEY_L, "L");
        typeUppercase.put(GLFW.GLFW_KEY_M, "M");
        typeUppercase.put(GLFW.GLFW_KEY_N, "N");
        typeUppercase.put(GLFW.GLFW_KEY_O, "O");
        typeUppercase.put(GLFW.GLFW_KEY_P, "P");
        typeUppercase.put(GLFW.GLFW_KEY_Q, "Q");
        typeUppercase.put(GLFW.GLFW_KEY_R, "R");
        typeUppercase.put(GLFW.GLFW_KEY_S, "S");
        typeUppercase.put(GLFW.GLFW_KEY_T, "T");
        typeUppercase.put(GLFW.GLFW_KEY_U, "U");
        typeUppercase.put(GLFW.GLFW_KEY_V, "V");
        typeUppercase.put(GLFW.GLFW_KEY_W, "W");
        typeUppercase.put(GLFW.GLFW_KEY_X, "X");
        typeUppercase.put(GLFW.GLFW_KEY_Y, "Y");
        typeUppercase.put(GLFW.GLFW_KEY_Z, "Z");
        typeUppercase.put(GLFW.GLFW_KEY_SPACE, " ");
        typeUppercase.put(GLFW.GLFW_KEY_MINUS, "_");
        typeUppercase.put(GLFW.GLFW_KEY_EQUAL, "+");
        typeUppercase.put(GLFW.GLFW_KEY_LEFT_BRACKET, "{");
        typeUppercase.put(GLFW.GLFW_KEY_RIGHT_BRACKET, "}");
        typeUppercase.put(GLFW.GLFW_KEY_COMMA, "<");
        typeUppercase.put(GLFW.GLFW_KEY_PERIOD, ">");
        typeUppercase.put(GLFW.GLFW_KEY_SEMICOLON, ":");
        typeUppercase.put(GLFW.GLFW_KEY_APOSTROPHE, "\"");
        typeUppercase.put(GLFW.GLFW_KEY_SLASH, "?");
        typeUppercase.put(GLFW.GLFW_KEY_BACKSLASH, "|");
        typeUppercase.put(GLFW.GLFW_KEY_GRAVE_ACCENT, "~");
        typeUppercase.put(GLFW.GLFW_KEY_1, "!");
        typeUppercase.put(GLFW.GLFW_KEY_2, "@");
        typeUppercase.put(GLFW.GLFW_KEY_3, "#");
        typeUppercase.put(GLFW.GLFW_KEY_4, "$");
        typeUppercase.put(GLFW.GLFW_KEY_5, "%");
        typeUppercase.put(GLFW.GLFW_KEY_6, "^");
        typeUppercase.put(GLFW.GLFW_KEY_7, "&");
        typeUppercase.put(GLFW.GLFW_KEY_8, "*");
        typeUppercase.put(GLFW.GLFW_KEY_9, "(");
        typeUppercase.put(GLFW.GLFW_KEY_0, ")");

    }

}
