package engine.graphics.vertex;

import engine.math.Vector2f;
import engine.math.Vector3f;

public class Vertex {

    protected Vector3f position;
    protected Vector2f uv;
    private Vector3f normal, tangent = Vector3f.zero(), bitangent = Vector3f.zero();

    public Vertex(Vector3f position, Vector2f uv) {
        this.position = position;
        this.normal = new Vector3f(0, 0, 0);
        this.uv = uv;
    }

    public Vertex(Vector3f position, Vector3f normal, Vector2f uv) {
        this.position = position;
        this.normal = normal;
        this.uv = uv;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getUV() {
        return uv;
    }

    public Vector3f getTangent() {
        return tangent;
    }

    public void setTangent(Vector3f tangent) {
        this.tangent = tangent;
    }

    public Vector3f getBitangent() {
        return bitangent;
    }

    public void setBitangent(Vector3f bitangent) {
        this.bitangent = bitangent;
    }

}
