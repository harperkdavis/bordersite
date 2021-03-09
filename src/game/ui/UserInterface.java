package game.ui;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.*;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector3f;
import engine.objects.GameObject;
import engine.objects.GameObjectGroup;
import engine.objects.GameObjectMesh;
import game.PlayerMovement;
import game.world.World;
import net.Client;
import org.lwjgl.glfw.GLFW;

public class UserInterface {

    private static UserInterface ui;

    private static float PIXEL = 0.0025f;
    private static int width;
    private static int height;

    private static List<GameObject> uiObjects = new ArrayList<>();

    private boolean inMainMenu = false;

    private Menu mainMenu;
    private Menu loadingMenu;


    public UserInterface(int width, int height) {
        this.width = width;
        this.height = height;

        PIXEL = 1.0f / (height / 2.25f);

        mainMenu = new MainMenu();
        loadingMenu = new LoadingMenu();
    }

    public void load() {
        for (GameObject o : uiObjects) {
            if (o instanceof GameObjectGroup) {
                ((GameObjectGroup) o).load();
            } else if (o instanceof GameObjectMesh) {
                ((GameObjectMesh) o).load();
            }
        }

        mainMenu.load();
        loadingMenu.load();
    }

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

        if (Client.isConnected()) {
            float recoil = PlayerMovement.getPlayerMovement().getRecoil();
            float recoilOffset = recoil > 1 ? recoil * recoil : recoil;
        }
    }

    public void render(Renderer renderer) {
        for (GameObject o : uiObjects) {
            renderer.renderMesh(o, null);
        }

        mainMenu.render(renderer);
        loadingMenu.render(renderer);
    }

    public void unload() {
        for (GameObject o : uiObjects) {
            if (o instanceof GameObjectGroup) {
                ((GameObjectGroup) o).unload();
            } else if (o instanceof GameObjectMesh) {
                ((GameObjectMesh) o).unload();
            }
        }

        mainMenu.unload();
        loadingMenu.unload();
    }

    public static GameObject addObject(GameObject object) {
        uiObjects.add(object);
        if (object instanceof GameObjectGroup) {
            ((GameObjectGroup) object).load();
        } else if (object instanceof GameObjectMesh) {
            ((GameObjectMesh) object).load();
        }
        return object;
    }

    public static GameObject addObjectWithoutLoading(GameObject object) {
        uiObjects.add(object);
        return object;
    }

    public static void removeObject(GameObject object) {
        uiObjects.remove(object);
        if (object instanceof GameObjectGroup) {
            ((GameObjectGroup) object).unload();
        } else if (object instanceof GameObjectMesh) {
            ((GameObjectMesh) object).unload();
        }
    }

    public static UserInterface getUi() {
        return ui;
    }

    public static void setUi(UserInterface ui) {
        UserInterface.ui = ui;
    }

    public static Vector3f screen(float posX, float posY, int layer) {
        return new Vector3f((posX * PIXEL * (width / 2.0f)) - (width / 2.0f) * PIXEL, (posY * PIXEL * (height / 2.0f)) + (height / 2.0f) * PIXEL, layer);

    }

    public static float p(float n) {
        return n * PIXEL;
    }

    public static float p(int n) {
        return n * PIXEL;
    }

    public static float getNormMouseX() {
        return ((float) Input.getMouseX() - Window.getWidth() / 2.0f) / (Window.getWidth() / 2.0f);
    }

    public static float getNormMouseY() {
        return (Window.getHeight() / 2.0f - (float) Input.getMouseY()) / (Window.getHeight() / 2.0f);
    }
}
