package game.scene;

import engine.audio.AudioMaster;
import engine.audio.SoundEffect;
import engine.graphics.Material;
import engine.graphics.MaterialLoader;
import engine.graphics.mesh.MeshBuilder;
import engine.math.Mathf;
import engine.math.Vector3f;
import engine.math.Vector4f;
import engine.objects.GameObject;
import engine.objects.camera.Camera;
import engine.objects.camera.PerspectiveCamera;
import main.Main;

public class MenuScene extends Scene {

    private static PerspectiveCamera camera;


    public MenuScene() {

        AudioMaster.playSound(SoundEffect.BACKGROUND);

        shadowsEnabled = false;

        addObject(new GameObject(new Vector3f(-500, 0, -500), Vector3f.zero(), Vector3f.one().multiply(4), MeshBuilder.TiledPlane(250, Material.ENV_GRASS)));

        for (int i = 0; i < 100; i++) {
            float x = (float) Math.random() * 80 - 40, z = (float) - Math.random() * 40;
            if (Mathf.distance(x, z, 0, 0) > 5) {
                addObject(new GameObject(new Vector3f(x, 0, z), SceneLoader.Tree((float) Math.random() * 4 + 10f, (float) Math.random() * 128f, (float) Math.random() * 128f)));
            }
        }

        addObject(new GameObject(new Vector3f(0, 5, -50), Vector3f.zero(), new Vector3f(1000, 12, 2), MeshBuilder.Cube(1, Material.ENV_HEIGHTMAP)));


        skybox = new GameObject(Vector3f.zero(), MeshBuilder.Skybox(1000, Material.SKYBOX_CLOUDY));

        camera = new PerspectiveCamera(new Vector3f(0, 12, 0), Vector3f.zero(), 90);

        Camera.setActiveCamera(camera);
    }

    public static void mainMenu() {
        Scene.setActiveScene(Scene.menuScene);
        Camera.setActiveCamera(camera);
    }


    @Override
    protected void load() {

    }

    @Override
    public void update() {
        camera.setRotation(new Vector3f((float) Math.sin(Main.getElapsedTime() * 0.00004f) * 10 - 20, (float) Math.sin(Main.getElapsedTime() * 0.0001f) * 20, (float) Math.sin(Main.getElapsedTime() * 0.00001f) * 4));
    }
}
