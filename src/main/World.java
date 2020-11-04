package main;

import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.math.Vector3f;
import engine.objects.GameObject;

public class World {

    private Main main;

    public Mesh groundMesh = Mesh.TiledPlane(400, new Material("/textures/grass.png"));
    public Mesh tallGrassMesh = Mesh.Plane(1f, new Material("/textures/tallgrass.png"));
    public GameObject groundPlane = new GameObject(new Vector3f(0, 0, 0), Vector3f.zero(), new Vector3f(4, 4, 4), groundMesh);
    public GameObject tallGrass = new GameObject(new Vector3f(0, 2, 0), Vector3f.zero(),  new Vector3f(0.2f, 0.2f, 0.2f), tallGrassMesh);

    public World(Main main) {
        this.main = main;
    }

    public void load() {
        groundMesh.create();
        tallGrassMesh.create();
    }

    public void update() {

    }

    public void render() {
        main.renderer.renderMesh(groundPlane, main.camera);
        main.renderer.renderMesh(tallGrass, main.camera);
    }

    public void unload() {
        groundMesh.destroy();
        tallGrassMesh.destroy();
    }

}
