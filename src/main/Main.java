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
import game.scene.Scene;
import game.ui.UserInterface;
import net.ClientHandler;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main implements Runnable {

    public Thread game;

    public int WIDTH, HEIGHT;
    public boolean FULLSCREEN;
    public final String TITLE = "Bordersite";

    private static String username = "Player";

    private static long startTime;
    private static long startTimeMillis;
    private static int elapsedTime;
    private long lastFixedUpdate;

    private static String ip;

    private static float deltaTime = 1.0f;

    private static boolean materialsLoaded = false;

    public void start() {
        startTime = System.nanoTime();
        startTimeMillis = System.currentTimeMillis();

        String[] resolutionOptions = new String[]{"1920x1080", "1024x576", "1280x720", "1336x768", "1600x900", "2560x1440"};

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4,2));

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

        panel.add(new JLabel("Ip Address"));
        JTextField ipf = new JTextField();
        ipf.setVisible(true);
        panel.add(ipf);
        ipf.setText("localhost");

        String[] randoms = new String[]{"Atrovel", "Famongs", "Distropertity", "Ternal", "Ankelers", "Pulappli", "Oblivia", "Tribution", "Gerrassa", "Exibillia", "Flunge", "Sinullacce", "Friness", "Hevephiny", "Excred", "Exciling", "Dumpined", "Buwheal", "Cotableat", "Litated", "Ationsoles", "Citions", "Catchibed", "Rumadle", "Diligord", "Conveyhopk", "Dropperclear", "Eefullanizess", "Cobypatachered", "Breestrass", "Wethosted", "Avorthuades", "Burgination", "Adwadiansie", "Disaplurntor", "Canous", "Morpenes", "Opcong", "Mitimigrood", "Ceallip", "Galition", "Innous", "Seiliu", "Arinterpord", "Writme", "Hasteives", "Cellordion", "Obseum", "Alerrawia", "Endency", "AbsoluteFuckingClown"};
        Random random = new Random();
        usr.setText(randoms[random.nextInt(50)] + random.nextInt(1000));

        int result = JOptionPane.showConfirmDialog(null, panel, "\uD83C\uDFF3 Bordersite Launching... ️", JOptionPane.OK_CANCEL_OPTION);

        if (result == 2 || result == -1) {
            return;
        }
        if (res.getSelectedItem() != null) {
            switch ((String) res.getSelectedItem()) {
                case "1024x576" -> {
                    WIDTH = 1024;
                    HEIGHT = 576;
                }
                case "1280x720" -> {
                    WIDTH = 1280;
                    HEIGHT = 720;
                }
                case "1336x768" -> {
                    WIDTH = 1336;
                    HEIGHT = 768;
                }
                case "1600x900" -> {
                    WIDTH = 1600;
                    HEIGHT = 900;
                }
                case "2560x1440" -> {
                    WIDTH = 2560;
                    HEIGHT = 1440;
                }
                default -> {
                    WIDTH = 1920;
                    HEIGHT = 1080;
                }
            }
        } else {
            WIDTH = 1920;
            HEIGHT = 1080;
        }
        FULLSCREEN = box.isSelected();
        username = usr.getText();
        ip = ipf.getText();
        if (username.equals("")) {
            username = "ThisPersonDoesntCare";
        }
        if (username.length() > 32) {
            username = username.substring(0, 32);
        }

        game = new Thread(this,"game");
        game.start();
    }

    public void init() {

        Printer.println("Initializing game...");
        Printer.println("Creating GLFW window..");

        Window.create(WIDTH, HEIGHT, FULLSCREEN, TITLE);

        Printer.println("GLFW window created!");

        PlayerMovement.setCamera();

        Scene.setActiveScene(new Scene());
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

        ClientHandler.connect(ip);

        System.out.println("[INFO] Initialization completed!");

    }

    private void connect() {

        Scene.getActiveScene().load();

    }

    private void disconnect() {
        ClientHandler.stopInputSender();
    }

    public void run() {
        init();
        while (!Window.shouldClose()) {
            elapsedTime = (int) (System.currentTimeMillis() - startTimeMillis);
            long cycleBegin = System.nanoTime();
            update();
            render();
            if (Input.isKey(GLFW.GLFW_KEY_ESCAPE) && Input.isKey(GLFW.GLFW_KEY_LEFT_SHIFT)) { break; }
            deltaTime = ((System.nanoTime() - cycleBegin) / 1000000.0f);

            if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) {
                Window.flipFullscreen();
            }
        }
        close();
    }

    private void update() {
        Window.update();
        Input.update();
        UserInterface.update();
        ClientHandler.update();
        PlayerMovement.update();

        boolean fixedUpdate = System.currentTimeMillis() - lastFixedUpdate >= 10;

        if (!materialsLoaded) {
            MaterialLoader.loadNext();
            if (MaterialLoader.isFinished()) {
                materialsLoaded = true;
                connect();
            }
            return;
        }

        if (fixedUpdate) {
            UserInterface.fixedUpdate();
            lastFixedUpdate = System.currentTimeMillis();
        }

        Scene.getActiveScene().update();
        AudioMaster.update();

    }

    private void render() {

        if (materialsLoaded && Scene.isLoaded()) {
            MainRenderer.renderActiveScene();
        }
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        UserInterface.render();

        Window.swapBuffers();
    }

    private void close() {
        System.out.println("[INFO] Closing game...");
        disconnect();
        System.out.println("[INFO] Disconnected from server");
        Window.destroy();

        Scene.getActiveScene().unload();
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

    public static String getUsername() {
        return username;
    }
}
