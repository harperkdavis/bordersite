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
import engine.objects.Player;

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
                    Vector3f position = new Vector3f(positionArray.get(0).floatValue(), positionArray.get(1).floatValue(), positionArray.get(2).floatValue()).normalize();
                    List<Double> pointColorArray = light.get("color");
                    Vector3f color = new Vector3f(pointColorArray.get(0).floatValue(), pointColorArray.get(1).floatValue(), pointColorArray.get(2).floatValue());

                    System.out.println(position + " | " + color);
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
