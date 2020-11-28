package engine.math;

public class Mathf {

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public static float clamp(float x, float min, float max) {
        return Math.max(Math.min(x, max), min);
    }
}
