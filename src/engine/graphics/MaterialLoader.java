package engine.graphics;

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

    public static void loadMapMaterials(String path) {

    }

    public static void unloadMapMaterials() {

    }

    public static void unloadAll() {
        for (Material m : Material.materials) {
            m.destroy();
        }
    }

}
