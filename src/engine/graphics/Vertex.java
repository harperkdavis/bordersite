package engine.graphics;

import engine.math.Vector2f;
import engine.math.Vector3f;

public class Vertex {

    private Vector3f position, color;
    private Vector2f uv;

    public Vertex(Vector3f position, Vector3f color, Vector2f uv) {
        this.position = position;
        this.color = color;
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
