package main;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.*;
import engine.math.Vector3f;
import engine.objects.GameObject;

public class UserInterface {

    private final float PIXEL = 4.0f / 900.0f;
    private Main main;

    private List<GameObject> uiObjects = new ArrayList<>();
    private List<GameObject> crosshair = new ArrayList<>();

    private GameObject gameScreen;
    private GameObject gameText;

    public UserInterface(Main main) {
        crosshair.add(add(new GameObject(new Vector3f(0, 0, 1), new Vector3f(90, 0, 0), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-mid.png")))));
        crosshair.add(add(new GameObject(new Vector3f(0, PIXEL * 24 * main.playerMovement.getRecoil(), 1), new Vector3f(90, 0, 90), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-out.png"))))); // Top
        crosshair.add(add(new GameObject(new Vector3f(0, -PIXEL * 24 * main.playerMovement.getRecoil(), 1), new Vector3f(90, 0, 90), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-out.png"))))); // Bottom
        crosshair.add(add(new GameObject(new Vector3f(-PIXEL * 24 * main.playerMovement.getRecoil(), 0, 1), new Vector3f(90, 0, 0), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-out.png"))))); // Left
        crosshair.add(add(new GameObject(new Vector3f(PIXEL * 24 * main.playerMovement.getRecoil(), 0, 1), new Vector3f(90, 0, 0), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-out.png"))))); // Right

        gameScreen = new GameObject(Vector3f.oneZ(), Vector3f.uirzero(), Vector3f.one().multiply(5), MeshBuilder.Plane(2, new Material("/textures/black.png")));
        gameText = new GameObject(Vector3f.oneZ(), Vector3f.zero(), Vector3f.one(), MeshBuilder.TextMesh("connecting to server...", PIXEL * 15, TextMode.CENTER));
        this.main = main;
    }

    public void load() {
        for (GameObject o : uiObjects) {
            o.load();
        }
        gameScreen.load();
        gameText.load();
    }

    public void update() {
        if (main.getSocketClient().isConnected()) {
            float recoilOffset = main.playerMovement.getRecoil() > 1 ? main.playerMovement.getRecoil() * main.playerMovement.getRecoil() : main.playerMovement.getRecoil();
            crosshair.get(1).position.set(0, PIXEL * 24 * recoilOffset, 1);
            crosshair.get(2).position.set(0, -PIXEL * 24 * recoilOffset, 1);
            crosshair.get(3).position.set(-PIXEL * 24 * recoilOffset, 0, 1);
            crosshair.get(4).position.set(PIXEL * 24 * recoilOffset, 0, 1);
        } else {
            gameText.setMesh(MeshBuilder.TextMesh("connecting to server... " + System.currentTimeMillis(), PIXEL * 15, TextMode.CENTER));
        }
    }

    public void render(Renderer renderer) {
        if (main.getSocketClient().isConnected()) {
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
            o.unload();
        }
        gameScreen.unload();
        gameText.unload();
    }

    public GameObject add(GameObject object) {
        uiObjects.add(object);
        return object;
    }

    public void remove(GameObject object) {
        uiObjects.remove(object);
    }
}
