package net;

import com.google.gson.Gson;

import engine.math.Vector3f;
import engine.objects.GameObject;
import engine.objects.Player;
import engine.util.JsonHandler;
import game.PlayerMovement;
import game.scene.Scene;
import net.packets.Packet;
import org.json.simple.JSONObject;

import java.util.List;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Client implements Runnable {

    private static Client socketClient;
    private static Thread clientThread;
    private static boolean connected = false;

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private final DataSender sender;

    private final List<Player> localPlayers = new ArrayList<>();

    private static boolean running = true;

    private String playerId = "null";

    public Client(String ipAddress) {
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException e) {
            System.err.println("[ERROR] Socket Exception: " + e);
        } catch (UnknownHostException e) {
            System.err.println("[ERROR] Unknown Host Exception: " + e);
        }
        sender = new DataSender(this, PlayerMovement.getPlayerMovement());
        socket.connect(this.ipAddress, 7777);
    }

    public void start() {
        clientThread = new Thread(this);
        clientThread.start();
    }

    public void run() {
        System.out.println("[INFO] Client thread started");
        do {
            byte[] data = new byte[65536];
            DatagramPacket packet = new DatagramPacket(data, data.length);

            try {
                socket.receive(packet);
            } catch (Exception e) {
                System.err.println("[ERROR] Error receiving packet: " + e);
            }
            parsePacket(packet.getData());
        } while (running);
        System.out.println("[INFO] Client thread stopped");
    }

    private void parsePacket(byte[] data) {
        String message = new String(data).trim();
        JSONObject jsonData;
        Packet.PacketType type;
        try {
            jsonData = new Gson().fromJson(message, JSONObject.class);
            if (jsonData == null) {
                return;
            }
            type = Packet.lookupPacket(((Double) jsonData.get("id")).intValue());
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        switch (type) {
            case INVALID -> System.err.println("[ERROR] Invalid packet! " + message);
            case CONNECT -> playerConnect(jsonData);
            case PUBLICPLAYERDATA -> playerData(jsonData);
            case PLAYERSPAWN -> playerSpawn(jsonData);
        }
    }

    private void playerConnect(JSONObject data) {

        if (connected) {
            return;
        }

        playerId = ((String) data.get("playerId"));
        connected = true;
        System.out.println("[INFO] Connected to server! Player ID " + playerId);
    }

    private void playerData(JSONObject data) {
        String id = (String) data.get("playerId");
        GameObject playerObject = Scene.getPlayerObjects().get(id);
        if (true) {
            Vector3f position = new Vector3f(JsonHandler.getAsFloat(data, "position.x"), JsonHandler.getAsFloat(data, "position.y"), JsonHandler.getAsFloat(data, "position.z"));
            Vector3f rotation = new Vector3f(JsonHandler.getAsFloat(data, "rotation.x"), JsonHandler.getAsFloat(data, "rotation.y"), JsonHandler.getAsFloat(data, "rotation.z"));
        }
    }

    private void playerSpawn(JSONObject data) {
        System.out.println(data.toString());
    }

    public void sendJsonData(JSONObject data) {
        sendData(data.toJSONString().getBytes());
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 7777);
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("[ERROR] Error sending packet: " + e);
        }
    }

    public static Thread getClientThread() {
        return clientThread;
    }

    public static void setRunning(boolean running) {
        Client.running = running;
    }

    public DataSender getSender() {
        return sender;
    }

    public static boolean isConnected() {
        return connected;
    }

    public String getPlayerId() {
        return playerId;
    }

    public static Client getSocketClient() {
        return socketClient;
    }

    public static void setSocketClient(Client client) {
        socketClient = client;
    }

    public static void sendPacket(Packet packet) {
        getSocketClient().sendJsonData(packet.getJsonData());
    }

    public static void sendData(JSONObject jsonData) {
        getSocketClient().sendJsonData(jsonData);
    }
}
