package engine.graphics.vertex;

import engine.math.Vector2f;
import engine.math.Vector3f;

public abstract class Vertex {

    protected Vector3f position;
    protected Vector2f uv;

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getUV() {
        return uv;
    }

}
