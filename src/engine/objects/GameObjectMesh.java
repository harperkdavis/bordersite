package engine.objects;

import engine.graphics.mesh.Mesh2f;
import engine.graphics.mesh.Mesh3f;
import engine.math.Vector3f;
import engine.math.Vector4f;

public class GameObjectMesh extends GameObject {

    private Mesh3f mesh;
    private Vector4f color;

    public GameObjectMesh(Vector3f position, Vector3f rotation, Vector3f scale, Mesh3f mesh) {
        super(position, rotation, scale);
        this.mesh = mesh;
        this.color = new Vector4f(1, 1, 1, 1);
    }

    public GameObjectMesh(Vector3f position, Vector3f rotation, Vector3f scale, Mesh3f mesh, Vector4f color) {
        super(position, rotation, scale);
        this.mesh = mesh;
        this.color = color;
    }

    public void load() {
        mesh.create();
    }

    public void setMesh(Mesh3f mesh) {
        this.mesh.updateMesh(mesh);
    }

    public void setMeshAndMaterial(Mesh3f mesh) {
        this.mesh.destroy();
        this.mesh = mesh;
        mesh.create();
    }

    public void unload() {
        mesh.destroy();
    }

    public Mesh3f getMesh() {
        return mesh;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }
}
