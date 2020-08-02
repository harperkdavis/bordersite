package main;

import engine.graphics.*;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector2;
import engine.math.Vector3;
import org.lwjgl.glfw.GLFW;

public class Bordersite implements Runnable {

    public Thread game;
    public Window window;
    public Renderer renderer;
    public Shader shader;

    public final int WIDTH = 800, HEIGHT = 600;
    public final String TITLE = "Bordersite";

    public Mesh mesh = new Mesh(new Vertex[] {
            new Vertex(new Vector3(-0.5f, 0.5f, 0.0f), new Vector3(1.0f, 0.0f, 0.0f), new Vector2(0.0f, 0.0f)),
            new Vertex(new Vector3(0.5f, 0.5f, 0.0f), new Vector3(0.0f, 1.0f, 0.0f), new Vector2(1.0f, 0.0f)),
            new Vertex(new Vector3(0.5f, -0.5f, 0.0f), new Vector3(0.0f, 0.0f, 1.0f), new Vector2(1.0f, 1.0f)),
            new Vertex(new Vector3(-0.5f, -0.5f, 0.0f), new Vector3(0.0f, 0.0f, 0.0f), new Vector2(0.0f, 1.0f))
    }, new int[] {
            0, 1, 2,
            0, 3, 2
    }, new Material("/textures/test.png"));

    public void start() {
        game = new Thread(this,"game");
        game.start();
    }

    public void init() {
        System.out.println("[INFO] Initializing game!");
        window = new Window(WIDTH, HEIGHT, TITLE);
        window.create();

        mesh.create();

        shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
        shader.create();

        renderer = new Renderer(shader);
        window.setBackgroundColor(new Vector3(0.2f, 0.6f, 1f));


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
        if (Input.isMouseButton(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            System.out.println("mouse down at x: " + Input.getMouseX() + " y:" + Input.getMouseY());
        }
    }

    private void render() {
        renderer.renderMesh(mesh);
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
