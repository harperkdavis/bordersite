package main;

import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.graphics.MeshBuilder;
import engine.math.Vector3f;
import engine.objects.GameObject;
import engine.math.Region3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {

    private Main main;

    public GameObject groundPlane = new GameObject(new Vector3f(0, 0, 0), Vector3f.zero(), new Vector3f(4, 4, 4), MeshBuilder.TiledPlane(400, new Material("/textures/grass.png")));

    private Material wallMaterial = new Material("/textures/stone.png");
    public Mesh wallMesh = MeshBuilder.Cube(1, wallMaterial);
    public GameObject wallObject = new GameObject(new Vector3f(10, 0, 10), Vector3f.zero(), new Vector3f(5, 5, 5), wallMesh);
    public Region3f wallRegion = new Region3f(new Vector3f(7.5f, 2.5f, 7.5f), new Vector3f( 12.5f, -2.5f, 12.5f));

    public GameObject test = new GameObject(Vector3f.zero(), Vector3f.zero(), Vector3f.one(), MeshBuilder.Cube(1, new Material("/textures/test.png")));

    private UserInterface ui;

    public List<Mesh> tallGrassMeshes = new ArrayList<>();
    public List<GameObject> grassObjects = new ArrayList<>();
    private final int GRASS_COUNT = 10;

    public World(Main main) {
        this.main = main;
        ui = new UserInterface(main);
    }

    public void load() {
        groundPlane.load();
        wallObject.load();
        test.load();
        ui.load();
        Random random = new Random();
        for (int i = 0; i < GRASS_COUNT; i++) {
            Mesh mesh = MeshBuilder.Plane(1f, new Material("/textures/tallgrass.png"));
            mesh.create();
            tallGrassMeshes.add(mesh);
            grassObjects.add(new GameObject(new Vector3f(random.nextFloat() * 40, 0.15f, random.nextFloat() * 40), new Vector3f(90, random.nextFloat() * 360,0),  new Vector3f(0.5f, 0.5f, 0.5f), mesh));
        }
    }

    public void update() {
        ui.update();
        test.position = wallRegion.closestPoint(main.playerMovement.getPosition());
    }

    public void render() {
        main.renderer.renderMesh(groundPlane, main.camera);
        main.renderer.renderMesh(wallObject, main.camera);
        main.renderer.renderMesh(test, main.camera);
        for (GameObject object : grassObjects) {
            main.renderer.renderMesh(object, main.camera);
        }
        ui.render(main.uirenderer);
    }

    public void unload() {
        groundPlane.unload();
        wallObject.unload();
        test.unload();
        ui.unload();
        for (Mesh mesh : tallGrassMeshes) {
            mesh.destroy();
        }
    }

}
