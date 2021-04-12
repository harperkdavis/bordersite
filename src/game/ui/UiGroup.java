package game.ui;

import engine.graphics.mesh.Mesh2f;
import engine.math.Vector3f;
import engine.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public class UiGroup extends GameObject {

    private List<UiObject> objects = new ArrayList<>();

    public UiGroup(Vector3f position, Vector3f rotation, Vector3f scale, UiObject object) {
        super(position, rotation, scale);
        this.objects.add(object);
    }

    public UiGroup(Vector3f position, Vector3f rotation, Vector3f scale, List<UiObject> objects) {
        super(position, rotation, scale);
        this.objects = objects;
    }

    @Override
    public void load() {
        for (UiObject object : objects) {
            object.load();
        }
    }

    public List<UiObject> getObjects() {
        return objects;
    }

    @Override
    public void unload() {
        for (UiObject object : objects) {
            object.unload();
        }
    }
}
