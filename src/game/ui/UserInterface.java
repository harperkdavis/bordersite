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

public class UserInterface implements GamePlane {

    private static UserInterface ui;

    private static float PIXEL = 0.0025f;
    private static int width;
    private static int height;

    private static List<GameObject> objects = new ArrayList<>();

    private boolean inMainMenu = false;

    private Menu mainMenu;
    private Menu loadingMenu;
    private Menu inGameMenu;

    private UiObject fpsLabel;

    public static boolean mouseLock;

    public UserInterface(int width, int height) {
        this.width = width;
        this.height = height;

        PIXEL = 1.0f / (height / 2.25f);

        mainMenu = new MainMenu();
        loadingMenu = new LoadingMenu();
        inGameMenu = new InGameMenu();

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
    }

    @Override
    public void update() {

        if (inMainMenu) {
            mainMenu.setVisible(true);
            mainMenu.update();
        } else {
            mainMenu.setVisible(false);
        }

        if (World.isLoading()) {
            loadingMenu.setVisible(true);
            loadingMenu.update();
        } else {
            loadingMenu.setVisible(false);
        }

        if (!World.isLoading() && !inMainMenu) {
            inGameMenu.setVisible(true);
            inGameMenu.update();
        } else {
            inGameMenu.setVisible(false);
        }

        fpsLabel.setMesh(TextMeshBuilder.TextMesh("fps: " + Window.getGameWindow().getFPS(), p(16), TextMode.LEFT, false));
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
        loadingMenu.render();
        inGameMenu.render();
    }

    @Override
    public void unload() {
        for (GameObject o : objects) {
            o.unload();
        }

        mainMenu.unload();
        loadingMenu.unload();
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
