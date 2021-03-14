package game.world;

import engine.graphics.Material;
import engine.graphics.MeshBuilder;
import engine.graphics.Renderer;
import engine.io.MeshLoader;
import engine.math.Mathf;
import engine.math.Vector3f;
import engine.objects.Camera;
import engine.objects.GameObject;
import engine.objects.GameObjectGroup;
import engine.objects.GameObjectMesh;
import net.Client;
import net.packets.PacketLoaded;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {

    protected static World world;
    protected static float[][] heightMap;

    protected static boolean loaded = false;
    protected static boolean loading = false;

    public GameObject groundPlane;

    public static List<GameObject> objects = new ArrayList<>();
    private static List<GameObject> bufferedObjects = new ArrayList<>();

    public static Map<String, GameObject> playerObjects = new HashMap<>();

    private List<GameObject> playerObjectList = new ArrayList<>();

    public static GameObjectMesh cube;
    private static WorldLoader loader;

    protected static float SCALE_X = 4;
    protected static float SCALE_Y = 1;
    protected static float SCALE_Z = 4;

    public World() {

        // Head
        playerObjectList.add(new GameObjectMesh(new Vector3f(0, 1.75f, 0), Vector3f.zero(), Vector3f.one(), MeshBuilder.PlayerHead(new Material("/textures/player.png"))));
        playerObjectList.add(new GameObjectMesh(new Vector3f(0, 1.0f, 0), Vector3f.zero(), Vector3f.one(), MeshBuilder.PlayerTorso(new Material("/textures/player.png"))));

        playerObjectList.add(new GameObjectMesh(new Vector3f(-0.5f, 1.55f, 0), Vector3f.zero(), new Vector3f(1.0f, 1.0f, 1.0f), MeshBuilder.PlayerArm(new Material("/textures/player.png"))));
        playerObjectList.add(new GameObjectMesh(new Vector3f(0.5f, 1.55f, 0), Vector3f.zero(), new Vector3f(-1.0f, 1.0f, 1.0f), MeshBuilder.PlayerArm(new Material("/textures/player.png"))));

        playerObjectList.add(new GameObjectMesh(new Vector3f(-0.175f, 0.85f, 0), Vector3f.zero(), Vector3f.one(), MeshBuilder.PlayerLeg(new Material("/textures/player.png"))));
        playerObjectList.add(new GameObjectMesh(new Vector3f(0.175f, 0.85f, 0), Vector3f.zero(), Vector3f.one(), MeshBuilder.PlayerLeg(new Material("/textures/player.png"))));

        cube = new GameObjectMesh(Vector3f.zero(), Vector3f.zero(), Vector3f.one(), MeshBuilder.Cube(2, new Material("/textures/test.png")));
        objects.add(cube);

        loader = new WorldLoader();

        addObjectWithoutLoading(new GameObjectMesh(Vector3f.zero(), Vector3f.zero(), Vector3f.one().multiply(10), MeshLoader.loadModel("/models/untitled.obj", new Material("/textures/test.png"))));

        for (GameObject go : playerObjectList) {
            objects.add(go);
        }
    }

    public void load() {
        loading = true;
        loaded = false;
    }

    public void update() {
        if (loading) {
            loader.run();
            return;
        }
        for (int i = 0; i < bufferedObjects.size(); i++) {
            GameObject object = bufferedObjects.remove(i);
            if (object instanceof GameObjectMesh) {
                ((GameObjectMesh) object).load();
            } else if (object instanceof GameObjectGroup) {
                ((GameObjectGroup) object).load();
            }
            objects.add(object);
        }
    }

    public void render() {
        if (isLoaded()) {
            for (GameObject go : objects) {
                if (go != null) {
                    Renderer.getRenderer().renderMesh(go, Camera.getMainCamera());
                }
            }
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

    public static void addObjectWithoutLoading(GameObject object) {
        objects.add(object);
    }

    public static void removeObject(GameObject object) {
        objects.remove(object);
        if (object instanceof GameObjectGroup) {
            ((GameObjectGroup) object).unload();
        } else if (object instanceof GameObjectMesh) {
            ((GameObjectMesh) object).unload();
        }
    }

    public static void addBufferedObject(GameObject object) {
        bufferedObjects.add(object);
    }



    public static WorldLoader getLoader() {
        return loader;
    }

    public static boolean isLoading() {
        return loading;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static World getWorld() {
        return world;
    }

    public static void setWorld(World world) {
        World.world = world;
    }

    public static float[][] getHeightMap() {
        return heightMap;
    }

    public static float getTerrainHeight(float x, float z) {
        if (x >= 0 && x <= 512 * SCALE_X && z >= 0 && z <= 512 * SCALE_Z) {
            int minX = (int) (x / SCALE_X);
            int maxX = (int) Math.ceil(x / SCALE_X);
            int minZ = (int) (z / SCALE_Z);
            int maxZ = (int) Math.ceil(z / SCALE_Z);
            minX = Math.min(Math.max(minX, 0), 511);
            maxX = Math.min(Math.max(maxX, 0), 511);
            minZ = Math.min(Math.max(minZ, 0), 511);
            maxZ = Math.min(Math.max(maxZ, 0), 511);
            float inX = x / SCALE_X - minX;
            float inZ = z / SCALE_Z - minZ;
            return Mathf.lerp(Mathf.lerp(getHeightMap()[minX][minZ], getHeightMap()[maxX][minZ], inX), Mathf.lerp(getHeightMap()[minX][maxZ], getHeightMap()[maxX][maxZ], inX), inZ) * SCALE_Y;
        }
        return 0;
    }

    public static Map<String, GameObject> getPlayerObjects() {
        return playerObjects;
    }

    public static float getSlope(int x, int z)  {
        if (x >= 0 && x < 512 && z >= 0 && z < 512) {
            float heightSum = 0;
            float heightPoints = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 && j != 0 && x + i >= 0 && x + i < 512 && z + j >= 0 && z + j < 512) {
                        heightSum += getHeightMap()[x + i][z + j];
                        heightPoints += 1;
                    }
                }
            }
            heightSum /= heightPoints;
            float averageHeight = (heightSum + getHeightMap()[x][z]) / 2.0f;
            float distance = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 && j != 0 && x + i >= 0 && x + i < 512 && z + j >= 0 && z + j < 512) {
                        distance += Math.abs(averageHeight - getHeightMap()[x + i][z + j]);
                    }
                }
            }
            return distance;
        }
        return 0;
    }
}
