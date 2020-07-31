package main;

import engine.io.Window;

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
        window.create();
    }

    public void run() {
        init();
        while (!window.shouldClose()) {
            update();
            render();
        }
    }

    private void update() {
        window.update();
    }

    private void render() {
        window.swapBuffers();
    }

    public static void main(String[] args) {
        new Bordersite().start();
    }

}
