package main;

import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.graphics.MeshBuilder;
import engine.math.Vector3f;
import engine.objects.GameObject;
import engine.math.Region3f;
import engine.objects.GameObjectGroup;
import engine.objects.GameObjectMesh;
import org.lwjglx.Sys;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {

    private Main main;

    public GameObject groundPlane = new GameObjectMesh(new Vector3f(0, 0, 0), Vector3f.zero(), new Vector3f(4, 4, 4), MeshBuilder.TiledPlane(400, new Material("/textures/grass.png")));

    public static List<GameObject> objects = new ArrayList<>();
    public static List<GameObject> playerObjects = new ArrayList<>();

    private List<GameObject> playerObjectList = new ArrayList<>();

    private long time;

    public World(Main main) {
        this.main = main;
        objects.add(groundPlane);

        time = System.currentTimeMillis();
        // Head
        playerObjectList.add(new GameObjectMesh(new Vector3f(0, 1.75f, 0), Vector3f.zero(), Vector3f.one(), MeshBuilder.PlayerHead(new Material("/textures/player.png"))));
        playerObjectList.add(new GameObjectMesh(new Vector3f(0, 1.0f, 0), Vector3f.zero(), Vector3f.one(), MeshBuilder.PlayerTorso(new Material("/textures/player.png"))));

        playerObjectList.add(new GameObjectMesh(new Vector3f(-0.5f, 1.55f, 0), Vector3f.zero(), new Vector3f(1.0f, 1.0f, 1.0f), MeshBuilder.PlayerArm(new Material("/textures/player.png"))));
        playerObjectList.add(new GameObjectMesh(new Vector3f(0.5f, 1.55f, 0), Vector3f.zero(), new Vector3f(-1.0f, 1.0f, 1.0f), MeshBuilder.PlayerArm(new Material("/textures/player.png"))));

        playerObjectList.add(new GameObjectMesh(new Vector3f(-0.175f, 0.85f, 0), Vector3f.zero(), Vector3f.one(), MeshBuilder.PlayerLeg(new Material("/textures/player.png"))));
        playerObjectList.add(new GameObjectMesh(new Vector3f(0.175f, 0.85f, 0), Vector3f.zero(), Vector3f.one(), MeshBuilder.PlayerLeg(new Material("/textures/player.png"))));

        for (GameObject go : playerObjectList) {
            objects.add(go);
        }
    }

    public void load() {
        for (GameObject go : objects) {
            if (go instanceof GameObjectMesh) {
                ((GameObjectMesh) go).load();
            } else if (go instanceof GameObjectGroup) {
                ((GameObjectGroup) go).load();
            }
        }
    }

    public void update() {

    }

    public void render() {
        for (GameObject go : objects) {
            main.renderer.renderMesh(go, main.camera);
        }
    }


    public void unload() {
        for (GameObject go : objects) {
            if (go instanceof GameObjectMesh) {
                ((GameObjectMesh) go).unload();
            } else if (go instanceof GameObjectGroup) {
                ((GameObjectGroup) go).unload();
            }
        }
    }

    public static void addObject(GameObject object) {
        objects.add(object);
        if (object instanceof GameObjectGroup) {
            ((GameObjectGroup) object).load();
        } else if (object instanceof GameObjectMesh) {
            ((GameObjectMesh) object).load();
        }
    }

    public static void removeObject(GameObject object) {
        objects.remove(object);
        if (object instanceof GameObjectGroup) {
            ((GameObjectGroup) object).unload();
        } else if (object instanceof GameObjectMesh) {
            ((GameObjectMesh) object).unload();
        }
    }

}
