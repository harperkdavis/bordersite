package game.scene;

import engine.collision.BoxCollider3f;
import engine.collision.RampCollider3f;
import engine.graphics.Material;
import engine.graphics.mesh.MeshBuilder;
import engine.graphics.render.Renderer;
import engine.io.MeshLoader;
import engine.math.*;
import engine.objects.GameObject;
import game.GamePlane;
import game.PlayerMovement;

import java.util.*;

public class Scene implements GamePlane {

    protected static Scene scene;
    protected static float[][] heightMap;

    protected static boolean loaded = false;
    protected static boolean loading = false;

    public GameObject groundPlane;

    public static List<GameObject> objects = new ArrayList<>();
    public static List<Component> components = new ArrayList<>();
    private static final List<GameObject> bufferedObjects = new ArrayList<>();

    public static Map<String, GameObject> playerObjects = new HashMap<>();

    private static SceneLoader loader;

    protected static float SCALE_X = 4;
    protected static float SCALE_Y = 1;
    protected static float SCALE_Z = 4;

    public Scene() {

        loader = new SceneLoader();

        heightMap = new float[512][512];
        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {
                heightMap[i][j] = 0;
            }
        }

    }

    @Override
    public void load() {
        // Load ARENA
        // Outer Walls

        // Spawns
        addBox(new Vector3f(4, 0, 4), new Vector3f(20, 4, 20), Material.ENV_FLOOR);
        addBox(new Vector3f(20, 0, 12), new Vector3f(36, 4, 20), Material.ENV_FLOOR);
        addBox(new Vector3f(36, 0, 12), new Vector3f(52, 12, 24), Material.ENV_BRICK);

        addObject(new GameObject(Vector3f.zero(), MeshLoader.loadModel("sphere.obj", Material.ENV_FLOOR)), false);

        loading = true;
        loaded = false;
    }

    @Override
    public void update() {
        if (loading) {
            loader.run();
            return;
        }

        for (int i = 0; i < bufferedObjects.size(); i++) {
            GameObject object = bufferedObjects.remove(i);
            object.load();
            objects.add(object);
        }

    }

    @Override
    public void render() {

    }

    @Override
    public void fixedUpdate() {

    }

    @Override
    public void unload() {
        for (GameObject go : objects) {
                go.unload();
        }
    }

    private void addBox(Vector3f a, Vector3f b, Material material) {
        addObject(new GameObject(Vector3f.zero(), MeshBuilder.Rect(a, b, material)), false);
        PlayerMovement.addCollisionRegion(new BoxCollider3f(a, b));
    }

    private void addRamp(Vector3f a, Vector3f b, int direction, Material material) {
        addObject(new GameObject(Vector3f.zero(), MeshBuilder.Ramp(a, b, direction, material)), false);
        PlayerMovement.addCollisionRegion(new RampCollider3f(a, b, direction));
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

    public static void addBufferedObject(GameObject object) {
        bufferedObjects.add(object);
    }

    public static SceneLoader getLoader() {
        return loader;
    }

    public static boolean isLoading() {
        return loading;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static Scene getScene() {
        return scene;
    }

    public static void setScene(Scene scene) {
        Scene.scene = scene;
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

    public static float getScaleX() {
        return SCALE_X;
    }

    public static float getScaleY() {
        return SCALE_Y;
    }

    public static float getScaleZ() {
        return SCALE_Z;
    }
}
