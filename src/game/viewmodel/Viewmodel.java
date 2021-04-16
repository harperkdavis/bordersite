package game.viewmodel;

import engine.graphics.Material;
import engine.graphics.mesh.MeshBuilder;
import engine.graphics.render.Renderer;
import engine.io.MeshLoader;
import engine.math.Vector3f;
import engine.objects.GameObject;
import engine.objects.GameObjectMesh;
import game.GamePlane;
import main.Main;

import java.util.ArrayList;
import java.util.List;

public class Viewmodel implements GamePlane {

    protected static Viewmodel viewmodel;
    public static List<GameObject> objects = new ArrayList<>();

    private static final Vector3f FORWARDS = new Vector3f(0, -90, 0);

    private static GameObject gunObject;
    private static GameObject armObject;

    public Viewmodel() {
        // gunObject = addObject(new GameObjectMesh(new Vector3f(0, 0, -1.5f), new Vector3f(45, 0, 0), Vector3f.one(), MeshLoader.loadModel("/models/weapon-knife.obj", new Material("/textures/weapons/knife-nebula.png"))));
    }

    @Override
    public void load() {
        for (GameObject go : objects) {
            go.load();
        }
    }

    @Override
    public void fixedUpdate() {

    }

    @Override
    public void update() {
        // gunObject.setRotation(Vector3f.lerpdt(gunObject.getRotation(), new Vector3f(45, (float) Main.getElapsedTime() / 20.0f, 0), 0.1f));
    }

    @Override
    public void render() {
        for (GameObject go : objects) {
            Renderer.getViewmodel().render(go);
        }
    }

    @Override
    public void unload() {
        for (GameObject go : objects) {
            go.unload();
        }
    }

    public GameObject addObject(GameObject object) {
        return addObject(object, false);
    }

    public GameObject addObject(GameObject object, boolean load) {
        objects.add(object);
        if (load) {
            object.load();
        }
        return object;
    }

    public void removeObject(GameObject object) {
        objects.remove(object);
        object.unload();
    }

    public static Viewmodel getViewmodel() {
        return viewmodel;
    }

    public static void setViewmodel(Viewmodel viewmodel) {
        Viewmodel.viewmodel = viewmodel;
    }
}
