package net;

import com.google.gson.Gson;

import engine.objects.Player;
import main.Main;
import main.PlayerMovement;
import net.packets.Packet;
import org.json.simple.JSONObject;

import java.util.List;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Client implements Runnable {

    private static Client socketClient;

    private Thread thread;
    private InetAddress ipAddress;
    private DatagramSocket socket;
    private DataSender sender;

    private List<Player> localPlayers = new ArrayList<>();

    public boolean running = true;

    private boolean connected = false;
    private int playerId = 0;

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
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
        sender.start();
    }

    public void stop() {
        sender.stop();
        running = false;
    }

    public void run() {
        System.out.println("[INFO] Client thread started");
        do {
            byte[] data = new byte[4096];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                System.err.println("[ERROR] Error receiving packet: " + e);
            }
            parsePacket(packet.getData());
        } while (running);
        System.out.println("[INFO] Client thread stopped");
    }

    private void parsePacket(byte[] data) {
        String message = new String(data).trim();
        JSONObject jsonData = new Gson().fromJson(message, JSONObject.class);
        Packet.PacketType type = Packet.lookupPacket(((Double) jsonData.get("id")).intValue());

        switch (type) {
            case INVALID:
                System.err.println("[ERROR] Invalid packet! " + message);
                break;
            case CONNECT:
                playerConnect(jsonData);
            case PUBLICPLAYERDATA:
                playerData(jsonData);
                break;
            case PLAYERSPAWN:
                playerSpawn(jsonData);
                break;
        }
    }

    private void playerConnect(JSONObject data) {

        if (connected) {
            return;
        }

        playerId = ((Double) data.get("playerId")).intValue();
        connected = true;
        System.out.println("[INFO] Connected to server! Player ID " + playerId);
    }

    private void playerData(JSONObject data) {

    }

    private void playerSpawn(JSONObject data) {

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

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isConnected() {
        return connected;
    }

    public int getPlayerId() {
        return playerId;
    }

    public static Client getSocketClient() {
        return socketClient;
    }

    public static void setSocketClient(Client client) {
        socketClient = client;
    }
}
