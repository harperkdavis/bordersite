package engine.math;

public class Mathf {

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public static float clamp(float x, float min, float max) {
        return Math.max(Math.min(x, max), min);
    }

    public static boolean ccw(float ax, float ay, float bx, float by, float cx, float cy) {
        return (cy-ay) * (bx-ax) > (by-ay) * (cx-ax);
    }

    public static boolean intersect(float ax, float ay, float bx, float by, float cx, float cy, float dx, float dy) {
        return ccw(ax, ay, cx, cy, dx, dy) != ccw(bx, by, cx, cy, dx, dy) && ccw(ax, ay,bx, by, cx, cy) != ccw(ax, ay,bx, by, dx, dy);
    }
}
