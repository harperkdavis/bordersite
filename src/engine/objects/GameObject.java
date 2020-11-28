package engine.objects;

import java.util.ArrayList;
import java.util.List;
import engine.graphics.Mesh;
import engine.math.Vector3f;
import org.lwjgl.system.CallbackI;

public class GameObject {

    public Vector3f position, rotation, scale;
    public Mesh mesh;

    public GameObject(Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh) {
        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.scale = new Vector3f(scale);
        this.mesh = mesh;
    }

    public void load() {
        mesh.create();
    }

    public void unload() {
        mesh.destroy();
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public Vector3f getRotation() {
        return new Vector3f(rotation);
    }

    public Vector3f getScale() {
        return new Vector3f(scale);
    }

    public Mesh getMesh() {
        return mesh;
    }
}
