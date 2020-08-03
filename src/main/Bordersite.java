package main;

import engine.graphics.*;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.Camera;
import engine.objects.GameObject;
import org.lwjgl.glfw.GLFW;

public class Bordersite implements Runnable {

    public Thread game;
    public Window window;
    public Renderer renderer;
    public Shader shader;

    public final int WIDTH = 800, HEIGHT = 600;
    public final String TITLE = "Bordersite";

    public Mesh mesh = new Mesh(new Vertex[] {
            new Vertex(new Vector3f(-0.5f, 0.5f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), new Vector2f(0.0f, 0.0f)),
            new Vertex(new Vector3f(0.5f, 0.5f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector2f(1.0f, 0.0f)),
            new Vertex(new Vector3f(0.5f, -0.5f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), new Vector2f(1.0f, 1.0f)),
            new Vertex(new Vector3f(-0.5f, -0.5f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector2f(0.0f, 1.0f))
    }, new int[] {
            0, 1, 2,
            0, 3, 2
    }, new Material("/textures/test.png"));

    public GameObject object = new GameObject(new Vector3f(0, 0, -1), Vector3f.zero(), Vector3f.one(), mesh);

    public Camera camera = new Camera(Vector3f.zero(), Vector3f.zero());

    public void start() {
        game = new Thread(this,"game");
        game.start();
    }

    public void init() {
        System.out.println("[INFO] Initializing game...");
        System.out.println("[INFO] Creating GLFW window..");

        window = new Window(WIDTH, HEIGHT, TITLE);
        window.create();

        System.out.println("[INFO] GLFW window created!");

        System.out.println("[INFO] Creating test mesh...");
        mesh.create();
        System.out.println("[INFO] Test mesh created!");

        System.out.println("[INFO] Loading shader...");
        shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
        shader.create();
        System.out.println("[INFO] Loading shader complete.");

        System.out.println("[INFO] Initializing renderer...");
        renderer = new Renderer(window, shader);
        System.out.println("[INFO] Renderer initialized!");
        window.setBackgroundColor(new Vector3f(0.2f, 0.6f, 1f));

        System.out.println("[INFO] Initialization completed!");
    }

    public void run() {
        init();
        while (!window.shouldClose()) {
            update();
            render();
            if (Input.isKey(GLFW.GLFW_KEY_ESCAPE)) { break; }
        }
        close();
    }

    private void update() {
        window.update();
        camera.setRotation(camera.getRotation().add(0, 1, 0));
        if (Input.isMouseButton(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            System.out.println("mouse down at x: " + Input.getMouseX() + " y:" + Input.getMouseY());
        }
    }

    private void render() {
        renderer.renderMesh(object, camera);
        window.swapBuffers();
    }

    private void close() {
        window.destroy();
        mesh.destroy();
        shader.destroy();
    }

    public static void main(String[] args) {
        new Bordersite().start();
    }

}
