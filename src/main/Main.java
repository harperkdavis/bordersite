package main;

import engine.audio.*;
import engine.graphics.*;
import engine.graphics.render.MainRenderer;
import engine.graphics.render.UiRenderer;
import engine.io.Input;
import engine.io.Printer;
import engine.io.Window;
import engine.math.Vector3f;
import engine.objects.GameObject;
import game.PlayerMovement;
import game.scene.MenuScene;
import game.scene.Scene;
import game.ui.UserInterface;
import game.ui.text.UiTextField;
import net.ClientHandler;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class Main implements Runnable {

    public Thread game;

    public int WIDTH, HEIGHT;
    public boolean FULLSCREEN;
    public final String TITLE = "Bordersite";

    private static long startTime;
    private static long startTimeMillis;
    private static int elapsedTime;
    private long lastFixedUpdate;

    public static boolean requestClose = false;

    private static float deltaTime = 1.0f;

    private static boolean materialsLoaded = false;

    public static final int MAJOR_VERSION = 0;
    public static final int MINOR_VERSION = 2;
    public static final int PATCH_VERSION = 0;

    public void start() {
        startTime = System.nanoTime();
        startTimeMillis = System.currentTimeMillis();

        ConfigLoader.loadConfig();

        WIDTH = Global.WINDOW_WIDTH;
        HEIGHT = Global.WINDOW_HEIGHT;
        FULLSCREEN = Global.FULLSCREEN;

        game = new Thread(this,"game");
        game.start();
    }

    public void init() {

        Printer.println("Initializing game...");
        Printer.println("Creating GLFW window..");

        Window.create(WIDTH, HEIGHT, FULLSCREEN, TITLE);

        Printer.println("GLFW window created!");

        PlayerMovement.setCamera();

        UserInterface.init(Window.getWidth(), Window.getHeight());
        Global.init();
        ClientHandler.init();

        Printer.println("Loading scene...");

        Printer.println("Loading shader...");

        System.out.println("[INFO] Loading shader complete.");

        System.out.println("[INFO] Initializing renderer...");
        MainRenderer.init();
        UiRenderer.init();
        System.out.println("[INFO] Renderer initialized!");
        Window.setBackgroundColor(new Vector3f(0f, 0f, 0f));

        System.out.println("[INFO] Initializing audio...");
        AudioMaster.load();
        AudioListener listener = new AudioListener(new Vector3f(0, 0, 0));
        listener.setPosition(new Vector3f(0, 0, 0));
        listener.setOrientation(new Vector3f(0, 0, -1), new Vector3f(0, 1, 0));
        listener.setVelocity(new Vector3f(0, 0, 0));
        PlayerMovement.init();
        AudioMaster.setListener(listener);
        System.out.println("[INFO] Audio initialized!");

        MaterialLoader.initLoading();

        System.out.println("[INFO] Initialization completed!");

    }

    private void disconnect() {
        ClientHandler.stopInputSender();
    }

    private long updateNano = 0;
    private long renderNano = 0;

    public void run() {
        init();
        while (!Window.shouldClose()) {
            elapsedTime = (int) (System.currentTimeMillis() - startTimeMillis);
            long cycleBegin = System.nanoTime();
            update();
            render();
            if (Input.isKey(GLFW.GLFW_KEY_ESCAPE) && Input.isKey(GLFW.GLFW_KEY_LEFT_SHIFT) || requestClose) { break; }
            deltaTime = ((System.nanoTime() - cycleBegin) / 1000000.0f);

            UserInterface.debugFrameRate.setText("Framerate: " + Window.getFPS() + " fps");
            UserInterface.debugFrameTime.setText("Frame Time: " + deltaTime + " ms");

            if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) {
                Window.flipFullscreen();
            }
        }
        close();
    }

    private void update() {
        updateNano = System.nanoTime();

        long windowInputNano = System.nanoTime();

        Window.update();
        Input.update();

        windowInputNano = System.nanoTime() - windowInputNano;
        long uiNano = System.nanoTime();

        UiTextField.update();
        UserInterface.update();

        uiNano = System.nanoTime() - uiNano;
        long clientNano = System.nanoTime();

        ClientHandler.update();

        clientNano = System.nanoTime() - clientNano;
        long playerNano = System.nanoTime();

        PlayerMovement.update();
        playerNano = System.nanoTime() - playerNano;
        long otherNano = System.nanoTime();

        boolean fixedUpdate = System.currentTimeMillis() - lastFixedUpdate >= 10;

        if (!materialsLoaded) {
            MaterialLoader.loadNext();
            if (MaterialLoader.isFinished()) {
                materialsLoaded = true;
                MenuScene.init();
            }
            return;
        }

        if (fixedUpdate) {
            UserInterface.fixedUpdate();
            lastFixedUpdate = System.currentTimeMillis();
        }
        if (Scene.getActiveScene() != null) {
            Scene.getActiveScene().updateScene();
        }
        AudioMaster.update();

        otherNano = System.nanoTime() - otherNano;

        updateNano = System.nanoTime() - updateNano;

        NumberFormat nf = new DecimalFormat("#0.0");

        String updateText = "Update Time: " + nf.format(updateNano / 1000000.0f) + " ms " + " || " +
                "Window / Input: " + nf.format(windowInputNano / 1000000.0f) + " ms (" + nf.format(((float) windowInputNano / updateNano) * 100) + "%) || " +
                "Ui: " + nf.format(uiNano  / 1000000.0f) + " ms (" + nf.format(((float) uiNano / updateNano) * 100) + "%) || " +
                "Client: " + nf.format(clientNano / 1000000.0f) + " ms (" + nf.format(((float) clientNano / updateNano) * 100) + "%) || " +
                "Player: " + nf.format(playerNano / 1000000.0f) + " ms (" + nf.format(((float) playerNano / updateNano) * 100) + "%) || " +
                "Other: " + nf.format(otherNano / 1000000.0f) + " ms (" + nf.format(((float) otherNano / updateNano) * 100) + "%) ";
        UserInterface.debugUpdate.setText(updateText);

    }

    private void render() {
        renderNano = System.nanoTime();
        long sceneNano = System.nanoTime();

        if (materialsLoaded && Scene.getActiveScene() != null && Scene.getActiveScene().isLoaded()) {
            MainRenderer.renderActiveScene();
        }

        sceneNano = System.nanoTime() - sceneNano;

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        long uiNano = System.nanoTime();

        UserInterface.render();

        uiNano = System.nanoTime() - uiNano;
        long extraNano = System.nanoTime();

        Window.swapBuffers();

        extraNano = System.nanoTime() - extraNano;
        renderNano = System.nanoTime() - renderNano;

        NumberFormat nf = new DecimalFormat("#0.0");

        String renderText = "Render Time: " + nf.format(renderNano / 1000000.0f) + " ms " + " || " +
                "Scene Render: " + nf.format(sceneNano / 1000000.0f) + " ms (" + nf.format(((float) sceneNano / renderNano) * 100) + "%) || " +
                "Ui Render: " + nf.format(uiNano / 1000000.0f) + " ms (" + nf.format(((float) uiNano / renderNano) * 100) + " %) || " +
                "V-Sync Wait: " + nf.format(extraNano / 1000000.0f) + " ms (" + nf.format(((float) extraNano / renderNano) * 100) + "%) ||";

        UserInterface.debugRender.setText(renderText);


    }

    private void close() {
        System.out.println("[INFO] Closing game...");
        disconnect();
        System.out.println("[INFO] Disconnected from server");
        Window.destroy();
        if (Scene.getActiveScene() != null) {
            Scene.getActiveScene().unload();
        }
        UserInterface.unload();

        MaterialLoader.unloadAll();
        MainRenderer.unload();

        AudioMaster.unload();

        GameObject.unloadAll();

        System.out.println("[INFO] Game Closed");
        GLFW.glfwTerminate();
    }

    public static boolean areMaterialsLoaded() {
        return materialsLoaded;
    }

    public static void main(String[] args) {
        new Main().start();
    }

    public static long getStartTime() {
        return startTime;
    }

    public static int getElapsedTime() {
        return elapsedTime;
    }

    public static float getDeltaTime() {
        return deltaTime / 1000.0f;
    }

    public static float getFrameTime() {
        return (1000.0f / 60.0f) / deltaTime;
    }

}
