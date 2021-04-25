package game.ui;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.render.Renderer;
import engine.graphics.text.TextMeshBuilder;
import engine.graphics.text.TextMode;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.GameObject;
import game.GamePlane;
import game.world.World;
import org.lwjgl.glfw.GLFW;

public class UserInterface implements GamePlane {

    private static UserInterface ui;

    private static float PIXEL = 0.0025f;
    private static int width;
    private static int height;

    private static List<GameObject> objects = new ArrayList<>();

    private boolean inMainMenu = false;
    private boolean inMapMenu = false;

    private MainMenu mainMenu;
    private LoadingMenu loadingMenu;
    private InGameMenu inGameMenu;
    private MapMenu mapMenu;
    private Menu buildingMenu;

    private UiObject fpsLabel;

    public static boolean mouseLock;

    public UserInterface(int width, int height) {
        this.width = width;
        this.height = height;

        PIXEL = 1.0f / (height / 2.25f);

        mainMenu = new MainMenu();
        loadingMenu = new LoadingMenu();
        inGameMenu = new InGameMenu();
        mapMenu = new MapMenu();

        fpsLabel = new UiObject(screen(0, 0.05f, 1), Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh("fps: ", p(16), TextMode.LEFT, false));
        addObject(fpsLabel, false);
    }

    @Override
    public void load() {
        for (GameObject o : objects) {
            o.load();
        }

        mainMenu.load();
        loadingMenu.load();
        inGameMenu.load();
        mapMenu.load();
    }

    @Override
    public void update() {

        if (Input.isKeyDown(GLFW.GLFW_KEY_TAB)) {
            inMapMenu = !inMapMenu;
        }

        if (inMainMenu) {
            mainMenu.setVisible(true);
            mainMenu.update();
            Window.getGameWindow().mouseState(false);
        } else {
            mainMenu.setVisible(false);
        }

        if (World.isLoading()) {
            loadingMenu.setVisible(true);
            loadingMenu.update();
            Window.getGameWindow().mouseState(false);
        } else {
            loadingMenu.setVisible(false);
        }

        if (!World.isLoading() && !inMainMenu && !inMapMenu) {
            inGameMenu.setVisible(true);
            inGameMenu.update();
            Window.getGameWindow().mouseState(!(inGameMenu.buyMenuOpen || inGameMenu.pauseMenuOpen));
        } else {
            inGameMenu.setVisible(false);
        }

        if (!inMainMenu && !World.isLoading() && inMapMenu) {
            mapMenu.setVisible(true);
            mapMenu.update();
            Window.getGameWindow().mouseState(false);
        } else {
            mapMenu.setVisible(false);
        }
    }

    @Override
    public void fixedUpdate() {
        fpsLabel.setMesh(TextMeshBuilder.TextMesh("fps: " + Window.getGameWindow().getFPS(), p(16), TextMode.LEFT, false));
    }

    @Override
    public void render() {
        for (GameObject o : objects) {
            Renderer.getUi().render(o);
        }
        mainMenu.render();
        loadingMenu.render();
        inGameMenu.render();
        mapMenu.render();
    }

    @Override
    public void unload() {
        for (GameObject o : objects) {
            o.unload();
        }

        mainMenu.unload();
        loadingMenu.unload();
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
