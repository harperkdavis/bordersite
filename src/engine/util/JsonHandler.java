package engine.util;

import org.json.simple.JSONObject;

import java.util.List;

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

    public static List<String> getAsStringList(JSONObject object, String path) {
        return (List<String>) object.get(path);
    }

    public static List<Integer> getAsIntList(JSONObject object, String path) {
        return (List<Integer>) object.get(path);
    }
}
