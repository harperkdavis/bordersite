package net;

import com.google.gson.Gson;
import main.Main;
import net.packets.Packet;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.*;

public class Client extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private Main game;

    public boolean running = true;

    private boolean connected = false;
    private int playerId = 0;

    public Client(Main game, String ipAddress) {
        this.game = game;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException e) {
            System.err.println("[ERROR] Socket Exception: " + e);
        } catch (UnknownHostException e) {
            System.err.println("[ERROR] Unknown Host Exception: " + e);
        }

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
            case PLAYERDATA:
                playerData(jsonData);
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
}
