package engine.graphics.vertex;

import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.math.Vector4f;

public class Vertex2f extends Vertex {

    public Vertex2f(Vector2f position, Vector2f uv) {
        this.position = new Vector3f(position, 0);
        this.uv = uv;
    }

    public Vertex2f(Vector3f position, Vector2f uv) {
        this.position = position;
        this.uv = uv;
    }

}
