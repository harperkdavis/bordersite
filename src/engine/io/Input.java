package engine.io;

import org.lwjgl.glfw.*;
import org.lwjgl.system.CallbackI;

public class Input {

    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static boolean[] prevKeys = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] prevButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static boolean[] keysDown = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] buttonsDown = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static boolean[] keysUp = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] buttonsUp = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

    private static double mouseX, mouseY;
    private static double scrollState, pScrollState, scrollY;

    private final GLFWKeyCallback keyboard;
    private final GLFWCursorPosCallback mousePos;
    private final GLFWMouseButtonCallback mouse;
    private final GLFWScrollCallback mouseScroll;

    public Input() {
        keyboard = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                keys[key] = (action != GLFW.GLFW_RELEASE);
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

    public static boolean isKey(int key) {
        return keys[key];
    }

    public static boolean isMouseButton(int button) {
        return buttons[button];
    }

    public static boolean isKeyDown(int key) {
        return keysDown[key];
    }

    public static boolean isMouseButtonDown(int button) {
        return buttonsDown[button];
    }

    public static boolean isKeyUp(int key) {
        return keysUp[key];
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

        prevKeys = keys.clone();
        scrollState = 0;

    }

    public void destroy() {
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

    public GLFWKeyCallback getKeyCallback() {
        return keyboard;
    }

    public GLFWCursorPosCallback getMousePosCallback() {
        return mousePos;
    }

    public GLFWMouseButtonCallback getMouseButtonCallback() {
        return mouse;
    }

    public GLFWScrollCallback getMouseScrollCallback() {
        return mouseScroll;
    }

}
