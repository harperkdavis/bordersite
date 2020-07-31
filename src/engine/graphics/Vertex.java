package engine.graphics;

import engine.math.Vector3;

public class Vertex {

    private Vector3 position;

    public Vertex(Vector3 position) {
        this.position = position;
    }

    public Vector3 getPosition() {
        return position;
    }
}
