package game.world;

import engine.audio.AudioBuffer;
import engine.audio.AudioMaster;
import engine.audio.AudioSource;
import engine.audio.SoundEffect;
import engine.graphics.Material;
import engine.graphics.mesh.Mesh3f;
import engine.graphics.mesh.MeshBuilder;
import engine.graphics.vertex.Vertex3f;
import engine.math.Region3f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.GameObject;
import engine.objects.GameObjectMesh;
import game.PlayerMovement;
import net.Client;
import net.packets.PacketLoaded;

import java.util.ArrayList;
import java.util.List;

public class WorldLoader {

    private int loadingStage = 0;

    private long beginTime;
    private long elapsedTime;

    private int nextOperation;
    private long operationStart;

    private String loadingName;
    private float loadingProgress;

    private final int TREE_SIZE = 20;
    private final float TREE_WIDTH = 1.0f;

    // Loading-specific variables

    private byte[] textureData;
    private byte[] treeMap;
    private byte[] treeNoiseMap;

    private List<Vector3f> treePositions = new ArrayList<>();
    private List<Integer> treeASeeds = new ArrayList<>();
    private List<Integer> treeBSeeds = new ArrayList<>();

    private int treeChunkX = 0;
    private int treeChunkZ = 0;

    private int smoothIterations = 0;

    private int treeIndex = 0;

    private boolean vertsInitialized = false;

    private Vertex3f[] vertices;
    private int[] triangles;

    WorldLoader() {
        beginTime = System.currentTimeMillis();
        loadingName = "Loading world...";
    }


    public void run() {
        elapsedTime = System.currentTimeMillis() - beginTime;
        if (elapsedTime < nextOperation) {
            return;
        }

        if (loadingStage == 0) {
            System.out.println("[INFO] Loading world...");
            loadingName = "Loading Heightmap...";
            // Load Heightmap
            Material map = new Material("/textures/heightmap.png");
            map.create();

            textureData = map.getTexture().getTextureData();
            World.heightMap = new float[512][512];

            for (int x = 0; x < 512; x++) {
                for (int z = 0; z < 512; z++) {
                    // int data = textureData[(z * 512 + x) * 4];
                    // World.heightMap[x][z] = data >= 0 ? data : data + 256.0f;
                    World.heightMap[x][z] = 0;
                }
            }

            advanceLoading();
            wait(5);
        } else if (loadingStage == 1) {
            loadingName = "Smoothing Heightmap...";
            loadingProgress = smoothIterations / 3.0f;
            // Smooth Heightmap
            if (smoothIterations < 3) {
                float[][] newHeightMap = new float[512][512];
                for (int x = 0; x < 512; x++) {
                    for (int z = 0; z < 512; z++) {
                        int sum = 0;
                        int count = 0;
                        for (int j = -1; j <= 1; j++) {
                            for (int k = -1; k <= 1; k++) {
                                if (!(x + j < 0 || x + j >= 510 || z + k < 0 || z + k >= 510)) {
                                    sum += World.heightMap[x + j][z + k];
                                    count++;
                                }
                            }
                        }
                        newHeightMap[x][z] = (World.heightMap[x][z] + (float) sum / count) / 2.0f;
                    }
                }
                World.heightMap = newHeightMap.clone();
                smoothIterations++;
                wait(5);
            } else if (smoothIterations == 3) {
                AudioSource source = new AudioSource(true, false);
                source.setBuffer(AudioBuffer.getSoundEffectBufferId(SoundEffect.BACKGROUND));
                AudioMaster.addSoundSource("background", source);
                advanceLoading();
                wait(10);
            }
        } else if (loadingStage == 2) {
            World.getWorld().groundPlane = new GameObjectMesh(new Vector3f(0, 0, 0), Vector3f.zero(), new Vector3f(World.SCALE_X, World.SCALE_Y, World.SCALE_Z), MeshBuilder.Terrain(new Material("/textures/grass.png")));
            World.objects.add(World.getWorld().groundPlane);

            // Load Trees
            Material treeTex = new Material("/textures/treemap.png");
            treeTex.create();

            Material treeNoiseTex = new Material("/textures/treemap-noise.png");
            treeNoiseTex.create();

            treeMap = treeTex.getTexture().getTextureData();
            treeNoiseMap = treeNoiseTex.getTexture().getTextureData();
            advanceLoading();
            wait(5);
        } else if (loadingStage == 3) {
            loadingName = "Loading trees...";
            loadingProgress = (treeChunkX + treeChunkZ * 32) / 1024.0f;
            for (int i = 0; i < 32; i++) {
                if (i == 0 && treeChunkZ == 0) {
                    continue;
                }
                for (int bx = 0; bx < 16; bx++) {
                    for (int bz = 0; bz < 16; bz++) {
                        int x = (treeChunkX + i) * 16 + bx;
                        int z = (treeChunkZ) * 16 + bz;

                        int r = treeMap[(z * 512 + x) * 4] >= 0 ? treeMap[(z * 512 + x) * 4] : treeMap[(z * 512 + x) * 4] + 256;
                        int g = treeMap[(z * 512 + x) * 4 + 1] >= 0 ? treeMap[(z * 512 + x) * 4 + 1] : treeMap[(z * 512 + x) * 4 + 1] + 256;
                        int b = treeMap[(z * 512 + x) * 4 + 2] >= 0 ? treeMap[(z * 512 + x) * 4 + 2] : treeMap[(z * 512 + x) * 4 + 2] + 256;

                        int n = treeNoiseMap[(z * 512 + x) * 4] >= 0 ? treeNoiseMap[(z * 512 + x) * 4] : treeNoiseMap[(z * 512 + x) * 4] + 256;

                        treeASeeds.add(r);
                        treeBSeeds.add(g);

                        if (b >= n) {
                            float v = 1.0f;
                            v *= x % 100 == 0 ? 1.0f : 0.5f;
                            v *= z % 100 == 0 ? 1.0f : 0.5f;
                            if (v > 0.5f) {
                                float posX = x * World.SCALE_X + (r - 128.0f);
                                float posZ = z * World.SCALE_Z + (g - 128.0f);
                                posX = Math.max(Math.min(posX, 512.0f * World.SCALE_X), 0.0f);
                                posZ = Math.max(Math.min(posZ, 512.0f * World.SCALE_Z), 0.0f);
                                treePositions.add(new Vector3f(posX, World.getTerrainHeight(posX, posZ) - 0.5f, posZ));
                            }
                        }

                    }
                }
            }
            treeChunkZ++;
            if (treeChunkZ >= 32) {
                advanceLoading();
                wait(10);
            }
        } else if (loadingStage == 4) {
            loadingName = "Creating trees...";
            loadingProgress = (float) treeIndex / treePositions.size();
            int branches = (int) ((TREE_SIZE - 2.0f) * 4);
            if (!vertsInitialized) {
                vertices = new Vertex3f[(20 + (branches * 4 * 2)) * treePositions.size()]; // 4 Verts per face, 2 Faces per branch, 5 branches per size
                triangles = new int[(30 + (branches * 6 * 2)) * treePositions.size()];
                vertsInitialized = true;
            }
            for (int t = treeIndex; t < treeIndex + 128; t++) {
                if (t < treePositions.size()) {

                    int vertIndex = (20 + (branches * 4 * 2)) * t;
                    int triIndex = (30 + (branches * 6 * 2)) * t;

                    float xPos = treePositions.get(t).getX();
                    float yPos = treePositions.get(t).getY();
                    float zPos = treePositions.get(t).getZ();

                    Vector3f position = new Vector3f(xPos, yPos, zPos);

                    PlayerMovement.addCollisionRegion(new Region3f(Vector3f.subtract(position, new Vector3f(-TREE_WIDTH, -2, -TREE_WIDTH)), Vector3f.add(position, new Vector3f(TREE_WIDTH, TREE_SIZE + 2, TREE_WIDTH))));

                    // TODO: Fix Normals
                    Vertex3f[] treeVertices = new Vertex3f[]{
                            //Back face
                            new Vertex3f(new Vector3f(0.0f, TREE_SIZE, 0.0f).add(position), new Vector3f(0, 1,0), new Vector2f(0.0f, 0.0f)),
                            new Vertex3f(new Vector3f(-TREE_WIDTH, 0.0f, -TREE_WIDTH).add(position), new Vector3f(0, 1,0), new Vector2f(0.0f, 0.25f)),
                            new Vertex3f(new Vector3f(TREE_WIDTH, 0.0f, -TREE_WIDTH).add(position), new Vector3f(0, 1,0), new Vector2f(0.25f, 0.25f)),
                            new Vertex3f(new Vector3f(0.0f, TREE_SIZE, 0.0f).add(position), new Vector3f(0, 1,0), new Vector2f(0.25f, 0.0f)),

                            //Front face
                            new Vertex3f(new Vector3f(0.0f, TREE_SIZE, 0.0f).add(position), new Vector3f(0, 1,0), new Vector2f(0.0f, 0.0f)),
                            new Vertex3f(new Vector3f(-TREE_WIDTH, 0.0f, TREE_WIDTH).add(position), new Vector3f(0, 1,0), new Vector2f(0.0f, 0.25f)),
                            new Vertex3f(new Vector3f(TREE_WIDTH, 0.0f, TREE_WIDTH).add(position), new Vector3f(0, 1,0), new Vector2f(0.25f, 0.25f)),
                            new Vertex3f(new Vector3f(0.0f, TREE_SIZE, 0.0f).add(position), new Vector3f(0, 1,0), new Vector2f(0.25f, 0.0f)),

                            //Right face
                            new Vertex3f(new Vector3f(0.0f, TREE_SIZE, 0.0f).add(position), new Vector3f(0, 1,0), new Vector2f(0.0f, 0.0f)),
                            new Vertex3f(new Vector3f(TREE_WIDTH, 0.0f, -TREE_WIDTH).add(position), new Vector3f(0, 1,0), new Vector2f(0.0f, 0.25f)),
                            new Vertex3f(new Vector3f(TREE_WIDTH, 0.0f, TREE_WIDTH).add(position), new Vector3f(0, 1,0), new Vector2f(0.25f, 0.25f)),
                            new Vertex3f(new Vector3f(0.0f, TREE_SIZE, 0.0f).add(position), new Vector3f(0, 1,0), new Vector2f(0.25f, 0.0f)),

                            //Left face
                            new Vertex3f(new Vector3f(0.0f, TREE_SIZE, 0.0f).add(position), new Vector3f(0, 1,0), new Vector2f(0.0f, 0.0f)),
                            new Vertex3f(new Vector3f(-TREE_WIDTH, 0.0f, -TREE_WIDTH).add(position), new Vector3f(0, 1,0), new Vector2f(0.0f, 0.25f)),
                            new Vertex3f(new Vector3f(-TREE_WIDTH, 0.0f, TREE_WIDTH).add(position), new Vector3f(0, 1,0), new Vector2f(0.25f, 0.25f)),
                            new Vertex3f(new Vector3f(0.0f, TREE_SIZE, 0.0f).add(position), new Vector3f(0, 1,0), new Vector2f(0.25f, 0.0f)),

                            //Bottom face
                            new Vertex3f(new Vector3f(-TREE_WIDTH, 0.0f, TREE_WIDTH).add(position), new Vector3f(0, 1,0), new Vector2f(0.0f, 0.0f)),
                            new Vertex3f(new Vector3f(-TREE_WIDTH, 0.0f, -TREE_WIDTH).add(position), new Vector3f(0, 1,0), new Vector2f(0.0f, 0.25f)),
                            new Vertex3f(new Vector3f(TREE_WIDTH, 0.0f, -TREE_WIDTH).add(position), new Vector3f(0, 1,0), new Vector2f(0.25f, 0.25f)),
                            new Vertex3f(new Vector3f(TREE_WIDTH, 0.0f, TREE_WIDTH).add(position), new Vector3f(0, 1,0), new Vector2f(0.25f, 0.0f)),
                    };
                    int[] treeTriangles = new int[]{
                            //Back face
                            vertIndex + 0, vertIndex + 1, vertIndex + 3,
                            vertIndex + 3, vertIndex + 1, vertIndex + 2,

                            //Front face
                            vertIndex + 4, vertIndex + 5, vertIndex + 7,
                            vertIndex + 7, vertIndex + 5, vertIndex + 6,

                            //Right face
                            vertIndex + 8, vertIndex + 9, vertIndex + 11,
                            vertIndex + 11, vertIndex + 9, vertIndex + 10,

                            //Left face
                            vertIndex + 12, vertIndex + 13, vertIndex + 15,
                            vertIndex + 15, vertIndex + 13, vertIndex + 14,

                            //Bottom Face face
                            vertIndex + 16, vertIndex + 17, vertIndex + 19,
                            vertIndex + 19, vertIndex + 17, vertIndex + 18,
                    };
                    System.arraycopy(treeVertices, 0, vertices, vertIndex, 20);
                    System.arraycopy(treeTriangles, 0, triangles, triIndex, 30);
                    for (int i = 0; i < branches; i++) {
                        float turn = (i * (1 / 1.6180339887f)) + treeASeeds.get(t) * treeBSeeds.get(t);
                        float height = i * 0.25f + 2.0f;
                        float branchSize = 2 + ((float) (branches - i) / branches) * 9;
                        float halfBranchSize = branchSize / 2.0f;
                        float longBranchSize = (float) Math.sqrt(branchSize * branchSize + (branchSize / 2.0f) * (branchSize / 2.0f));

                        Vector3f c = new Vector3f(0.0f, height, 0.0f);
                        Vector3f f = new Vector3f((float) Math.sin(turn) * branchSize, height - 0.3f * branchSize, (float) Math.cos(turn) * branchSize);

                        Vector3f r = new Vector3f((float) Math.sin(turn + Math.PI / 2.0f) * halfBranchSize, height - 0.5f * branchSize, (float) Math.cos(turn + Math.PI / 2.0f) * halfBranchSize);
                        Vector3f fr = new Vector3f((float) Math.sin(turn + Math.PI / 6.0f) * longBranchSize, height - 0.7f * branchSize, (float) Math.cos(turn + Math.PI / 6.0f) * longBranchSize);

                        Vector3f l = new Vector3f((float) Math.sin(turn - Math.PI / 2.0f) * halfBranchSize, height - 0.5f * branchSize, (float) Math.cos(turn - Math.PI / 2.0f) * halfBranchSize);
                        Vector3f fl = new Vector3f((float) Math.sin(turn - Math.PI / 6.0f) * longBranchSize, height - 0.7f * branchSize, (float) Math.cos(turn - Math.PI / 6.0f) * longBranchSize);

                        vertices[vertIndex + 20 + i * 8] = new Vertex3f(Vector3f.add(position, f), new Vector3f(0, 1,0), new Vector2f(0.625f, 1.0f));
                        vertices[vertIndex + 20 + i * 8 + 1] = new Vertex3f(Vector3f.add(position, c), new Vector3f(0, 1,0), new Vector2f(0.625f, 0.0f));
                        vertices[vertIndex + 20 + i * 8 + 2] = new Vertex3f(Vector3f.add(position, r), new Vector3f(0, 1,0), new Vector2f(0.25f, 0.0f));
                        vertices[vertIndex + 20 + i * 8 + 3] = new Vertex3f(Vector3f.add(position, fr), new Vector3f(0, 1,0), new Vector2f(0.25f, 1.0f));

                        vertices[vertIndex + 20 + i * 8 + 4] = new Vertex3f(Vector3f.add(position, fl), new Vector3f(0, 1,0), new Vector2f(1.0f, 1.0f));
                        vertices[vertIndex + 20 + i * 8 + 5] = new Vertex3f(Vector3f.add(position, l), new Vector3f(0, 1,0), new Vector2f(1.0f, 0.0f));
                        vertices[vertIndex + 20 + i * 8 + 6] = new Vertex3f(Vector3f.add(position, c), new Vector3f(0, 1,0), new Vector2f(0.625f, 0.0f));
                        vertices[vertIndex + 20 + i * 8 + 7] = new Vertex3f(Vector3f.add(position, f), new Vector3f(0, 1,0), new Vector2f(0.625f, 1.0f));

                        triangles[triIndex + 30 + i * 12] = vertIndex + 20 + i * 8;
                        triangles[triIndex + 30 + i * 12 + 1] = vertIndex + 20 + i * 8 + 1;
                        triangles[triIndex + 30 + i * 12 + 2] = vertIndex + 20 + i * 8 + 3;
                        triangles[triIndex + 30 + i * 12 + 3] = vertIndex + 20 + i * 8 + 3;
                        triangles[triIndex + 30 + i * 12 + 4] = vertIndex + 20 + i * 8 + 1;
                        triangles[triIndex + 30 + i * 12 + 5] = vertIndex + 20 + i * 8 + 2;

                        triangles[triIndex + 30 + i * 12 + 6] = vertIndex + 20 + i * 8 + 4;
                        triangles[triIndex + 30 + i * 12 + 7] = vertIndex + 20 + i * 8 + 5;
                        triangles[triIndex + 30 + i * 12 + 8] = vertIndex + 20 + i * 8 + 7;
                        triangles[triIndex + 30 + i * 12 + 9] = vertIndex + 20 + i * 8 + 7;
                        triangles[triIndex + 30 + i * 12 + 10] = vertIndex + 20 + i * 8 + 5;
                        triangles[triIndex + 30 + i * 12 + 11] = vertIndex + 20 + i * 8 + 6;
                    }
                }
            }
            if (treeIndex + 128 < treePositions.size()) {
                treeIndex += 128;
            } else {
                Mesh3f mesh = new Mesh3f(vertices, triangles, new Material("/textures/tree.png"));
                World.objects.add(new GameObjectMesh(new Vector3f(0.0f, 0.0f, 0.0f), Vector3f.zero(), Vector3f.one(), mesh));
                advanceLoading();
            }
        } else if (loadingStage == 5) {
            loadingName = "Loading objects...";

            for (GameObject go : World.objects) {
                go.load();
            }

            if (Client.isConnected()) {
                System.out.println("[INFO] Sending loaded packet");
                Client.getSocketClient().sendData(new PacketLoaded().getByteData());

            }

            World.loading = false;
            World.loaded = true;

            System.out.println("[INFO] World Loaded!");

            advanceLoading();
        }
    }



    private void advanceLoading() {
        System.out.println("[INFO] Loading stage " + loadingStage + " was completed. (" + elapsedTime + " ms)");
        loadingStage ++;
        loadingProgress = 0;
    }

    private void beginOperation() {
        operationStart = System.currentTimeMillis();
    }

    private void finishOperation() {
        nextOperation += System.currentTimeMillis() - operationStart;
    }

    private void wait(int milliseconds) {
        nextOperation += milliseconds;
    }

    public String getLoadingName() {
        return loadingName;
    }

    public float getLoadingProgress() {
        return loadingProgress;
    }
}
