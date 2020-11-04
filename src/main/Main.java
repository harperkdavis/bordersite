package main;

import engine.graphics.*;
import engine.graphics.Renderer;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector3f;
import engine.objects.Camera;
import engine.objects.GameObject;
import net.Client;
import net.packets.PacketDisconnect;
import net.packets.PacketLogin;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;

public class Main implements Runnable {

    public Thread game;
    public Window window;
    public Renderer renderer;
    public Shader shader;

    public final int WIDTH = 1280, HEIGHT = 720;
    public final String TITLE = "Bordersite";

    public World world;

    public Camera camera = new Camera(new Vector3f(0, 0, 0), Vector3f.zero());

    private Client socketClient;
    private Bordersite bordersite;

    private String username;

    public void start() {
        username = "";
        String message = "Enter username.";
        while (!(username.length() >= 3 && username.length() <= 16)) {
            username = JOptionPane.showInputDialog(new JFrame(), message);
            message = "Invalid username. Try again.";
        }

        bordersite = new Bordersite(this);

        world = new World(this);
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
        world.load();
        System.out.println("[INFO] World created!");

        System.out.println("[INFO] Loading shader...");
        shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
        shader.create();
        System.out.println("[INFO] Loading shader complete.");

        System.out.println("[INFO] Initializing renderer...");
        renderer = new Renderer(window, shader);
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
            update();
            render();
            if (Input.isKey(GLFW.GLFW_KEY_ESCAPE)) { break; }
            if (Input.isMouseButton(GLFW.GLFW_MOUSE_BUTTON_LEFT)) { window.mouseState(true); }
        }
        close();
    }

    private void update() {
        camera.update();
        window.update();
        world.render();
        bordersite.update();
    }

    private void render() {
        world.render();
        window.swapBuffers();
    }

    private void close() {
        System.out.println("[INFO] Closing game...");
        disconnect();
        System.out.println("[INFO] Disconnected from server");
        window.destroy();
        world.unload();
        shader.destroy();
        System.out.println("[INFO] Game Closed");
    }

    public static void main(String[] args) {
        new Main().start();
    }

}
