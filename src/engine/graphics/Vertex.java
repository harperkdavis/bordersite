package engine.graphics;

import engine.math.Vector2;
import engine.math.Vector3;

public class Vertex {

    private Vector3 position, color;
    private Vector2 uv;

    public Vertex(Vector3 position, Vector3 color, Vector2 uv) {
        this.position = position;
        this.color = color;
        this.uv = uv;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getColor() {
        return color;
    }

    public Vector2 getUV() {
        return uv;
    }
}
