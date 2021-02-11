package main;

import engine.graphics.*;
import engine.graphics.Renderer;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector3f;
import engine.objects.Camera;
import net.Client;
import net.packets.PacketDisconnect;
import net.packets.PacketLogin;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;

public class Main implements Runnable {

    public Thread game;
    public Shader shader;
    public Shader uishader;

    public final int WIDTH = 1600, HEIGHT = 900;
    public final float PIXEL = 4.0f / 900.0f;
    public final String TITLE = "Bordersite";

    private String username;

    private long startTime;
    public int elapsedTime;

    public void start() {
        startTime = System.currentTimeMillis();

        username = "";
        String message = "Enter username.";
        while (!(username.length() >= 3 && username.length() <= 16)) {
            username = JOptionPane.showInputDialog(new JFrame(), message);
            message = "Invalid username. Try again.";
        }

        PlayerMovement.setPlayerMovement(new PlayerMovement());

        World.setWorld(new World());
        UserInterface.setUi(new UserInterface());
        game = new Thread(this,"game");
        game.start();
    }

    public void init() {
        System.out.println("[INFO] Initializing game...");
        System.out.println("[INFO] Creating GLFW window..");

        Window.setGameWindow(new Window(WIDTH, HEIGHT, TITLE));
        Window.getGameWindow().create();

        System.out.println("[INFO] GLFW window created!");

        System.out.println("[INFO] Loading world...");
        UserInterface.getUi().load();
        World.getWorld().load();
        System.out.println("[INFO] World created!");

        System.out.println("[INFO] Loading shader...");
        shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
        shader.create();
        uishader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
        uishader.create();
        System.out.println("[INFO] Loading shader complete.");

        System.out.println("[INFO] Initializing renderer...");
        Renderer.setRenderer(new Renderer(Window.getGameWindow(), shader, false));
        Renderer.setUiRenderer(new Renderer(Window.getGameWindow(), uishader, true));
        System.out.println("[INFO] Renderer initialized!");
        Window.getGameWindow().setBackgroundColor(new Vector3f(0.8f, 0.8f, 0.8f));

        System.out.println("[INFO] Initialization completed!");

        System.out.println("[INFO] Connecting to server...");
        connect();
    }

    private void connect() {
        Client.setSocketClient(new Client( "localhost"));
        Client.getSocketClient().start();

        PacketLogin packet = new PacketLogin(username);
        packet.writeData(Client.getSocketClient());
    }

    private void disconnect() {
        System.out.println("[INFO] Sending disconnect packet");
        PacketDisconnect packet = new PacketDisconnect();
        packet.writeData(Client.getSocketClient());
        System.out.println("[INFO] Ending client thread");
        Client.getSocketClient().setRunning(false);
        Client.getSocketClient().stop();
    }

    public void run() {
        init();
        while (!Window.getGameWindow().shouldClose()) {
            elapsedTime = (int) (System.currentTimeMillis() - startTime);
            update();
            render();
            if (Input.isKey(GLFW.GLFW_KEY_ESCAPE)) { break; }
            if (Input.isMouseButton(GLFW.GLFW_MOUSE_BUTTON_LEFT)) { Window.getGameWindow().mouseState(Client.getSocketClient().isConnected()); }
            if (Input.isKey(GLFW.GLFW_KEY_E)) { Window.getGameWindow().mouseState(false); }
        }
        close();
    }

    private void update() {
        Camera.getMainCamera().update();
        Window.getGameWindow().update();
        UserInterface.getUi().update();

        if (Client.getSocketClient().isConnected()) {
            PlayerMovement.getPlayerMovement().update();
            World.getWorld().update();
        }
    }

    private void render() {
        if (Client.getSocketClient().isConnected()) {
            World.getWorld().render();
        }
        UserInterface.getUi().render(Renderer.getUiRenderer());
        Window.getGameWindow().swapBuffers();
    }

    private void close() {
        System.out.println("[INFO] Closing game...");
        disconnect();
        System.out.println("[INFO] Disconnected from server");
        Window.getGameWindow().destroy();

        World.getWorld().unload();
        UserInterface.getUi().unload();

        shader.destroy();
        uishader.destroy();
        System.out.println("[INFO] Game Closed");
    }

    public static void main(String[] args) {
        new Main().start();
    }


}
