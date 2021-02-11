package engine.io;

import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Window {

    private static Window gameWindow;

    private int width, height;
    private String title;
    private long window;

    private int fpsFrames;
    private long fpsTime;

    private long pastFrame;
    public float deltaTime;

    public float frameRate = 60;

    public Input input;

    private Vector3f background;

    private GLFWWindowSizeCallback sizeCallback;
    private boolean isResized;

    private Matrix4f projection;
    private Matrix4f ortho;

    private float fov = 80.0f;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;

        projection = Matrix4f.projection(fov, (float) width / (float) height, 0.1f, 1000);
        ortho = Matrix4f.ortho(-2, 2, -((float) height / 2) / ((float) width / 2), ((float) height / 2) / ((float) width / 2), 0.0001f, 1000);

        pastFrame = System.currentTimeMillis();
    }

    public void create() {

        if (!GLFW.glfwInit()) {
            System.err.println("[ERROR] GLFW wasn't initialized.");
            return;
        }

        input = new Input();
        window = GLFW.glfwCreateWindow(width, height,title, 0,0);

        if (window == 0) {
            System.err.println("[ERROR] Window wasn't created.");
            return;
        }

        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(window, (videoMode.width() - width) / 2,(videoMode.height() - height) / 2);

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        createCallbacks();

        GLFW.glfwShowWindow(window);

        GLFW.glfwSwapInterval(1);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        fpsTime = System.currentTimeMillis();
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
        projection = Matrix4f.projection(fov, (float) width / (float) height, 0.1f, 1000);
        if (isResized) {
            GL11.glViewport(0, 0, width, height);
            isResized = false;
        }
        GL11.glClearColor(background.getX(), background.getY(), background.getZ(), 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GLFW.glfwPollEvents();

        deltaTime = System.currentTimeMillis() - pastFrame;
        deltaTime /= 1000f;
        pastFrame = System.currentTimeMillis();

        fpsFrames ++;
        if (System.currentTimeMillis() > fpsTime + 1000) {
            GLFW.glfwSetWindowTitle(window, title + " (FPS: " + fpsFrames + ")");
            frameRate = fpsFrames;
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

    public void destroy() {
        input.destroy();
        sizeCallback.free();
        GLFW.glfwWindowShouldClose(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public void setBackgroundColor(Vector3f color) {
        background = color;
    }

    public void mouseState(boolean lock) {
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, lock ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public long getWindow() {
        return window;
    }

    public Matrix4f getProjectionMatrix() {
        return projection;
    }

    public Matrix4f getOrthoMatrix() {
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
}
