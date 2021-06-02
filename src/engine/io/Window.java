package engine.io;

import engine.math.Matrix4;
import engine.math.Vector3;
import main.Main;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

public class Window {

    private static Window gameWindow;

    private static int width, height;
    private boolean fullscreen;
    private final String title;
    private long window;

    private long fpsTime;

    private float frameRate = 60;
    private final float averageFPS = 60;
    private final float deltaTime = 0;

    private boolean mouseLocked = false;

    public Input input;

    private Vector3 background;

    private GLFWWindowSizeCallback sizeCallback;
    private boolean isResized;

    private static Matrix4 projection;
    private static Matrix4 ortho;

    private float fov = 80.0f;

    private final int[] windowPosX = new int[1];
    private final int[] windowPosY = new int[1];

    public Window(int width, int height, boolean fullscreen, String title) {
        Window.width = width;
        Window.height = height;
        this.title = title;
        this.fullscreen = fullscreen;

        projection = Matrix4.projection(fov, (float) width / (float) height, 0.01f, 10000.0f);
        ortho = Matrix4.ortho(-2, 2, -((float) height / 2) / ((float) width / 2), ((float) height / 2) / ((float) width / 2), 0.0001f, 1000.0f);
    }

    public void create() {

        System.out.println("    [INFO] Initializing GLFW...");
        if (!GLFW.glfwInit()) {
            System.err.println("[ERROR] GLFW wasn't initialized.");
            return;
        }
        System.out.println("    [INFO] GLFW Initialized!");

        System.out.println("    [INFO] Creating input class...");
        input = new Input();
        System.out.println("    [INFO] Input class created!");

        System.out.println("    [INFO] Creating window...");
        window = GLFW.glfwCreateWindow(width, height, title, 0,0);

        if (window == 0) {
            System.err.println("[ERROR] Window wasn't created.");
            return;
        }
        System.out.println("    [INFO] Window created!");

        System.out.println("    [INFO] Setting video mode and hints...");
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

        GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, 10);
        GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        windowPosX[0] = (videoMode.width() - width) / 2;
        windowPosY[0] = (videoMode.height() - height) / 2;
        GLFW.glfwSetWindowPos(window, windowPosX[0], windowPosY[0]);
        System.out.println("    [INFO] Video mode and hints set!");

        System.out.println("    [INFO] Creating capabilities...");
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        System.out.println("    [INFO] Capabilities created!");

        System.out.println("    [INFO] Creating callbacks");
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL13.GL_MULTISAMPLE);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_REPEAT);

        createCallbacks();
        System.out.println("    [INFO] Callbacks created!");

        System.out.println("    [INFO] Finalizing window...");
        GLFW.glfwShowWindow(window);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        fpsTime = System.currentTimeMillis();
        System.out.println("    [INFO] Window process completed.");
    }

    private void createCallbacks() {
        sizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
                width = w;
                height = h;
                isResized = true;
            }
        };

        GLFW.glfwSetKeyCallback(window, input.getKeyCallback());
        GLFW.glfwSetCursorPosCallback(window, input.getMousePosCallback());
        GLFW.glfwSetMouseButtonCallback(window, input.getMouseButtonCallback());
        GLFW.glfwSetScrollCallback(window, input.getMouseScrollCallback());
        GLFW.glfwSetWindowSizeCallback(window, sizeCallback);
    }

    public void update() {

        projection = Matrix4.projection(fov, (float) width / (float) height, 0.01f, 10000.0f);
        ortho = Matrix4.ortho(-2, 2, -((float) height / 2) / ((float) width / 2), ((float) height / 2) / ((float) width / 2), 0.0001f, 1000.0f);

        if (isResized) {
            GL11.glViewport(0, 0, width, height);
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

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        isResized = true;
        if (fullscreen) {
            GLFW.glfwGetWindowPos(window, windowPosX, windowPosY);
            GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor(), 0, 0, width, height, -1);
        } else {
            GLFW.glfwSetWindowMonitor(window, 0, windowPosX[0], windowPosY[0], width, height, -1);
        }
    }

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(window);
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public void destroy() {
        input.destroy();
        sizeCallback.free();
        GLFW.glfwWindowShouldClose(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public void setBackgroundColor(Vector3 color) {
        background = color;
    }

    public void mouseState(boolean lock) {
        mouseLocked = lock;
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, lock ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
    }

    public boolean isMouseLocked() {
        return mouseLocked;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public long getWindow() {
        return window;
    }

    public static Matrix4 getProjectionMatrix() {
        return projection;
    }

    public static Matrix4 getOrthographicMatrix() {
        return ortho;
    }

    public float getPixelHeight() {
        return 4f / height;
    }

    public float getPixelHeight(float amount) {
        return (4f / height) * amount;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public static Window getGameWindow() {
        return gameWindow;
    }

    public static void setGameWindow(Window gameWindow) {
        Window.gameWindow = gameWindow;
    }

    public static float getDeltaTime() {
        return Window.getGameWindow().deltaTime;
    }

    public float getFPS() {
        return frameRate;
    }
}
