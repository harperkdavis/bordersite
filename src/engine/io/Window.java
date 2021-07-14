package engine.io;

import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.objects.Camera;
import game.ui.UserInterface;
import main.Main;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjglx.opengl.Display;

public class Window {

    private static int width, height;
    private static boolean fullscreen;
    private static String title;
    private static long window;

    private static long fpsTime;

    private static float frameRate = 60;

    private static boolean mouseLocked = false;

    private static Vector3f background;

    private static GLFWWindowSizeCallback sizeCallback;
    private static boolean isResized;

    private static Matrix4f projection;
    private static Matrix4f ortho;

    private static final int[] windowPosX = new int[1];
    private static final int[] windowPosY = new int[1];

    public static void create(int width, int height, boolean fullscreen, String title) {


        Window.width = width;
        Window.height = height;
        Window.title = title;
        Window.fullscreen = fullscreen;

        projection = Matrix4f.projection(Camera.getFov(), (float) width / (float) height, 0.01f, 10000.0f);
        ortho = Matrix4f.ortho(-2, 2, -((float) height / 2) / ((float) width / 2), ((float) height / 2) / ((float) width / 2), 0.0001f, 1000.0f);

        if (!GLFW.glfwInit()) {
            Printer.println(0, "GLFW wasn't initialized.");
            return;
        }

        Input.createCallbacks();

        window = GLFW.glfwCreateWindow(width, height, title, 0,0);

        if (window == 0) {
            Printer.println(0, "Window wasn't created.");
            return;
        }

        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

        GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, 0);
        GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        windowPosX[0] = (videoMode.width() - width) / 2;
        windowPosY[0] = (videoMode.height() - height) / 2;
        GLFW.glfwSetWindowPos(window, windowPosX[0], windowPosY[0]);
        GLFW.glfwMakeContextCurrent(window);

        GL.createCapabilities();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL13.GL_MULTISAMPLE);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_REPEAT);

        createCallbacks();

        GLFW.glfwShowWindow(window);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glAlphaFunc(GL11.GL_GREATER, 2 / 255.0f);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        fpsTime = System.currentTimeMillis();

        try {
            IconLoader.setIcon("resources/icon-64.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setFullscreen(fullscreen);

    }

    private static void createCallbacks() {
        sizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
                width = w;
                height = h;
                isResized = true;
            }
        };

        GLFW.glfwSetKeyCallback(window, Input.getKeyCallback());
        GLFW.glfwSetCursorPosCallback(window, Input.getMousePosCallback());
        GLFW.glfwSetMouseButtonCallback(window, Input.getMouseButtonCallback());
        GLFW.glfwSetScrollCallback(window, Input.getMouseScrollCallback());
        GLFW.glfwSetWindowSizeCallback(window, sizeCallback);
    }

    public static void update() {

        projection = Matrix4f.projection(Camera.getFov(), (float) width / (float) height, 0.01f, 10000.0f);
        ortho = Matrix4f.ortho(-2, 2, -((float) height / 2) / ((float) width / 2), ((float) height / 2) / ((float) width / 2), 0.0001f, 1000.0f);

        if (isResized) {
            GL11.glViewport(0, 0, width, height);
            UserInterface.resize(width, height);
            isResized = false;
        }
        GL11.glClearColor(background.getX(), background.getY(), background.getZ(), 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GLFW.glfwPollEvents();

        if (System.currentTimeMillis() > fpsTime + 100) {

            frameRate = Math.round(1.0f / Main.getDeltaTime());
            GLFW.glfwSetWindowTitle(window, title + " (FPS: " + frameRate + ")");

            fpsTime = System.currentTimeMillis();

        }

    }

    public static void flipFullscreen() {
        setFullscreen(!fullscreen);
    }

    public static void setFullscreen(boolean fullscreen) {
        Window.fullscreen = fullscreen;
        isResized = true;
        if (fullscreen) {
            GLFW.glfwGetWindowPos(window, windowPosX, windowPosY);
            GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor(), 0, 0, width, height, -1);
        } else {
            GLFW.glfwSetWindowMonitor(window, 0, windowPosX[0], windowPosY[0], width, height, -1);
        }
    }

    public static void swapBuffers() {
        GLFW.glfwSwapBuffers(window);
    }

    public static boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public static void destroy() {
        Input.destroy();
        sizeCallback.free();
        GLFW.glfwWindowShouldClose(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public static void setBackgroundColor(Vector3f color) {
        background = color;
    }

    public static void mouseState(boolean lock) {
        mouseLocked = lock;
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, lock ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
    }

    public static boolean isMouseLocked() {
        return mouseLocked;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static String getTitle() {
        return title;
    }

    public static long getWindow() {
        return window;
    }

    public static Matrix4f getProjectionMatrix() {
        return projection;
    }

    public static Matrix4f getOrthographicMatrix() {
        return ortho;
    }

    public static float getPixelHeight() {
        return 4f / height;
    }

    public static float getPixelHeight(float amount) {
        return (4f / height) * amount;
    }

    public static float getFPS() {
        return frameRate;
    }


}
