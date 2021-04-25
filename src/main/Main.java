package main;

import engine.audio.*;
import engine.graphics.*;
import engine.graphics.render.MainRenderer;
import engine.graphics.render.Renderer;
import engine.graphics.render.UiRenderer;
import engine.graphics.render.ViewmodelRenderer;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector3f;
import engine.objects.Camera;
import game.PlayerMovement;
import game.ui.UserInterface;
import game.viewmodel.Viewmodel;
import game.world.World;
import net.Client;
import net.packets.PacketDisconnect;
import net.packets.PacketLogin;
import org.lwjgl.glfw.GLFW;
import org.lwjglx.Sys;

import javax.swing.*;
import java.awt.*;

public class Main implements Runnable {

    public Thread game;
    public Shader mainShader, uiShader, viewmodelShader;

    public int WIDTH, HEIGHT;
    public boolean FULLSCREEN;
    public final float PIXEL = 4.0f / 900.0f;
    public final String TITLE = "Bordersite";

    private String username = "Player";

    private static long startTime;
    private static int elapsedTime;
    private long lastFixedUpdate;

    private static long cycleBegin;
    private static float deltaTime = 1.0f;

    private boolean hasSentLoadedPacket = false;

    public void start() {
        startTime = System.nanoTime();

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
        Viewmodel.setViewmodel(new Viewmodel());
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
        mainShader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
        mainShader.create();
        uiShader = new Shader("/shaders/uiVertex.glsl", "/shaders/uiFragment.glsl");
        uiShader.create();
        viewmodelShader = new Shader("/shaders/viewmodelVertex.glsl", "/shaders/viewmodelFragment.glsl");
        viewmodelShader.create();
        System.out.println("[INFO] Loading shader complete.");

        System.out.println("[INFO] Initializing renderer...");
        Renderer.setMain(new MainRenderer(mainShader));
        Renderer.setUi(new UiRenderer(uiShader));
        Renderer.setViewmodel(new ViewmodelRenderer(viewmodelShader));
        System.out.println("[INFO] Renderer initialized!");
        Window.getGameWindow().setBackgroundColor(new Vector3f(0.8f, 0.8f, 0.8f));

        System.out.println("[INFO] Initializing audio...");
        AudioMaster.load();
        AudioListener listener = new AudioListener(new Vector3f(0, 0, 0));
        listener.setPosition(new Vector3f(0, 0, 0));
        listener.setOrientation(new Vector3f(0, 0, -1), new Vector3f(0, 1, 0));
        listener.setVelocity(new Vector3f(0, 0, 0));
        AudioMaster.setListener(listener);
        System.out.println("[INFO] Audio initialized!");

        System.out.println("[INFO] Initialization completed!");

        connect();
    }

    private void connect() {
        Client.setSocketClient(new Client( "localhost"));
        Client.getSocketClient().start();

        World.getWorld().load();
        Viewmodel.getViewmodel().load();

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
            cycleBegin = System.nanoTime();
            update();
            render();
            if (Input.isKey(GLFW.GLFW_KEY_ESCAPE) && Input.isKey(GLFW.GLFW_KEY_LEFT_SHIFT)) { break; }
            deltaTime = ((System.nanoTime() - cycleBegin) / 1000000.0f);
        }
        close();
    }

    private void update() {
        Window.getGameWindow().update();
        Input.update();
        UserInterface.getUi().update();

        boolean fixedUpdate = System.currentTimeMillis() - lastFixedUpdate >= 10;

        if (fixedUpdate) {
            UserInterface.getUi().fixedUpdate();
            lastFixedUpdate = System.currentTimeMillis();
        }


        if (Client.isConnected()) {
            PlayerMovement.getPlayerMovement().update();
            Viewmodel.getViewmodel().update();
            World.getWorld().update();

            if (fixedUpdate && World.isLoaded()) {
                Client.getSocketClient().getSender().sendData();
                lastFixedUpdate = System.currentTimeMillis();
            }
        }

        if (Input.isKeyDown(GLFW.GLFW_KEY_L)) {
            AudioMaster.playSound(SoundEffect.AIRHORN, new Vector3f(50, 0, 50));
        }

        AudioMaster.update();
    }

    private void fixedUpdate() {

    }

    private void render() {

        if (Client.isConnected()) {
            Renderer.getMain().renderPrep();
            World.getWorld().render();
            Renderer.getMain().renderCleanup();

            Viewmodel.getViewmodel().render();
        }
        UserInterface.getUi().render();


        Window.getGameWindow().swapBuffers();
    }

    private void close() {
        System.out.println("[INFO] Closing game...");
        disconnect();
        System.out.println("[INFO] Disconnected from server");
        Window.getGameWindow().destroy();

        World.getWorld().unload();
        Viewmodel.getViewmodel().unload();
        UserInterface.getUi().unload();

        mainShader.destroy();
        uiShader.destroy();

        AudioMaster.unload();

        System.out.println("[INFO] Game Closed");
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
