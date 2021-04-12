package engine.graphics.vertex;

import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.math.Vector4f;

public class Vertex3f extends Vertex {

    private Vector3f normal;

    public Vertex3f(Vector3f position, Vector3f normal, Vector2f uv) {
        this.position = position;
        this.normal = normal;
        this.uv = uv;
    }

    public Vector3f getNormal() {
        return normal;
    }
}
