package main;

import engine.io.Input;
import engine.io.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.CallbackI;

public class Bordersite implements Runnable {

    public Thread game;
    public Window window;
    public final int WIDTH = 800, HEIGHT = 600;
    public final String TITLE = "Bordersite";

    public void start() {
        game = new Thread(this,"game");
        game.start();
    }

    public void init() {
        System.out.println("[INFO] Initializing game!");
        window = new Window(WIDTH, HEIGHT, TITLE);
        window.setBackgroundColor(0.2f, 0.6f, 1f);
        window.create();
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
        window.swapBuffers();
    }

    public static void main(String[] args) {
        new Bordersite().start();
    }

}
