package game.ui;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.Material;
import engine.graphics.MaterialLoader;
import engine.graphics.mesh.UiBuilder;
import engine.graphics.render.Renderer;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.GameObject;
import game.GamePlane;
import game.scene.Scene;
import main.Main;
import org.lwjgl.glfw.GLFW;

public class UserInterface implements GamePlane {

    private static UserInterface ui;

    private static float PIXEL = 0.0025f;
    private static int width;
    private static int height;

    private static final List<GameObject> objects = new ArrayList<>();

    private boolean inMapMenu = false;

    private final MainMenu mainMenu;
    private final MapLoadingMenu mapLoadingMenu;
    private final InGameMenu inGameMenu;
    private final MapMenu mapMenu;

    public static boolean mouseLock = true;

    private GameObject loadingBackground, loadingText, loadingMaterial;
    private UiText loadingProgress;

    public UserInterface(int width, int height) {
        UserInterface.width = width;
        UserInterface.height = height;

        PIXEL = 1.0f / (height / 2.25f);

        mainMenu = new MainMenu();
        mapLoadingMenu = new MapLoadingMenu();
        inGameMenu = new InGameMenu();
        mapMenu = new MapMenu();

        loadingBackground = addObject(new UiPanel(0, 0, 2, 2, 3, 1));
        loadingText = addObject(new UiText(screen(0 - p(8), 0 + p(32), 1), "LOADING..."));
        loadingProgress = (UiText) addObject(new UiText(screen(0 + p(2), 0 + p(46), 1), "0% / loaded"));
        loadingMaterial = addObject(new GameObject(screen(0 + p(4), 0 + p(60), 1), UiBuilder.UIRect(p(1),  Material.DEFAULT)));
    }

    @Override
    public void load() {



        for (GameObject o : objects) {
            o.load();
        }

        mainMenu.load();
        mapLoadingMenu.load();
        inGameMenu.load();
        mapMenu.load();
    }

    @Override
    public void update() {

        if (Main.areMaterialsLoaded()) {

            loadingBackground.setVisible(false);
            loadingProgress.setVisible(false);
            loadingText.setVisible(false);
            loadingMaterial.setVisible(false);

            if (Input.isKeyDown(GLFW.GLFW_KEY_TAB)) {
                inMapMenu = !inMapMenu;
            }

            boolean inMainMenu = false;
            if (inMainMenu) {
                mainMenu.setVisible(true);
                mainMenu.update();
                Window.getGameWindow().mouseState(false);
            } else {
                mainMenu.setVisible(false);
            }

            if (Scene.isLoading()) {
                mapLoadingMenu.setVisible(true);
                mapLoadingMenu.update();
                Window.getGameWindow().mouseState(false);
            } else {
                mapLoadingMenu.setVisible(false);
            }

            if (Scene.isLoaded()) {
                inGameMenu.setVisible(true);
                inGameMenu.update();
            } else {
                inGameMenu.setVisible(false);
            }

            Window.getGameWindow().mouseState(mouseLock);

            if (Input.isBindDown("esc")) {
                mouseLock = !mouseLock;
            }
        } else {

            if (MaterialLoader.getCurrentMaterial() != null) {
                Material current = MaterialLoader.getCurrentMaterial();

                int width = current.getDiffuseTexture().getTextureWidth(), height = current.getDiffuseTexture().getTextureHeight();
                String percent = (Math.round(MaterialLoader.getProgress() * 1000) / 10.0f) + "%";

                loadingProgress.setText(percent + " // [" + current.getDiffusePath() + "] (" + width + "x" + height + ")");
                loadingMaterial.setMaterial(current);
                loadingMaterial.setScale(new Vector3f(width, width, width));
            }

            loadingBackground.setVisible(true);
            loadingProgress.setVisible(true);
            loadingText.setVisible(true);
            loadingMaterial.setVisible(true);

            mainMenu.setVisible(false);
            inGameMenu.setVisible(false);
            mapMenu.setVisible(false);
            mapLoadingMenu.setVisible(false);
        }
    }

    @Override
    public void fixedUpdate() {
    }

    @Override
    public void render() {

        for (GameObject o : objects) {
            Renderer.getUi().render(o);
        }
        mainMenu.render();
        mapLoadingMenu.render();
        inGameMenu.render();
        mapMenu.render();
    }

    @Override
    public void unload() {
        for (GameObject o : objects) {
            o.unload();
        }

        mainMenu.unload();
        mapLoadingMenu.unload();
        inGameMenu.unload();
        mapMenu.unload();
    }

    public GameObject addObject(GameObject object) {
        return addObject(object, false);
    }

    public GameObject addObject(GameObject object, boolean load) {
        objects.add(object);
        if (load) {
            object.load();
        }
        return object;
    }

    public void removeObject(GameObject object) {
        objects.remove(object);
        object.unload();
    }

    public static UserInterface getUi() {
        return ui;
    }

    public static void setUi(UserInterface ui) {
        UserInterface.ui = ui;
    }

    public static Vector2f screen(float posX, float posY) {
        return new Vector2f((posX * PIXEL * (width / 2.0f)) - (width / 2.0f) * PIXEL, (-posY * PIXEL * (height / 2.0f)) + (height / 2.0f) * PIXEL);
    }

    public static Vector3f screen(float posX, float posY, int layer) {
        return new Vector3f((posX * PIXEL * (width / 2.0f)) - (width / 2.0f) * PIXEL, (-posY * PIXEL * (height / 2.0f)) + (height / 2.0f) * PIXEL, layer);
    }

    public static float p(float n) {
        return n * PIXEL;
    }

    public static float p(int n) {
        return n * PIXEL;
    }

    public static float getNormMouseX() {
        return 1.0f + ((float) Input.getMouseX() - Window.getWidth() / 2.0f) / (Window.getWidth() / 2.0f);
    }

    public static float getNormMouseY() {
        return 1.0f - (Window.getHeight() / 2.0f - (float) Input.getMouseY()) / (Window.getHeight() / 2.0f);
    }
}
