package engine.graphics;

import engine.math.Vector2f;
import engine.math.Vector3f;

public class Vertex {

    private Vector3f position, color;
    private Vector2f uv;

    public Vertex(Vector3f position, Vector2f uv) {
        this.position = position;
        this.color = new Vector3f(0, 0, 0);
        this.uv = uv;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColor() {
        return color;
    }

    public Vector2f getUV() {
        return uv;
    }
}
