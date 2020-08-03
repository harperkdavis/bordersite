package engine.math;

public class Vector2f {

    public float x, y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector3f other) {
        this.x = other.getX();
        this.y = other.getY();
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

}
