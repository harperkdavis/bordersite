package engine.io;

import game.PlayerMovement;
import game.ui.text.UiTextField;
import main.Global;
import net.InputSender;
import org.lwjgl.glfw.*;

import java.util.ArrayList;

public class Input {

    private static final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static final boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static boolean[] prevKeys = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] prevButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static final boolean[] keysDown = new boolean[GLFW.GLFW_KEY_LAST];
    private static final boolean[] buttonsDown = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static final boolean[] keysUp = new boolean[GLFW.GLFW_KEY_LAST];
    private static final boolean[] buttonsUp = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

    private static ArrayList<String> prevKeybindList = new ArrayList<>();

    private static double mouseX, mouseY;
    private static double scrollState, pScrollState, scrollY;

    private static GLFWKeyCallback keyboard;
    private static GLFWCursorPosCallback mousePos;
    private static GLFWMouseButtonCallback mouse;
    private static GLFWScrollCallback mouseScroll;

    public static boolean inputsLocked = false;

    public static void createCallbacks() {
        keyboard = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key >= 0 && key < 348) {
                    keys[key] = (action != GLFW.GLFW_RELEASE);
                }
            }
        };

        mousePos = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mouseX = xpos;
                mouseY = ypos;
            }
        };

        mouse = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                buttons[button] = (action != GLFW.GLFW_RELEASE);
            }
        };

        mouseScroll = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                scrollState = yoffset;
            }
        };
    }

    public static ArrayList<String> getKeybindList() {
        if (inputsLocked) {
            return new ArrayList<>();
        }
        ArrayList<String> binds = new ArrayList<>();
        for (String bind : Global.keybinds.keySet()) {
            if (isKeybind(bind)) {
                binds.add(bind);
            }
        }
        return binds;
    }

    public static boolean isKey(int key) {
        if (key >= 0 && key < 348) {
            return keys[key];
        }
        return false;
    }

    public static boolean isMouseButton(int button) {
        return buttons[button];
    }

    public static boolean isKeyDown(int key) {
        if (key >= 0 && key < 348) {
            return keysDown[key];
        }
        return false;
    }

    public static boolean isMouseButtonDown(int button) {
        return buttonsDown[button];
    }

    public static boolean isKeyUp(int key) {
        if (key >= 0 && key < 348) {
            return keysUp[key];
        }
        return false;
    }

    public static boolean isMouseButtonUp(int button) {
        return buttonsUp[button];
    }

    public static void update() {
        for (int i = 0; i < keys.length; i++) {
            keysDown[i] = keys[i] && !prevKeys[i];
            keysUp[i] = !keys[i] && prevKeys[i];
        }
        for (int i = 0; i < buttons.length; i++) {
            buttonsDown[i] = buttons[i] && !prevButtons[i];
            buttonsUp[i] = !buttons[i] && prevButtons[i];
        }
        scrollY = scrollState;

        if (!getKeybindList().equals(prevKeybindList)) {
            InputSender.addInput(getKeybindList(), PlayerMovement.getCamera().getRotation());
        }

        prevKeys = keys.clone();
        prevButtons = buttons.clone();
        scrollState = 0;
        prevKeybindList = new ArrayList<>(getKeybindList());
    }

    public static boolean isBind(String bindName) {
        if (Global.bindNames.get(bindName) == null) {
            return false;
        }

        int bindId = Global.bindNames.get(bindName);

        if (bindId < 1000) {
            return isKey(bindId);
        } else if (bindId <= 1005) {
            return isMouseButton(bindId - 1000);
        } else if (bindId == 1006) {
            return scrollState == 1;
        } else if (bindId == 1007) {
            return scrollState == -1;
        }

        return false;
    }

    public static boolean isBindDown(String bindName) {
        if (Global.bindNames.get(bindName) == null) {
            return false;
        }

        int bindId = Global.bindNames.get(bindName);

        if (bindId < 1000) {
            return isKeyDown(bindId);
        } else if (bindId <= 1005) {
            return isMouseButtonDown(bindId - 1000);
        } else if (bindId == 1006) {
            return scrollState == 1;
        } else if (bindId == 1007) {
            return scrollState == -1;
        }

        return false;
    }

    public static boolean isBindUp(String bindName) {
        if (Global.bindNames.get(bindName) == null) {
            return false;
        }

        int bindId = Global.bindNames.get(bindName);

        if (bindId < 1000) {
            return isKeyUp(bindId);
        } else if (bindId <= 1005) {
            return isMouseButtonUp(bindId - 1000);
        } else if (bindId == 1006) {
            return scrollState == 1;
        } else if (bindId == 1007) {
            return scrollState == -1;
        }

        return false;
    }

    public static String getTypedCharacter() {
        for (int i : Global.typeLowercase.keySet()) {
            if (Input.isKeyDown(i)) {
                if (Input.isKey(GLFW.GLFW_KEY_LEFT_SHIFT) || Input.isKey(GLFW.GLFW_KEY_RIGHT_SHIFT)) {
                    return Global.typeUppercase.get(i);
                } else {
                    return Global.typeLowercase.get(i);
                }
            }
        }
        return "";
    }

    public static boolean isKeybind(String keybind) {
        if (isTyping()) {
            return false;
        }
        return isBind(Global.keybinds.getOrDefault(keybind, ""));
    }

    public static boolean isKeybindDown(String keybind) {
        if (isTyping()) {
            return false;
        }
        return isBindDown(Global.keybinds.getOrDefault(keybind, ""));
    }

    public static boolean isKeybindUp(String keybind) {
        if (isTyping()) {
            return false;
        }
        return isBindUp(Global.keybinds.getOrDefault(keybind, ""));
    }

    public static void destroy() {
        keyboard.free();
        mousePos.free();
        mouse.free();
        mouseScroll.free();
    }

    public static double getMouseX() {
        return mouseX;
    }

    public static double getMouseY() {
        return mouseY;
    }

    public static double getScrollY() {
        return scrollY;
    }

    public static GLFWKeyCallback getKeyCallback() {
        return keyboard;
    }

    public static GLFWCursorPosCallback getMousePosCallback() {
        return mousePos;
    }

    public static GLFWMouseButtonCallback getMouseButtonCallback() {
        return mouse;
    }

    public static GLFWScrollCallback getMouseScrollCallback() {
        return mouseScroll;
    }

    public static boolean isTyping() {
        return UiTextField.activeTextField != null;
    }

}
