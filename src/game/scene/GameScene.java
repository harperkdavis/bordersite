package game.scene;

import engine.graphics.Material;
import engine.graphics.mesh.MeshBuilder;
import engine.io.Input;
import engine.io.MeshLoader;
import engine.math.Vector3f;
import engine.objects.GameObject;
import engine.objects.camera.Camera;
import engine.objects.camera.OrbitCamera;
import main.Main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameScene extends Scene {

    public Map<String, GameObject> playerObjects = new HashMap<>();
    private final ConcurrentLinkedQueue<GameScene.PlayerData> playerAddBuffer = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> playerRemoveBuffer = new ConcurrentLinkedQueue<>();

    private OrbitCamera orbitCamera;
    private GameObject gunObject;
    private GameObject gunMuzzleFlash;

    public GameScene(String map) {
        loader = new SceneLoader(map);
        orbitCamera = new OrbitCamera(Vector3f.zero(), new Vector3f(0, 10, 0), 80.0f);
    }

    @Override
    public void load() {

        gunObject = new GameObject(new Vector3f(2.8f, -1.65f, -2.9f), MeshLoader.loadModel("acp_9.obj", Material.WPN_ACP_9_DEFAULT));
        gunMuzzleFlash = new GameObject(new Vector3f(2.8f, -1.65f, -2.9f), MeshBuilder.Cross4(2.0f, Material.WPN_MUZZLE_FLASH));

        loading = true;
        loaded = false;
    }

    @Override
    public void update() {


        for (int i = 0; i < playerAddBuffer.size(); i++) {
            PlayerData player = playerAddBuffer.poll();
            GameObject object = new GameObject(new Vector3f(0, 0, 0), MeshLoader.loadModel("player.obj", player.team == 0 ? Material.PLAYER_MODEL_RED : Material.PLAYER_MODEL_BLUE));
            addObject(object);
            playerObjects.put(player.uuid, object);
        }

        for (int i = 0; i < playerRemoveBuffer.size(); i++) {
            String uuid = playerRemoveBuffer.poll();
            GameObject object = playerObjects.get(uuid);
            if (object != null) {
                removeObject(object);
                playerObjects.remove(uuid);
            }
        }

        orbitCamera.setPosition(new Vector3f((float) Math.cos(Main.getElapsedTime() / 10000.0f) * 10.0f, 15,(float) Math.sin(Main.getElapsedTime() / 10000.0f) * 10.0f));
        skybox.setPosition(Camera.getActiveCameraPosition());
    }

    private static class PlayerData {

        public String uuid;
        public int team;

        public PlayerData(String uuid, int team) {
            this.uuid = uuid;
            this.team = team;
        }

    }

    public GameObject getGunObject() {
        return gunObject;
    }

    public GameObject getGunMuzzleFlash() {
        return gunMuzzleFlash;
    }

    public void setGunPosition(Vector3f position) {
        gunObject.setPosition(position);
        gunMuzzleFlash.setPosition(position.plus(0, 0.5f, -2.5f));
    }

    public Map<String, GameObject> getPlayerObjects() {
        return playerObjects;
    }

    public void addBufferedPlayer(String uuid, int team) {
        if (!playerObjects.containsKey(uuid)) {
            playerAddBuffer.add(new PlayerData(uuid, team));
        }
    }

    public void removePlayer(String uuid) {
        playerRemoveBuffer.add(uuid);
    }

    public GameObject getPlayer(String uuid) {
        System.out.println(Arrays.toString(playerObjects.keySet().toArray()));
        return playerObjects.get(uuid);
    }

    public OrbitCamera getOrbitCamera() {
        return orbitCamera;
    }
}
