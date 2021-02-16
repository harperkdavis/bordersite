package engine.graphics;

import engine.math.Vector2f;
import engine.math.Vector3f;

public class Vertex {

    private Vector3f position, color, normal;
    private Vector2f uv;

    public Vertex(Vector3f position, Vector3f normal, Vector2f uv) {
        this.position = position;
        this.color = new Vector3f(1, 1, 1);
        this.normal = normal;
        this.uv = uv;
    }

    public Vertex(Vector3f position, Vector3f normal, Vector2f uv, Vector3f color) {
        this.position = position;
        this.color = color;
        this.normal = normal;
        this.uv = uv;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColor() {
        return color;
    }

    public Vector3f getNormal() { return normal; }

    public Vector2f getUV() {
        return uv;
    }
}
