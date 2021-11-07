package game.ui;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.Material;
import engine.graphics.MaterialLoader;
import engine.graphics.mesh.UiBuilder;
import engine.graphics.render.UiRenderer;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.GameObject;
import game.scene.Scene;
import game.ui.text.UiText;
import game.ui.text.UiTextField;
import main.Main;
import net.ClientHandler;
import org.lwjgl.glfw.GLFW;

public class UserInterface {

    public static float PIXEL;
    private static int width;
    private static int height;

    private static final List<GameObject> objects = new ArrayList<>();

    private static boolean inMapMenu = false;

    protected static MainMenu mainMenu;
    protected static MapLoadingMenu mapLoadingMenu;
    protected static InGameMenu inGameMenu;
    protected static MapMenu mapMenu;
    protected static TeamSelectMenu teamSelectMenu;

    public static boolean mouseLock = false, menuOpen = false;

    private static GameObject loadingBackground, loadingText, loadingMaterial;
    private static UiText loadingProgress;

    public static UiText debugFrameRate, debugFrameTime, debugPercentages, debugUpdate, debugMain, debugRender;

    public static void init(int width, int height) {
        UserInterface.width = width;
        UserInterface.height = height;

        PIXEL = 1.0f / (height / 2.25f);

        mainMenu = new MainMenu();
        mapLoadingMenu = new MapLoadingMenu();
        inGameMenu = new InGameMenu();
        mapMenu = new MapMenu();
        teamSelectMenu = new TeamSelectMenu();

        loadingBackground = addObject(new UiPanel(0, 0, 2, 2, 3, 1));
        loadingText = addObject(new UiText(screen(0 - p(8), 0 + p(32), 1), "LOADING..."));
        loadingProgress = (UiText) addObject(new UiText(screen(0 + p(2), 0 + p(46), 1), "0% / loaded"));
        loadingMaterial = addObject(new GameObject(screen(0 + p(4), 0 + p(60), 1), UiBuilder.UIRect(p(1),  Material.DEFAULT)));

        debugFrameRate = (UiText) addObject(new UiText(screen(0 + p(4), 1, 1), "Framerate: 0 fps"));
        debugFrameTime = (UiText) addObject(new UiText(screen(0 + p(4), 1 + p(16), 1), "Frame Time: 0 ms"));
        debugPercentages = (UiText) addObject(new UiText(screen(0 + p(4), 1 + p(32), 1), "Profile: "));
        debugUpdate = (UiText) addObject(new UiText(screen(0 + p(4), 1 + p(48), 1), "Update Time: 0 ms"));
        debugMain = (UiText) addObject(new UiText(screen(0 + p(4), 1 + p(64), 1), "Render Time: 0 ms"));
        debugRender = (UiText) addObject(new UiText(screen(0 + p(4), 1 + p(80), 1), "Total Render Time: 0 ms"));
    }

    public static void update() {

        if (Main.areMaterialsLoaded()) {

            loadingBackground.setVisible(false);
            loadingProgress.setVisible(false);
            loadingText.setVisible(false);
            loadingMaterial.setVisible(false);

            if (Input.isKeyDown(GLFW.GLFW_KEY_TAB)) {
                inMapMenu = !inMapMenu;
            }

            if (!ClientHandler.isConnected()) {
                mainMenu.setVisible(true);
                mainMenu.update();
                mouseLock = false;
                Input.inputsLocked = true;
                Window.mouseState(false);
            } else {
                mainMenu.setVisible(false);
            }
            if (ClientHandler.isConnected() && Scene.getGameScene() != null) {
                if (Scene.getGameScene().isLoading()) {
                    mapLoadingMenu.setVisible(true);
                    mapLoadingMenu.update();
                    Window.mouseState(false);
                } else {
                    mapLoadingMenu.setVisible(false);
                }

                if (Scene.getGameScene().isLoaded() && ClientHandler.hasRegisteredTeam()) {
                    mouseLock = true;
                    if (Input.isBindDown("esc")) {
                        if (UiTextField.activeTextField == null) {
                            menuOpen = !menuOpen;
                        }
                    }
                    Input.inputsLocked = false;
                    if (menuOpen) {
                        Input.inputsLocked = true;
                        mainMenu.setVisible(true);
                        mainMenu.update();
                        mouseLock = false;
                        Window.mouseState(false);
                    }

                    inGameMenu.setVisible(true);
                    inGameMenu.update();
                } else {
                    inGameMenu.updateChat();
                    inGameMenu.setVisible(false);
                }
                if (Scene.getGameScene().isLoaded() && !ClientHandler.hasRegisteredTeam()) {
                    teamSelectMenu.setVisible(true);
                    teamSelectMenu.update();
                    mouseLock = false;
                } else {
                    teamSelectMenu.setVisible(false);
                }
            } else {
                mapLoadingMenu.setVisible(false);
                inGameMenu.setVisible(false);
                teamSelectMenu.setVisible(false);
            }

            Window.mouseState(mouseLock);
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
            teamSelectMenu.setVisible(false);
        }

        debugFrameRate.setVisible(Input.isKey(GLFW.GLFW_KEY_F7));
        debugFrameTime.setVisible(Input.isKey(GLFW.GLFW_KEY_F7));
        debugPercentages.setVisible(Input.isKey(GLFW.GLFW_KEY_F7));
        debugUpdate.setVisible(Input.isKey(GLFW.GLFW_KEY_F7));
        debugMain.setVisible(Input.isKey(GLFW.GLFW_KEY_F7));
        debugRender.setVisible(Input.isKey(GLFW.GLFW_KEY_F7));
    }

    public static void fixedUpdate() {
    }

    public static void render() {

        for (GameObject o : objects) {
            UiRenderer.render(o);
        }

        mapLoadingMenu.render();
        inGameMenu.render();
        mapMenu.render();
        teamSelectMenu.render();
        mainMenu.render();
    }

    public static void unload() {
        for (GameObject o : objects) {
            o.unload();
        }

        mainMenu.unload();
        mapLoadingMenu.unload();
        inGameMenu.unload();
        mapMenu.unload();
        teamSelectMenu.unload();
    }

    public static GameObject addObject(GameObject object) {
        objects.add(object);
        return object;
    }

    public static void removeObject(GameObject object) {
        objects.remove(object);
        object.unload();
    }

    public static void resize(int width, int height) {
        UserInterface.width = width;
        UserInterface.height = height;
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

    // returns null if behind camera
    // public static Vector3f worldToScreen(Vector3f vector) {

    // }
}
