package engine.math;

public class Vector2 {

    public float x, y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

}
