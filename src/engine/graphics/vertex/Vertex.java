package engine.graphics.vertex;

import engine.math.Vector2;
import engine.math.Vector3;

public class Vertex {

    protected Vector3 position;
    protected Vector2 uv;
    private Vector3 normal, tangent = Vector3.zero(), bitangent = Vector3.zero();

    public Vertex(Vector3 position, Vector2 uv) {
        this.position = position;
        this.normal = new Vector3(0, 0, 0);
        this.uv = uv;
    }

    public Vertex(Vector3 position, Vector3 normal, Vector2 uv) {
        this.position = position;
        this.normal = normal;
        this.uv = uv;
    }

    public Vector3 getNormal() {
        return normal;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector2 getUV() {
        return uv;
    }

    public Vector3 getTangent() {
        return tangent;
    }

    public void setTangent(Vector3 tangent) {
        this.tangent = tangent;
    }

    public Vector3 getBitangent() {
        return bitangent;
    }

    public void setBitangent(Vector3 bitangent) {
        this.bitangent = bitangent;
    }

}
