package engine.objects;

import engine.graphics.Mesh;
import engine.math.Vector3f;

public class GameObjectMesh extends GameObject {

    private Mesh mesh;

    public GameObjectMesh(Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh) {
        super(position, rotation, scale);
        this.mesh = mesh;
    }

    public void load() {
        mesh.create();
    }

    public void setMesh(Mesh mesh) {
        this.mesh.destroy();
        this.mesh = mesh;
        this.mesh.create();
    }

    public void unload() {
        mesh.destroy();
    }

    public Mesh getMesh() {
        return mesh;
    }

}
