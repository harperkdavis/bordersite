package engine.io;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public class Window {

    private int width, height;
    private String title;
    private long window;

    private int fpsFrames;
    private long fpsTime;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void create() {
        if (!GLFW.glfwInit()) {
            System.err.println("[ERROR] GLFW wasn't initialized.");
            return;
        }

        window = GLFW.glfwCreateWindow(width, height,title, 0,0);

        if (window == 0) {
            System.err.println("[ERROR] Window wasn't created.");
            return;
        }

        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(window, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);

        GLFW.glfwShowWindow(window);

        fpsTime = System.currentTimeMillis();
    }

    public void update() {
        GLFW.glfwPollEvents();

        fpsFrames ++;
        if (System.currentTimeMillis() > fpsTime + 1000) {
            GLFW.glfwSetWindowTitle(window, title + " (FPS: " + fpsFrames + ")");
            fpsFrames = 0;
            fpsTime = System.currentTimeMillis();
        }
    }

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(window);
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }


}
