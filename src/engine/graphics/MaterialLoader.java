package engine.graphics;

import com.google.gson.Gson;
import engine.util.JsonHandler;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class MaterialLoader {

    private static int materialIndex = 0;
    private static boolean finished = false;

    public static void initLoading() {

        for (int i = 0; i < 4; i++) {
            Material.materials.get(i).create();
        }

        materialIndex = 4;
    }

    public static void loadNext() {
        System.out.println("[INFO] Loading Material: " + Material.materials.get(materialIndex).getDiffusePath());

        Material.materials.get(materialIndex).create();
        materialIndex++;

        if (materialIndex == Material.materials.size()) {
            finished = true;
        }

    }

    public static Material getCurrentMaterial() {
        return Material.materials.get(materialIndex - 1);
    }

    public static float getProgress() {
        return (float) materialIndex / Material.materials.size();
    }

    public static boolean isFinished() {
        return finished;
    }

    @SuppressWarnings("unchecked")
    public static void loadMapMaterials(String mapName) throws IOException {
        Material.mapMaterials.clear();

        Material mapIcon = new Material("/maps/" + mapName + "/map-icon.png");
        mapIcon.create();
        Material.mapMaterials.put("icon", mapIcon);

        Gson gson = new Gson();

        Reader reader = Files.newBufferedReader(Paths.get("resources/maps/" + mapName + "/materials.json"));
        Map<?, ?> map = gson.fromJson(reader, Map.class);

        Map<String, ArrayList<String>> materials = (Map<String, ArrayList<String>>) map.get("materials");
        String prefix = "/maps/" + mapName + "/";
        for (String key : materials.keySet()) {
            ArrayList<String> textures = materials.get(key);
            Material material = Material.DEFAULT;
            if (textures.size() != 0) {
                if (textures.size() <= 2) {
                    material = new Material(prefix + textures.get(0));
                } else {
                    material = new Material(prefix + textures.get(0), prefix + textures.get(1),prefix + textures.get(2));
                }
            }
            Material.mapMaterials.put(key, material);
            material.create();
        }
    }

    public static Material loadMapSkyboxMaterial(String mapName, String path) {
        String matPath = "/maps/" + mapName + "/" + path;
        Material material = new Material(matPath);
        material.create();
        return material;
    }

    public static void unloadMapMaterials() {
        for (Material m : Material.mapMaterials.values()) {
            m.destroy();
        }
        Material.mapMaterials.clear();
    }

    public static void unloadAll() {
        for (Material m : Material.materials) {
            m.destroy();
        }
    }

}
