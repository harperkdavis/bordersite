package main;

import engine.audio.AudioBuffer;
import engine.audio.AudioListener;
import engine.audio.AudioMaster;
import engine.audio.AudioSource;
import engine.graphics.*;
import engine.graphics.Renderer;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector3f;
import engine.objects.Camera;
import game.PlayerMovement;
import game.ui.UserInterface;
import game.world.World;
import net.Client;
import net.packets.PacketDisconnect;
import net.packets.PacketLogin;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import java.awt.*;

public class Main implements Runnable {

    public Thread game;
    public Shader shader;
    public Shader uishader;

    public int WIDTH, HEIGHT;
    public boolean FULLSCREEN;
    public final float PIXEL = 4.0f / 900.0f;
    public final String TITLE = "Bordersite";

    private String username = "Player";

    private static long startTime;
    public int elapsedTime;
    private long lastDataSend;

    private boolean hasSentLoadedPacket = false;

    public void start() {
        startTime = System.currentTimeMillis();

        String[] resolutionOptions = new String[]{"1920x1080", "1024x576", "1280x720", "1336x768", "1600x900", "2560x1440"};

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3,2));

        panel.add(new JLabel("Resolution"));
        JComboBox<String> res = new JComboBox<>(resolutionOptions);
        res.setVisible(true);
        panel.add(res);

        panel.add(new JLabel("Fullscreen"));
        JCheckBox box = new JCheckBox();
        box.setVisible(true);
        panel.add(box);

        panel.add(new JLabel("Username"));
        JTextField usr = new JTextField();
        usr.setVisible(true);
        panel.add(usr);

        int result = JOptionPane.showConfirmDialog(null, panel, "Bordersite Launching", JOptionPane.OK_CANCEL_OPTION);

        if (result == 2 || result == -1) {
            return;
        }
        if (res.getSelectedItem() != null) {
            switch ((String) res.getSelectedItem()) {
                case "1024x576":
                    WIDTH = 1024;
                    HEIGHT = 576;
                    break;
                case "1280x720":
                    WIDTH = 1280;
                    HEIGHT = 720;
                    break;
                case "1336x768":
                    WIDTH = 1336;
                    HEIGHT = 768;
                    break;
                case "1600x900":
                    WIDTH = 1600;
                    HEIGHT = 900;
                    break;
                case "2560x1440":
                    WIDTH = 2560;
                    HEIGHT = 1440;
                    break;
                default:
                    WIDTH = 1920;
                    HEIGHT = 1080;
                    break;
            }
        } else {
            WIDTH = 1920;
            HEIGHT = 1080;
        }
        FULLSCREEN = box.isSelected();
        username = usr.getText();

        PlayerMovement.setPlayerMovement(new PlayerMovement());

        World.setWorld(new World());
        UserInterface.setUi(new UserInterface(WIDTH, HEIGHT));
        game = new Thread(this,"game");
        game.start();
    }

    public void init() {
        System.out.println("[INFO] Initializing game...");
        System.out.println("[INFO] Creating GLFW window..");

        Window.setGameWindow(new Window(WIDTH, HEIGHT, FULLSCREEN, TITLE));
        Window.getGameWindow().create();
        Window.getGameWindow().setFullscreen(FULLSCREEN);

        System.out.println("[INFO] GLFW window created!");

        System.out.println("[INFO] Loading world...");
        UserInterface.getUi().load();

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

        System.out.println("[INFO] Initializing audio...");
        AudioMaster.load();
        AudioMaster.setListener(new AudioListener(new Vector3f(0, 0, 0)));
        System.out.println("[INFO] Audio initialized!");

        System.out.println("[INFO] Initialization completed!");

        connect();
    }

    private void connect() {
        Client.setSocketClient(new Client( "localhost"));
        Client.getSocketClient().start();

        World.getWorld().load();

        PacketLogin packet = new PacketLogin(username);
        packet.writeData(Client.getSocketClient());
    }

    private void disconnect() {
        System.out.println("[INFO] Sending disconnect packet");
        if (Client.isConnected()) {
            PacketDisconnect packet = new PacketDisconnect();
            packet.writeData(Client.getSocketClient());
            System.out.println("[INFO] Ending client thread");
            Client.getSocketClient().setRunning(false);
            Client.getSocketClient().stop();
        }
    }

    public void run() {
        init();
        while (!Window.getGameWindow().shouldClose()) {
            elapsedTime = (int) (System.currentTimeMillis() - startTime);
            update();
            render();
            if (Input.isKey(GLFW.GLFW_KEY_ESCAPE) && Input.isKey(GLFW.GLFW_KEY_LEFT_SHIFT)) { break; }
            if (Input.isMouseButton(GLFW.GLFW_MOUSE_BUTTON_LEFT)) { Window.getGameWindow().mouseState(Client.getSocketClient().isConnected()); }
            if (Input.isKey(GLFW.GLFW_KEY_E)) { Window.getGameWindow().mouseState(false); }
        }
        close();
    }

    private void update() {
        Camera.getMainCamera().update();
        Window.getGameWindow().update();
        Input.update();
        UserInterface.getUi().update();

        if (Client.isConnected()) {
            PlayerMovement.getPlayerMovement().update();
            World.getWorld().update();
            if (System.currentTimeMillis() - lastDataSend >= 10 && World.isLoaded()) {
                Client.getSocketClient().getSender().sendData();
                lastDataSend = System.currentTimeMillis();
            }
        }

        if (Input.isKeyDown(GLFW.GLFW_KEY_L)) {
            AudioSource source = new AudioSource(false, false);
            AudioBuffer buffer = new AudioBuffer("resources/audio/test.ogg");
            source.setBuffer(buffer.getBufferId());
            AudioMaster.addSoundSource("test", source);
            AudioMaster.playSoundSource("test");
        }
    }

    private void render() {
        if (Client.isConnected()) {
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

        AudioMaster.unload();

        System.out.println("[INFO] Game Closed");
    }

    public static void main(String[] args) {
        new Main().start();
    }

    public static long getStartTime() {
        return startTime;
    }
}
