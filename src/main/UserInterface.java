package main;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.*;
import engine.math.Vector3f;
import engine.objects.GameObject;

public class UserInterface {

    private final float PIXEL = 4.0f / 900.0f;
    private Main main;

    private List<GameObject> crosshair = new ArrayList<GameObject>();

    private GameObject text = new GameObject(new Vector3f(0, 1.9f, 1), new Vector3f(0, 0, 0), new Vector3f(1f, 1f, 1f), MeshBuilder.TextMesh("bordersite alpha", PIXEL * 16, TextMode.CENTER));

    public UserInterface(Main main) {
        crosshair.add(new GameObject(new Vector3f(0, 0, 1), new Vector3f(90, 0, 0), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-mid.png"))));
        crosshair.add(new GameObject(new Vector3f(0, PIXEL * 24 * main.playerMovement.getRecoil(), 1), new Vector3f(90, 0, 90), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-out.png")))); // Top
        crosshair.add(new GameObject(new Vector3f(0, -PIXEL * 24 * main.playerMovement.getRecoil(), 1), new Vector3f(90, 0, 90), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-out.png")))); // Bottom
        crosshair.add(new GameObject(new Vector3f(-PIXEL * 24 * main.playerMovement.getRecoil(), 0, 1), new Vector3f(90, 0, 0), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-out.png")))); // Left
        crosshair.add(new GameObject(new Vector3f(PIXEL * 24 * main.playerMovement.getRecoil(), 0, 1), new Vector3f(90, 0, 0), new Vector3f(PIXEL * 8, PIXEL * 8, PIXEL * 8),
                MeshBuilder.Plane(1, new Material("/textures/crosshair-out.png")))); // Right
        this.main = main;
    }

    public void load() {
        for (GameObject go : crosshair) {
            go.load();
        }
        text.load();
    }

    public void update() {
        float recoilOffset = main.playerMovement.getRecoil() > 1 ? main.playerMovement.getRecoil() * main.playerMovement.getRecoil() : main.playerMovement.getRecoil();
        crosshair.get(1).position.set(0, PIXEL * 24 * recoilOffset, 1);
        crosshair.get(2).position.set(0, -PIXEL * 24 * recoilOffset, 1);
        crosshair.get(3).position.set(-PIXEL * 24 * recoilOffset, 0, 1);
        crosshair.get(4).position.set(PIXEL * 24 * recoilOffset, 0, 1);
    }

    public void render(Renderer renderer) {
        for (int i = 0; i < crosshair.size(); i++) {
            renderer.renderMesh(crosshair.get(i), null);
        }
        renderer.renderMesh(text, null);
    }

    public void unload() {
        for (GameObject go : crosshair) {
            go.unload();
        }
        text.unload();
    }
}
