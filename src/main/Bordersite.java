package main;

import engine.graphics.*;
import engine.graphics.Renderer;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.Camera;
import engine.objects.GameObject;
import net.Client;
import net.packets.PacketDisconnect;
import net.packets.PacketLogin;
import org.lwjgl.glfw.GLFW;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;

import javax.swing.*;

public class Bordersite implements Runnable {

    public Thread game;
    public Window window;
    public Renderer renderer;
    public Shader shader;

    public final int WIDTH = 800, HEIGHT = 600;
    public final String TITLE = "Bordersite";

    public Mesh mesh = Mesh.Cube(1, new Material("/textures/test.png"));

    public GameObject object = new GameObject(new Vector3f(0, 0, -1), Vector3f.zero(), Vector3f.one(), mesh);

    public Camera camera = new Camera(Vector3f.zero(), Vector3f.zero());

    private Client socketClient;

    private String username;

    public void start() {
        username = "";
        String message = "Enter username.";
        while (!(username.length() >= 3 && username.length() <= 16)) {
            username = JOptionPane.showInputDialog(new JFrame(), message);
            message = "Invalid username. Try again.";
        }

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
        PacketDisconnect packet = new PacketDisconnect();
        packet.writeData(socketClient);
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
    }

    private void render() {
        renderer.renderMesh(object, camera);
        window.swapBuffers();
    }

    private void close() {
        System.out.println("[INFO] Closing game...");
        disconnect();
        window.destroy();
        mesh.destroy();
        shader.destroy();
    }

    public static void main(String[] args) {
        new Bordersite().start();
    }

}
