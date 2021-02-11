package net;

import engine.objects.Camera;
import main.Main;
import main.PlayerMovement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DataSender implements Runnable {

    private Client client;
    private PlayerMovement player;
    private Thread thread;
    private long lastTime;
    private boolean stopped;

    public DataSender(Client client, PlayerMovement player) {
        this.client = client;
        this.player = player;
        lastTime = System.currentTimeMillis();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while(!stopped && client.isConnected()) {
            if (System.currentTimeMillis() - lastTime >= 10) {
                JSONObject packet = packetData();
                client.sendJsonData(packet);
            }
        }
        System.out.println("[INFO] Data Sender stopped");
    }

    private JSONObject packetData() {
        JSONObject data = new JSONObject();

        data.put("id", 3);

        data.put("position.x", player.getPosition().getX());
        data.put("position.y", player.getPosition().getY());
        data.put("position.z", player.getPosition().getZ());

        data.put("rotation.x", Camera.getMainCamera().getRotation().getX());
        data.put("rotation.y", Camera.getMainCamera().getRotation().getY());
        data.put("rotation.z", Camera.getMainCamera().getRotation().getZ());

        return data;
    }

    public void stop() {
        stopped = true;
    }
}
