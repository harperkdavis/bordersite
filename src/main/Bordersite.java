package main;

import engine.graphics.Mesh;
import engine.graphics.Renderer;
import engine.graphics.Vertex;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector3;
import org.lwjgl.glfw.GLFW;

public class Bordersite implements Runnable {

    public Thread game;
    public Window window;
    public Renderer renderer;
    public final int WIDTH = 800, HEIGHT = 600;
    public final String TITLE = "Bordersite";

    public Mesh mesh = new Mesh(new Vertex[] {
            new Vertex(new Vector3(-0.5f, 0.5f, 0.0f)),
            new Vertex(new Vector3(0.5f, 0.5f, 0.0f)),
            new Vertex(new Vector3(0.5f, -0.5f, 0.0f)),
            new Vertex(new Vector3(-0.5f, -0.5f, 0.0f)),
    }, new int[] {
            0, 1, 2,
            0, 3, 2
    });

    public void start() {
        game = new Thread(this,"game");
        game.start();
    }

    public void init() {
        System.out.println("[INFO] Initializing game!");
        window = new Window(WIDTH, HEIGHT, TITLE);
        renderer = new Renderer();
        window.setBackgroundColor(new Vector3(0.2f, 0.6f, 1f));
        window.create();
        mesh.create();
    }

    public void run() {
        init();
        while (!window.shouldClose()) {
            update();
            render();
            if (Input.isKey(GLFW.GLFW_KEY_ESCAPE)) { break; }
        }
        window.destroy();
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

    public static void main(String[] args) {
        new Bordersite().start();
    }

}
