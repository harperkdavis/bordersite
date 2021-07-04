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
import game.PlayerMovement;
import game.scene.Scene;
import game.ui.UserInterface;
import game.viewmodel.Viewmodel;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main implements Runnable {

    public Thread game;
    public Shader gShader, ssaoShader, ssaoBlurShader, shadowShader, mainShader, blurShader, postShader, uiShader, viewmodelShader, unlitShader;

    public int WIDTH, HEIGHT;
    public boolean FULLSCREEN;
    public final String TITLE = "Main";

    private String username = "Player";

    private static long startTime;
    private static int elapsedTime;
    private long lastFixedUpdate;

    private static float deltaTime = 1.0f;

    private static boolean materialsLoaded = false;

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
        username = usr.getText().substring(0, Math.min(username.length(), 64));
        if (username.equals("")) {
            username = "ThisPersonDoesntCare";
        }

        PlayerMovement.setPlayerMovement(new PlayerMovement());

        Scene.setScene(new Scene());
        Viewmodel.setViewmodel(new Viewmodel());
        UserInterface.setUi(new UserInterface(WIDTH, HEIGHT));
        Global.init();
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

        System.out.println("[INFO] Loading scene...");
        UserInterface.getUi().load();

        System.out.println("[INFO] Loading shader...");
        gShader = loadShader("g");

        ssaoShader = loadShader("ssao");
        ssaoBlurShader = loadShader("ssaoBlur");

        shadowShader = loadShader("shadow");
        mainShader = loadShader("main");
        postShader = loadShader("post");

        blurShader = loadShader("blur");
        uiShader = loadShader("ui");
        viewmodelShader = loadShader("viewmodel");

        unlitShader = loadShader("unlit");

        System.out.println("[INFO] Loading shader complete.");

        System.out.println("[INFO] Initializing renderer...");
        Renderer.setMain(new MainRenderer(gShader, ssaoShader, ssaoBlurShader, mainShader, shadowShader, blurShader, postShader, unlitShader));
        Renderer.setUi(new UiRenderer(uiShader));
        Renderer.setViewmodel(new ViewmodelRenderer(viewmodelShader));
        System.out.println("[INFO] Renderer initialized!");
        Window.getGameWindow().setBackgroundColor(new Vector3f(0f, 0f, 0f));

        System.out.println("[INFO] Initializing audio...");
        AudioMaster.load();
        AudioListener listener = new AudioListener(new Vector3f(0, 0, 0));
        listener.setPosition(new Vector3f(0, 0, 0));
        listener.setOrientation(new Vector3f(0, 0, -1), new Vector3f(0, 1, 0));
        listener.setVelocity(new Vector3f(0, 0, 0));
        AudioMaster.setListener(listener);
        System.out.println("[INFO] Audio initialized!");

        MaterialLoader.initLoading();

        System.out.println("[INFO] Initialization completed!");

    }

    private Shader loadShader(String name) {
        Shader shader = new Shader("/shaders/" + name + "Vertex.glsl", "/shaders/" + name + "Fragment.glsl");
        shader.create();
        return shader;
    }

    private void connect() {

        Scene.getScene().load();
        Viewmodel.getViewmodel().load();

    }

    private void disconnect() {
    }

    public void run() {
        init();
        while (!Window.getGameWindow().shouldClose()) {
            elapsedTime = (int) (System.currentTimeMillis() - startTime);
            long cycleBegin = System.nanoTime();
            update();
            render();
            if (Input.isKey(GLFW.GLFW_KEY_ESCAPE) && Input.isKey(GLFW.GLFW_KEY_LEFT_SHIFT)) { break; }
            deltaTime = ((System.nanoTime() - cycleBegin) / 1000000.0f);

            if (Input.isKeyDown(GLFW.GLFW_KEY_G)) {
                int i = 0;
            }
        }
        close();
    }

    private void update() {
        Window.getGameWindow().update();
        Input.update();
        UserInterface.getUi().update();

        boolean fixedUpdate = System.currentTimeMillis() - lastFixedUpdate >= 10;

        if (!materialsLoaded) {
            MaterialLoader.loadNext();
            if (MaterialLoader.isFinished()) {
                materialsLoaded = true;
                Renderer.getMain().createBuffers();
                connect();
            }
            return;
        }

        if (fixedUpdate) {
            UserInterface.getUi().fixedUpdate();
            lastFixedUpdate = System.currentTimeMillis();
        }

        Scene.getScene().update();
        if (Scene.isLoaded()) {
            PlayerMovement.getPlayerMovement().update();
            Viewmodel.getViewmodel().update();

        }

            AudioMaster.update();
    }

    private void render() {

        if (materialsLoaded && Scene.isLoaded()) {
            Renderer.getMain().renderScene();

            Viewmodel.getViewmodel().render();
        }
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        UserInterface.getUi().render();

        Window.getGameWindow().swapBuffers();
    }

    private void close() {
        System.out.println("[INFO] Closing game...");
        disconnect();
        System.out.println("[INFO] Disconnected from server");
        Window.getGameWindow().destroy();

        Scene.getScene().unload();
        Viewmodel.getViewmodel().unload();
        UserInterface.getUi().unload();

        MaterialLoader.unloadAll();

        mainShader.destroy();
        uiShader.destroy();

        AudioMaster.unload();

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
