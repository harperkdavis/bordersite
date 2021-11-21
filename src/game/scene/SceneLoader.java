package game.scene;

import com.google.gson.Gson;
import engine.components.BlockComponent;
import engine.components.RampComponent;
import engine.graphics.Material;
import engine.graphics.MaterialLoader;
import engine.graphics.light.DirectionalLight;
import engine.graphics.light.PointLight;
import engine.graphics.mesh.Mesh;
import engine.graphics.mesh.MeshBuilder;
import engine.graphics.vertex.Vertex;
import engine.io.MeshLoader;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.GameObject;
import engine.util.JsonHandler;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneLoader {

    private int loadingStage = 0;

    private final long beginTime;
    private long elapsedTime;

    private int nextOperation;
    private long operationStart;

    private String loadingName;
    private float loadingProgress;

    public static final String MAP = "sakrev";

    private Map<String, String> models;

    public SceneLoader(String map) {
        beginTime = System.currentTimeMillis();
        loadingName = "Loading scene...";

        models = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void run() {
        elapsedTime = System.currentTimeMillis() - beginTime;
        if (elapsedTime < nextOperation) {
            return;
        }

        if (loadingStage == 0) {

            loadingName = "Loading map materials and models...";

            try {
                MaterialLoader.loadMapMaterials(MAP);

                models.clear();

                Gson gson = new Gson();

                Reader reader = Files.newBufferedReader(Paths.get("resources/maps/" + MAP + "/map.json"));
                Map<?, ?> map = gson.fromJson(reader, Map.class);

                ArrayList<Map<String, String>> models = ((Map<String, ArrayList<Map<String, String>>>) map.get("assets")).get("models");
                String prefix = "/maps/" + MAP + "/";
                for (Map<String, String> mod : models) {
                    this.models.put(mod.get("name"), prefix + mod.get("path"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            advanceLoading();
            wait(5);
        } else if (loadingStage == 1) {

            loadingName = "Loading map...";

            Gson gson = new Gson();
            Map<?, ?> map = new HashMap<>();
            try {
                Reader reader = Files.newBufferedReader(Paths.get("resources/maps/" + MAP + "/map.json"));
                map = gson.fromJson(reader, Map.class);
            } catch(IOException e) {
                e.printStackTrace();
            }

            try {
                // Layout

                List<Map<String, ?>> components = ((Map<String, List<Map<String, ?>>>) map.get("layout")).get("components");
                for (Map<String, ?> component : components) {

                    String type = (String) component.get("type");
                    String materialName = (String) component.get("material");
                    boolean visible = (Boolean) component.get("visible");
                    Material material;
                    if (visible) {
                        material = Material.getMapMaterial(materialName);
                    } else {
                        material = Material.DEFAULT;
                    }


                    if (type.equals("block") || type.equals("ramp")) {

                        Map<String, ?> colliderData = (Map<String, ?>) component.get("colliderData");

                        Vector3f a = JsonHandler.getVector3f(colliderData, "a");
                        Vector3f b = JsonHandler.getVector3f(colliderData, "b");
                        Vector3f c = JsonHandler.getVector3f(colliderData, "c");
                        float height = ((Double) colliderData.get("height")).floatValue(), tiling = ((Double) colliderData.get("tiling")).floatValue();
                        boolean collidable = (Boolean) colliderData.get("collidable");

                        if (type.equals("block")) {

                            BlockComponent newComponent = new BlockComponent(a, b, c, height, material, tiling, visible, collidable);
                            newComponent.getObject().setVisible(visible);
                            Scene.getGameScene().addComponent(newComponent);

                        } else {

                            int direction = ((Double) colliderData.get("direction")).intValue();

                            RampComponent newComponent = new RampComponent(a, b, c, height, direction, material, tiling, visible, collidable);
                            newComponent.getObject().setVisible(visible);
                            Scene.getGameScene().addComponent(newComponent);

                        }
                    } else if (type.equals("tree")) {
                        Map<String, ?> treeData = (Map<String, ?>) component.get("treeData");

                        Vector3f position = JsonHandler.getVector3f(treeData, "position");

                        float size = ((Double) treeData.get("size")).floatValue(), seedA = ((Double) treeData.get("seedA")).floatValue(), seedB = ((Double) treeData.get("seedB")).floatValue();

                        GameObject newObject = new GameObject(position, Tree(size, seedA, seedB));
                        newObject.setVisible(visible);
                        Scene.getGameScene().addObject(newObject);
                    } else if (type.equals("model")) {
                        Map<String, ?> modelData = (Map<String, ?>) component.get("modelData");

                        Vector3f position = JsonHandler.getVector3f(modelData, "position");
                        Vector3f rotation = JsonHandler.getVector3f(modelData, "rotation");
                        Vector3f scale = JsonHandler.getVector3f(modelData, "scale");

                        String modelPath = models.get(modelData.get("model"));

                        GameObject newObject = new GameObject(position, rotation, scale, MeshLoader.loadModelRoot(modelPath, material));
                        newObject.setVisible(visible);
                        Scene.getGameScene().addObject(newObject);
                    }
                }

                Map<String, ?> lighting = ((Map<String, List<Map<String, ?>>>) map.get("lighting"));

                Map<String, ?> dirLightData = (Map<String, ?>) lighting.get("directionalLight");

                Vector3f dirColor = JsonHandler.getVector3f(dirLightData, "color");
                Vector3f dirDirection = JsonHandler.getVector3f(dirLightData, "direction").times(-1);
                float dirIntensity = ((Double) dirLightData.get("intensity")).floatValue();

                Scene.getGameScene().directionalLight = new DirectionalLight(dirColor, dirDirection, dirIntensity);

                for (int i = 0; i < Scene.MAX_POINT_LIGHTS; i++) {
                    Scene.getGameScene().pointLights[i] = PointLight.IDENTITY;
                }
                List<Map<String, ?>> lights = (List<Map<String, ?>>) lighting.get("lights");

                for (int i = 0; i < lights.size(); i++) {
                    Vector3f position = JsonHandler.getVector3f(lights.get(i), "position");
                    Vector3f color = JsonHandler.getVector3f(lights.get(i), "color");

                    PointLight pointLight = new PointLight(position, color);
                    Scene.getGameScene().pointLights[i] = pointLight;
                }

                String skyboxMat = (String) lighting.get("skybox");
                Scene.getGameScene().skybox = new GameObject(Vector3f.zero(), MeshBuilder.Skybox(1000, MaterialLoader.loadMapSkyboxMaterial(MAP, skyboxMat)));

            } catch (Exception e) {
                e.printStackTrace();
            }

            Scene.getGameScene().loading = false;
            Scene.getGameScene().loaded = true;

            advanceLoading();

        }
    }

    public static Mesh Tree(float size, float seedA, float seedB) {

        Vertex[] treeVertices = new Vertex[] {
                //Back face
                new Vertex(new Vector3f(0.0f, size, 0.0f), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f, 0.0f, -0.5f), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.25f)),
                new Vertex(new Vector3f( 0.5f, 0.0f, -0.5f), new Vector3f(0, 0, -1), new Vector2f(0.25f, 0.25f)),
                new Vertex(new Vector3f( 0.0f, size, 0.0f), new Vector3f(0, 0, -1), new Vector2f(0.25f, 0.0f)),

                //Front face
                new Vertex(new Vector3f(0.0f, size,  0.0f), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f, 0.0f,  0.5f), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.25f)),
                new Vertex(new Vector3f( 0.5f, 0.0f,  0.5f), new Vector3f(0, 0, 1), new Vector2f(0.25f, 0.25f)),
                new Vertex(new Vector3f( 0.0f, size,  0.0f), new Vector3f(0, 0, 1), new Vector2f(0.25f, 0.0f)),

                //Right face
                new Vertex(new Vector3f( 0.0f, size, 0.0f), new Vector3f(1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f( 0.5f, 0.0f, -0.5f), new Vector3f(1, 0, 0), new Vector2f(0.0f, 0.25f)),
                new Vertex(new Vector3f( 0.5f, 0.0f,  0.5f), new Vector3f(1, 0, 0), new Vector2f(0.25f, 0.25f)),
                new Vertex(new Vector3f( 0.0f, size,  0.0f), new Vector3f(1, 0, 0), new Vector2f(0.25f, 0.0f)),

                //Left face
                new Vertex(new Vector3f(0.0f, size, 0.0f), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f, 0.0f, -0.5f), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 0.25f)),
                new Vertex(new Vector3f(-0.5f, 0.0f, 0.5f), new Vector3f(-1, 0, 0), new Vector2f(0.25f, 0.25f)),
                new Vertex(new Vector3f(0.0f, size,  0.0f), new Vector3f(-1, 0, 0), new Vector2f(0.25f, 0.0f)),

                //Bottom face
                new Vertex(new Vector3f(-0.5f, 0.0f, 0.5f), new Vector3f(0, -1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f, 0.0f, -0.5f), new Vector3f(0, -1, 0), new Vector2f(0.0f, 0.25f)),
                new Vertex(new Vector3f( 0.5f, 0.0f, -0.5f), new Vector3f(0, -1, 0), new Vector2f(0.25f, 0.25f)),
                new Vertex(new Vector3f( 0.5f, 0.0f, 0.5f), new Vector3f(0, -1, 0), new Vector2f(0.25f, 0.0f)),
        };
        int[] treeTriangles = new int[] {
                //Back face
                0, 1, 3,
                3, 1, 2,

                //Front face
                4, 5, 7,
                7, 5, 6,

                //Right face
                8, 9, 11,
                11, 9, 10,

                //Left face
                12, 13, 15,
                15, 13, 14,

                //Bottom Face face
                16, 17, 19,
                19, 17, 18,
        };
        int branches = (int) ((size - 2.0f) * 5);
        Vertex[] vertices = new Vertex[20 + (branches * 4 * 2)]; // 4 Verts per face, 2 Faces per branch, 5 branches per size
        int[] triangles = new int[30 + (branches * 6 * 2)];
        System.arraycopy(treeVertices, 0, vertices, 0, 20);
        System.arraycopy(treeTriangles, 0, triangles, 0, 30);
        for (int i = 0; i < branches; i++) {
            float turn = (i * 0.9266462599f + i) + seedA * seedB;
            float height = i * 0.2f + 2.0f;
            float branchSize = 0.5f + ((float) (branches - i) / branches) * 6;
            float halfBranchSize = branchSize / 2.0f;
            float longBranchSize = (float) Math.sqrt(branchSize * branchSize + (branchSize / 2.0f) * (branchSize / 2.0f));

            Vector3f c = new Vector3f(0.0f, height, 0.0f);
            Vector3f f = new Vector3f((float) Math.sin(turn) * branchSize, height - 0.25f, (float) Math.cos(turn) * branchSize);

            Vector3f r = new Vector3f((float) Math.sin(turn + Math.PI / 2.0f) * halfBranchSize, height - 0.5f, (float) Math.cos(turn + Math.PI / 2.0f) * halfBranchSize);
            Vector3f fr = new Vector3f((float) Math.sin(turn + Math.PI / 6.0f) * longBranchSize, height - 0.75f, (float) Math.cos(turn + Math.PI / 6.0f) * longBranchSize);

            Vector3f l = new Vector3f((float) Math.sin(turn - Math.PI / 2.0f) * halfBranchSize, height - 0.5f, (float) Math.cos(turn - Math.PI / 2.0f) * halfBranchSize);
            Vector3f fl = new Vector3f((float) Math.sin(turn - Math.PI / 6.0f) * longBranchSize, height - 0.75f, (float) Math.cos(turn - Math.PI / 6.0f) * longBranchSize);

            vertices[20 + i * 8    ] = new Vertex(f, new Vector3f(1, 0, 0), new Vector2f(0.625f, 1.0f));
            vertices[20 + i * 8 + 1] = new Vertex(c, new Vector3f(1, 0, 0), new Vector2f(0.625f, 0.0f));
            vertices[20 + i * 8 + 2] = new Vertex(r, new Vector3f(1, 0, 0), new Vector2f(0.25f, 0.0f));
            vertices[20 + i * 8 + 3] = new Vertex(fr, new Vector3f(1, 0, 0), new Vector2f(0.25f, 1.0f));

            vertices[20 + i * 8 + 4] = new Vertex(fl, new Vector3f(-1, 0, 0), new Vector2f(1.0f, 1.0f));
            vertices[20 + i * 8 + 5] = new Vertex(l, new Vector3f(-1, 0, 0), new Vector2f(1.0f, 0.0f));
            vertices[20 + i * 8 + 6] = new Vertex(c, new Vector3f(-1, 0, 0), new Vector2f(0.625f, 0.0f));
            vertices[20 + i * 8 + 7] = new Vertex(f, new Vector3f(-1, 0, 0), new Vector2f(0.625f, 1.0f));

            triangles[30 + i * 12] = 20 + i * 8;
            triangles[30 + i * 12 + 1] = 20 + i * 8 + 1;
            triangles[30 + i * 12 + 2] = 20 + i * 8 + 3;
            triangles[30 + i * 12 + 3] = 20 + i * 8 + 3;
            triangles[30 + i * 12 + 4] = 20 + i * 8 + 1;
            triangles[30 + i * 12 + 5] = 20 + i * 8 + 2;

            triangles[30 + i * 12 + 6] = 20 + i * 8 + 4;
            triangles[30 + i * 12 + 7] = 20 + i * 8 + 5;
            triangles[30 + i * 12 + 8] = 20 + i * 8 + 7;
            triangles[30 + i * 12 + 9] = 20 + i * 8 + 7;
            triangles[30 + i * 12 + 10] = 20 + i * 8 + 5;
            triangles[30 + i * 12 + 11] = 20 + i * 8 + 6;
        }
        return new Mesh(vertices, triangles, Material.ENV_TREE);
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
