package engine.io;

import org.lwjgl.glfw.*;

public class Input {

    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static double mouseX, mouseY;
    private static double scrollX, scrollY;

    private GLFWKeyCallback keyboard;
    private GLFWCursorPosCallback mousePos;
    private GLFWMouseButtonCallback mouse;
    private GLFWScrollCallback mouseScroll;

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
                mouseX = xoffset;
                mouseY = yoffset;
            }
        };
    }

    public static boolean isKey(int key) {
        return keys[key];
    }

    public static boolean isMouseButton(int button) {
        return buttons[button];
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

    public static double getScrollX() {
        return scrollX;
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
