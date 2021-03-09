package net;

import java.util.ArrayList;
import java.util.List;

import engine.io.Input;
import engine.objects.Camera;
import game.PlayerMovement;
import org.json.simple.JSONObject;
import org.lwjgl.glfw.GLFW;
import org.lwjglx.Sys;

public class DataSender {

    private Client client;
    private PlayerMovement player;

    public DataSender(Client client, PlayerMovement player) {
        this.client = client;
        this.player = player;
        System.out.println("[INFO] Data sender initialized");
    }

    public void sendData() {
        JSONObject packet = packetData();
        client.sendJsonData(packet);
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

        // KEYS

        List<String> keys = new ArrayList<>();
        List<String> keysDown = new ArrayList<>();
        List<String> keysUp = new ArrayList<>();

        for (int i = 0; i < GLFW.GLFW_KEY_LAST; i++) {
            char c = (char) i;
            if (Input.isKey(i)) {
                keys.add(Character.toString(c));
            }
            if (Input.isKeyDown(i)) {
                keysDown.add(Character.toString(c));
            }
            if (Input.isKeyUp(i)) {
                keysUp.add(Character.toString(c));
            }
        }

        data.put("keys", keys);
        data.put("keys.down", keysDown);
        data.put("keys.up", keysUp);

        // MOUSE

        List<Integer> mouse = new ArrayList<>();
        List<Integer> mouseDown = new ArrayList<>();
        List<Integer> mouseUp = new ArrayList<>();

        for (int i = 0; i < GLFW.GLFW_MOUSE_BUTTON_LAST; i++) {
            if (Input.isKey(i)) {
                mouse.add(i);
            }
            if (Input.isKeyDown(i)) {
                mouseDown.add(i);
            }
            if (Input.isKeyUp(i)) {
                mouseUp.add(i);
            }
        }

        data.put("keys", keys);
        data.put("keys.down", keysDown);
        data.put("keys.up", keysUp);

        data.put("mouse", mouse);
        data.put("mouse.down", mouseDown);
        data.put("mouse.up", mouseUp);

        return data;
    }

}
