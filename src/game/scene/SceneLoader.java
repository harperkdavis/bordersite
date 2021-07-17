package game.scene;

import com.google.gson.Gson;
import engine.audio.AudioBuffer;
import engine.audio.AudioMaster;
import engine.audio.AudioSource;
import engine.audio.SoundEffect;
import engine.components.BlockComponent;
import engine.components.RampComponent;
import engine.graphics.Material;
import engine.graphics.MaterialLoader;
import engine.graphics.light.DirectionalLight;
import engine.graphics.light.PointLight;
import engine.graphics.mesh.Mesh;
import engine.graphics.mesh.MeshBuilder;
import engine.graphics.vertex.Vertex;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.GameObject;

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

    public static final String MAP = "atnitz";

    SceneLoader() {
        beginTime = System.currentTimeMillis();
        loadingName = "Loading scene...";
    }

    @SuppressWarnings("unchecked")
    public void run() {
        elapsedTime = System.currentTimeMillis() - beginTime;
        if (elapsedTime < nextOperation) {
            return;
        }

        if (loadingStage == 0) {

            loadingName = "Loading map materials...";

            try {
                MaterialLoader.loadMapMaterials(MAP);
            } catch (IOException e) {
                e.printStackTrace();
            }

            advanceLoading();
            wait(5);
        } else if (loadingStage == 1) {

            loadingName = "Loading map layout...";

            Gson gson = new Gson();
            Map<?, ?> map = new HashMap<>();
            try {
                Reader reader = Files.newBufferedReader(Paths.get("resources/maps/" + MAP + "/layout.json"));
                map = gson.fromJson(reader, Map.class);
            } catch(IOException e) {
                e.printStackTrace();
            }

            try {
                List<Map<String, ?>> components = (List<Map<String, ?>>) map.get("components");
                for (Map<String, ?> component : components) {
                    String type = (String) component.get("type");
                    String materialName = (String) component.get("material");
                    Material material = Material.getMapMaterial(materialName);
                    ArrayList<Double> doubleVal = (ArrayList<Double>) component.get("values");
                    ArrayList<Float> val = new ArrayList<>();
                    for (Double d : doubleVal) {
                        val.add(d.floatValue());
                    }
                    if (type.equals("block")) {

                        Vector3f a = new Vector3f(val.get(0), val.get(1), val.get(2));
                        Vector3f b = new Vector3f(val.get(3), val.get(4), val.get(5));
                        Vector3f c = new Vector3f(val.get(6), val.get(7), val.get(8));

                        float height = val.get(9), tiling = val.get(10);
                        boolean mesh = (val.get(11) == 1), collision = (val.get(12) == 1);

                        BlockComponent newComponent = new BlockComponent(a, b, c, height, material, tiling, mesh, collision);
                        Scene.addComponent(newComponent);

                    } else if (type.equals("ramp")) {

                        Vector3f a = new Vector3f(val.get(0), val.get(1), val.get(2));
                        Vector3f b = new Vector3f(val.get(3), val.get(4), val.get(5));
                        Vector3f c = new Vector3f(val.get(6), val.get(7), val.get(8));

                        float height = val.get(9), tiling = val.get(10);
                        boolean mesh = (val.get(11) == 1), collision = (val.get(12) == 1);
                        int direction = val.get(13).intValue();

                        RampComponent newComponent = new RampComponent(a, b, c, height, direction, material, tiling, mesh, collision);
                        Scene.addComponent(newComponent);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Scene.addObject(new GameObject(new Vector3f(-11, 0, 6), Tree(12, 0, 0)));
            Scene.addObject(new GameObject(new Vector3f(11, 0, 6), Tree(12, 20, 10)));
            Scene.addObject(new GameObject(new Vector3f(-11, 0, -6), Tree(12, 40, 20)));
            Scene.addObject(new GameObject(new Vector3f(11, 0, -6), Tree(12, 60, 30)));

            advanceLoading();
            wait(5);
        } else if (loadingStage == 2) {

            loadingName = "Loading map lighting...";

            Gson gson = new Gson();
            Map<?, ?> map = new HashMap<>();
            try {
                Reader reader = Files.newBufferedReader(Paths.get("resources/maps/" + MAP + "/lighting.json"));
                map = gson.fromJson(reader, Map.class);
            } catch(IOException e) {
                e.printStackTrace();
            }

            try {
                Map<String, ?> dirLight = (Map<String, ?>) map.get("directional_light");
                List<Double> directionArray = (List<Double>) dirLight.get("direction");
                Vector3f lightDir = new Vector3f(directionArray.get(0).floatValue(), directionArray.get(1).floatValue(), directionArray.get(2).floatValue());
                List<Double> colorArray = (List<Double>) dirLight.get("color");
                Vector3f lightColor = new Vector3f(colorArray.get(0).floatValue(), colorArray.get(1).floatValue(), colorArray.get(2).floatValue());

                Scene.directionalLight = new DirectionalLight(lightColor, lightDir, ((Double) dirLight.get("intensity")).floatValue());

                for (int i = 0; i < Scene.MAX_POINT_LIGHTS; i++) {
                    Scene.pointLights[i] = PointLight.IDENTITY;
                }
                List<Map<String, List<Double>>> pointLights = (List<Map<String, List<Double>>>) map.get("lights");

                for (int i = 0; i < pointLights.size(); i++) {
                    Map<String, List<Double>> light = pointLights.get(i);
                    List<Double> positionArray = light.get("position");
                    Vector3f position = new Vector3f(positionArray.get(0).floatValue(), positionArray.get(1).floatValue(), positionArray.get(2).floatValue());
                    List<Double> pointColorArray = light.get("color");
                    Vector3f color = new Vector3f(pointColorArray.get(0).floatValue(), pointColorArray.get(1).floatValue(), pointColorArray.get(2).floatValue());

                    Scene.pointLights[i] = new PointLight(position, color);
                }

                String skyboxMat = (String) map.get("skybox");
                Scene.skybox = new GameObject(Vector3f.zero(), MeshBuilder.Skybox(1000, MaterialLoader.loadMapSkyboxMaterial(MAP, skyboxMat)));

            } catch (Exception e) {
                e.printStackTrace();
            }

            Scene.loading = false;
            Scene.loaded = true;

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
            float branchSize = 0.5f + ((float) (branches - i) / branches) * 4;
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
