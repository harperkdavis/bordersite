package game.world;

import engine.graphics.Material;
import engine.graphics.MeshBuilder;
import engine.graphics.Renderer;
import engine.math.Mathf;
import engine.math.Vector3f;
import engine.objects.Camera;
import engine.objects.GameObject;
import engine.objects.GameObjectGroup;
import engine.objects.GameObjectMesh;

import java.util.ArrayList;
import java.util.List;

public class World {

    private static World world;
    private static float[][] heightMap;

    public GameObject groundPlane;

    public static List<GameObject> objects = new ArrayList<>();
    public static List<GameObject> playerObjects = new ArrayList<>();

    private List<GameObject> playerObjectList = new ArrayList<>();

    private long time;

    public World() {

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

        // Load Heightmap
        Material map = new Material("/textures/heightmap.png");
        map.create();

        byte[] textureData = map.getTexture().getTextureData();
        heightMap = new float[512][512];

        for (int x = 0; x < 512; x++) {
            for (int z = 0; z < 512; z++) {
                int data = textureData[(z * 512 + x) * 4];
                heightMap[x][z] = data >= 0 ? data : data + 256.0f;
            }
        }

        // Smooth Heightmap
        for (int i = 0; i < 3; i++) {
            float[][] newHeightMap = new float[512][512];
            for (int x = 0; x < 512; x++) {
                for (int z = 0; z < 512; z++) {
                    int sum = 0;
                    int count = 0;
                    for (int j = -1; j <= 1; j++) {
                        for (int k = -1; k <= 1; k++) {
                            if (!(x + j < 0 || x + j >= 510 || z + k < 0 || z + k >= 510)) {
                                sum += heightMap[x + j][z + k];
                                count ++;
                            }
                        }
                    }
                    newHeightMap[x][z] = (heightMap[x][z] + (float) sum / count) / 2.0f;
                }
            }
            heightMap = newHeightMap.clone();
        }

        groundPlane = new GameObjectMesh(new Vector3f(0, 0, 0), Vector3f.zero(), new Vector3f(4, 2, 4), MeshBuilder.Terrain(new Material("/textures/grass.png")));
        objects.add(groundPlane);

        // Load Trees
        Material treeTex = new Material("/textures/treemap.png");
        treeTex.create();

        byte[] treeMap = treeTex.getTexture().getTextureData();

        List<Vector3f> positions = new ArrayList<>();
        List<Integer> seedA = new ArrayList<>();
        List<Integer> seedB = new ArrayList<>();
        for (int ax = 0; ax < 32; ax++) {
            for (int az = 0; az < 32; az++) {

                for (int bx = 0; bx < 16; bx++) {
                    for (int bz = 0; bz < 16; bz++) {
                        int x = ax * 16 + bx;
                        int z = az * 16 + bz;

                        int r = treeMap[(z * 512 + x) * 4] >= 0 ? treeMap[(z * 512 + x) * 4] : treeMap[(z * 512 + x) * 4] + 256;
                        int g = treeMap[(z * 512 + x) * 4 + 1] >= 0 ? treeMap[(z * 512 + x) * 4 + 1] : treeMap[(z * 512 + x) * 4 + 1] + 256;
                        int b = treeMap[(z * 512 + x) * 4 + 2] >= 0 ? treeMap[(z * 512 + x) * 4 + 2] : treeMap[(z * 512 + x) * 4 + 2] + 256;

                        seedA.add(r);
                        seedB.add(g);

                        if (b >= 0) {
                            float v = b / 256.0f;
                            v *= x % 4 == 0 ? 1.0f : 0.5f;
                            v *= z % 4 == 0 ? 1.0f : 0.5f;
                            if (v > 0.5f) {
                                float posX = x * groundPlane.getScale().getX() + (r - 128.0f) / 8.0f;
                                float posZ = z * groundPlane.getScale().getZ() + (g - 128.0f) / 8.0f;
                                posX = Math.max(Math.min(posX, 512.0f * groundPlane.getScale().getX()), 0.0f);
                                posZ = Math.max(Math.min(posZ, 512.0f * groundPlane.getScale().getZ()), 0.0f);
                                positions.add(new Vector3f(posX, getTerrainHeight(posX, posZ) - 0.5f, posZ));
                            }
                        }

                    }
                }
            }
        }
        objects.add(new GameObjectMesh(new Vector3f(0.0f, 0.0f, 0.0f), Vector3f.zero(), Vector3f.one(), MeshBuilder.TreeChunk(15.0f, positions, seedA, seedB, new Material("/textures/tree.png"))));


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
            if (go != null) {
                Renderer.getRenderer().renderMesh(go, Camera.getMainCamera());
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

    public static void removeObject(GameObject object) {
        objects.remove(object);
        if (object instanceof GameObjectGroup) {
            ((GameObjectGroup) object).unload();
        } else if (object instanceof GameObjectMesh) {
            ((GameObjectMesh) object).unload();
        }
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
        Vector3f scale = getWorld().groundPlane.getScale();
        if (x >= 0 && x <= 512 * scale.getX() && z >= 0 && z <= 512 * scale.getZ()) {
            int minX = (int) (x / scale.getX());
            int maxX = (int) Math.ceil(x / scale.getX());
            int minZ = (int) (z / scale.getZ());
            int maxZ = (int) Math.ceil(z / scale.getZ());
            minX = Math.min(Math.max(minX, 0), 511);
            maxX = Math.min(Math.max(maxX, 0), 511);
            minZ = Math.min(Math.max(minZ, 0), 511);
            maxZ = Math.min(Math.max(maxZ, 0), 511);
            float inX = x / scale.getX() - minX;
            float inZ = z / scale.getZ() - minZ;
            return Mathf.lerp(Mathf.lerp(getHeightMap()[minX][minZ], getHeightMap()[maxX][minZ], inX), Mathf.lerp(getHeightMap()[minX][maxZ], getHeightMap()[maxX][maxZ], inX), inZ) * scale.getY();
        }
        return 0;
    }
}
