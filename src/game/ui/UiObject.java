package game.ui;

import engine.graphics.mesh.Mesh2f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import engine.objects.GameObject;

public class UiObject extends GameObject {

    private Mesh2f mesh;
    private Vector4f color;

    public UiObject(Vector3f position, Vector3f rotation, Vector3f scale, Mesh2f mesh) {
        super(position, rotation, scale);
        this.mesh = mesh;
        this.color = new Vector4f(1, 1, 1, 1);
    }

    public UiObject(Vector3f position, Vector3f rotation, Vector3f scale, Mesh2f mesh, Vector4f color) {
        super(position, rotation, scale);
        this.mesh = mesh;
        this.color = color;
    }

    @Override
    public void load() {
        mesh.create();
    }

    public void setMesh(Mesh2f mesh) {
        this.mesh.updateMesh(mesh);
    }

    public void setMeshAndMaterial(Mesh2f mesh) {
        this.mesh.destroy();
        this.mesh = mesh;
        mesh.create();
    }

    @Override
    public void unload() {
        mesh.destroy();
    }

    public Mesh2f getMesh() {
        return mesh;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

}
