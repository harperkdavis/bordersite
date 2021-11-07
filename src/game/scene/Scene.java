package game.scene;

import engine.components.BlockComponent;
import engine.components.Component;
import engine.components.RampComponent;
import engine.graphics.Material;
import engine.graphics.light.DirectionalLight;
import engine.graphics.light.PointLight;
import engine.graphics.mesh.MeshBuilder;
import engine.io.Input;
import engine.io.MeshLoader;
import engine.math.*;
import engine.objects.camera.Camera;
import engine.objects.GameObject;
import engine.objects.camera.OrbitCamera;
import game.PlayerMovement;
import main.Main;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Scene {

    protected static Scene scene;
    protected static MenuScene menuScene;
    protected static GameScene gameScene;

    protected boolean loaded = false;
    protected boolean loading = false;

    public static final int MAX_POINT_LIGHTS = 128;

    public boolean shadowsEnabled = true;
    protected DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0.8f, 1.0f, 0.4f).normalize(), 1.0f);
    protected PointLight[] pointLights = new PointLight[MAX_POINT_LIGHTS];

    protected final List<GameObject> objects = new ArrayList<>();
    protected GameObject skybox;

    protected SceneLoader loader;

    public static void init() {
        menuScene = new MenuScene();
        menuScene.loadScene();
        scene = menuScene;
    }

    public void loadScene() {
        loading = true;
        loaded = false;

        load();
    }

    protected abstract void load();

    public void updateScene() {
        if (loading && loader != null) {
            loader.run();
            return;
        } else {
            loading = false;
            loaded = true;
        }
        update();
    }

    public abstract void update();

    public void unload() {
        for (GameObject go : objects) {
                go.unload();
        }
    }

    public void addComponent(Component c) {
        addObject(c.getObject());
        PlayerMovement.addCollisionComponent(c);
    }

    public GameObject addObject(GameObject object) {
        objects.add(object);
        return object;
    }

    public void removeObject(GameObject object) {
        objects.remove(object);
        object.unload();
    }

    public static void createGameScene() {
        gameScene = new GameScene("atnitz");
        gameScene.loadScene();
    }

    public static GameScene getGameScene() {
        return gameScene;
    }

    public static boolean isGameSceneLoaded() {
        if (gameScene != null) {
            return gameScene.isLoaded();
        }
        return false;
    }

    public SceneLoader getLoader() {
        return loader;
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public static Scene getActiveScene() {
        return scene;
    }

    public static void setActiveScene(Scene scene) {
        Scene.scene = scene;
    }

    public GameObject getSkybox() {
        return skybox;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public PointLight[] getPointLights() {
        return pointLights;
    }

    public List<GameObject> getObjects() {
        return objects;
    }
}
