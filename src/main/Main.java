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
    public Window window;
    public Renderer renderer;
    public Renderer uirenderer;
    public Shader shader;
    public Shader uishader;

    public final int WIDTH = 1600, HEIGHT = 900;
    public final float PIXEL = 4.0f / 900.0f;
    public final String TITLE = "Bordersite";

    public World world;
    private UserInterface ui;

    public Camera camera = new Camera(new Vector3f(0, 0, 0), Vector3f.zero());

    private Client socketClient;
    public PlayerMovement playerMovement;

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

        playerMovement = new PlayerMovement(this);

        world = new World(this);
        ui = new UserInterface(this);
        game = new Thread(this,"game");
        game.start();
    }

    public void init() {
        System.out.println("[INFO] Initializing game...");
        System.out.println("[INFO] Creating GLFW window..");

        window = new Window(WIDTH, HEIGHT, TITLE);
        window.create();

        System.out.println("[INFO] GLFW window created!");

        System.out.println("[INFO] Loading world...");
        ui.load();
        world.load();
        System.out.println("[INFO] World created!");

        System.out.println("[INFO] Loading shader...");
        shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
        shader.create();
        uishader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
        uishader.create();
        System.out.println("[INFO] Loading shader complete.");

        System.out.println("[INFO] Initializing renderer...");
        renderer = new Renderer(window, shader, false);
        uirenderer = new Renderer(window, uishader, true);
        System.out.println("[INFO] Renderer initialized!");
        window.setBackgroundColor(new Vector3f(0.8f, 0.8f, 0.8f));

        System.out.println("[INFO] Initialization completed!");

        System.out.println("[INFO] Connecting to server...");
        connect();
    }

    private void connect() {
        socketClient = new Client(this, "localhost");
        socketClient.start();

        PacketLogin packet = new PacketLogin(username);
        packet.writeData(socketClient);
    }

    private void disconnect() {
        System.out.println("[INFO] Sending disconnect packet");
        PacketDisconnect packet = new PacketDisconnect();
        packet.writeData(socketClient);
        System.out.println("[INFO] Ending client thread");
        socketClient.setRunning(false);
        socketClient.interrupt();
    }

    public void run() {
        init();
        while (!window.shouldClose()) {
            elapsedTime = (int) (System.currentTimeMillis() - startTime);
            update();
            render();
            if (Input.isKey(GLFW.GLFW_KEY_ESCAPE)) { break; }
            if (Input.isMouseButton(GLFW.GLFW_MOUSE_BUTTON_LEFT)) { window.mouseState(socketClient.isConnected()); }
            if (Input.isKey(GLFW.GLFW_KEY_E)) { window.mouseState(false); }
        }
        close();
    }

    private void update() {
        camera.update();
        window.update();
        ui.update();
        if (socketClient.isConnected()) {
            playerMovement.update();
            world.update();
        }
    }

    private void render() {
        if (socketClient.isConnected()) {
            world.render();
        }
        ui.render(uirenderer);
        window.swapBuffers();
    }

    private void close() {
        System.out.println("[INFO] Closing game...");
        disconnect();
        System.out.println("[INFO] Disconnected from server");
        window.destroy();
        world.unload();
        ui.unload();
        shader.destroy();
        System.out.println("[INFO] Game Closed");
    }

    public static void main(String[] args) {
        new Main().start();
    }

    public Client getSocketClient() {
        return socketClient;
    }
}
