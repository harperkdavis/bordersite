package main;

import engine.io.Window;

public class Bordersite implements Runnable {

    public Thread game;
    public static Window window;
    public static final int WIDTH = 800, HEIGHT = 600;
    public static final String TITLE = "Bordersite";

    public void start() {
        game = new Thread(this,"game");
        game.start();
    }

    public static void init() {
        System.out.println("[INFO] Initializing game!");
        window = new Window(WIDTH, HEIGHT, TITLE);
        window.create();
    }

    public void run() {
        init();
        while (true) {
            update();
            render();
        }
    }

    private void update() {
        window.update();
        window.swapBuffers();
    }

    private void render() {

    }

    public static void main(String[] args) {
        new Bordersite().start();
    }

}
