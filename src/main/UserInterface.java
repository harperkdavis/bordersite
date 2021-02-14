package main;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.*;
import engine.math.Vector3f;
import engine.objects.GameObject;
import engine.objects.GameObjectGroup;
import engine.objects.GameObjectMesh;
import net.Client;

public class UserInterface {

    private static UserInterface ui;

    private final float PIXEL = 0.0025f;

    private static List<GameObject> uiObjects = new ArrayList<>();
    private List<GameObject> crosshair = new ArrayList<>();

    private GameObjectMesh gameScreen;
    private GameObjectMesh gameText;

    public UserInterface() {

        crosshair.add(addObjectWithoutLoading(new GameObjectMesh(new Vector3f(0, 0, 1), new Vector3f(90, 0, 0), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-mid.png")))));
        crosshair.add(addObjectWithoutLoading(new GameObjectMesh(new Vector3f(0, -PIXEL * 24 * PlayerMovement.getPlayerMovement().getRecoil(), 1), new Vector3f(90, 0, 90), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-out.png"))))); // Top
        crosshair.add(addObjectWithoutLoading(new GameObjectMesh(new Vector3f(0, PIXEL * 24 * PlayerMovement.getPlayerMovement().getRecoil(), 1), new Vector3f(90, 0, 90), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-out.png"))))); // Bottom
        crosshair.add(addObjectWithoutLoading(new GameObjectMesh(new Vector3f(-PIXEL * 24 * PlayerMovement.getPlayerMovement().getRecoil(), 0, 1), new Vector3f(90, 0, 0), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-out.png"))))); // Left
        crosshair.add(addObjectWithoutLoading(new GameObjectMesh(new Vector3f(PIXEL * 24 * PlayerMovement.getPlayerMovement().getRecoil(), 0, 1), new Vector3f(90, 0, 0), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-out.png"))))); // Right

        gameScreen = new GameObjectMesh(Vector3f.oneZ(), Vector3f.uirzero(), Vector3f.one().multiply(5), MeshBuilder.Plane(2, new Material("/textures/black.png")));
        gameText = new GameObjectMesh(Vector3f.oneZ(), Vector3f.zero(), Vector3f.one(), MeshBuilder.TextMesh("connecting to server...", PIXEL * 15, TextMode.CENTER));

        uiObjects.add(new GameObjectMesh(Vector3f.oneZ(), Vector3f.uirzero(), Vector3f.one(), MeshBuilder.Plane(0.5f, new Material("/textures/bordersite-logo.png"))));

    }

    public void load() {
        for (GameObject o : uiObjects) {
            if (o instanceof GameObjectGroup) {
                ((GameObjectGroup) o).load();
            } else if (o instanceof GameObjectMesh) {
                ((GameObjectMesh) o).load();
            }
        }
        gameScreen.load();
        gameText.load();
    }

    public void update() {
        if (Client.getSocketClient().isConnected()) {
            float recoil = PlayerMovement.getPlayerMovement().getRecoil();
            float recoilOffset = recoil > 1 ? recoil * recoil : recoil;
            crosshair.get(1).setPosition(new Vector3f(0, PIXEL * 24 * recoilOffset, 1));
            crosshair.get(2).setPosition(new Vector3f(0, -PIXEL * 24 * recoilOffset, 1));
            crosshair.get(3).setPosition(new Vector3f(-PIXEL * 24 * recoilOffset, 0, 1));
            crosshair.get(4).setPosition(new Vector3f(PIXEL * 24 * recoilOffset, 0, 1));

        } else {
            gameText.setMesh(MeshBuilder.TextMesh("connecting to server... " + System.currentTimeMillis(), PIXEL * 15, TextMode.CENTER));
        }
    }

    public void render(Renderer renderer) {
        if (Client.getSocketClient().isConnected()) {
            for (GameObject o : uiObjects) {
                renderer.renderMesh(o, null);
            }
        } else {
            renderer.renderMesh(gameScreen, null);
            renderer.renderMesh(gameText, null);
        }
    }

    public void unload() {
        for (GameObject o : uiObjects) {
            if (o instanceof GameObjectGroup) {
                ((GameObjectGroup) o).unload();
            } else if (o instanceof GameObjectMesh) {
                ((GameObjectMesh) o).unload();
            }
        }
        gameScreen.unload();
        gameText.unload();
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
}
