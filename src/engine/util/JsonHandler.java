package engine.util;

import engine.math.Vector3f;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class JsonHandler {

    public static float getAsFloat(JSONObject object, String path) {
        return ((Double) object.get(path)).floatValue();
    }

    public static double getAsDouble(JSONObject object, String path) {
        return (Double) object.get(path);
    }

    public static int getAsInt(JSONObject object, String path) {
        return ((Double) object.get(path)).intValue();
    }

    @SuppressWarnings("unchecked")
    public static List<String> getAsStringList(JSONObject object, String path) {
        return (List<String>) object.get(path);
    }

    @SuppressWarnings("unchecked")
    public static List<Integer> getAsIntList(JSONObject object, String path) {
        return (List<Integer>) object.get(path);
    }
    @SuppressWarnings("unchecked")
    public static Vector3f getVector3f(Map<String, ?> map, String path) {
        Map<String, Double> vectorMap = ((Map<String, Map<String, Double>>) map).get(path);
        return new Vector3f(vectorMap.get("x").floatValue(), vectorMap.get("y").floatValue(), vectorMap.get("z").floatValue());
    }
    public static Vector3f getFlipVector3f(Map<String, ?> map, String path) {
        Map<String, Double> vectorMap = ((Map<String, Map<String, Double>>) map).get(path);
        return new Vector3f(-vectorMap.get("x").floatValue(), vectorMap.get("y").floatValue(), -vectorMap.get("z").floatValue());
    }
}
