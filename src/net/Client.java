package net;

import engine.math.ByteUtil;
import main.Bordersite;
import net.packets.Packet;
import net.packets.PacketLogin;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class Client extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private Bordersite game;
    private boolean running = true;

    private boolean connected = false;
    private int playerId = 0;

    public Client(Bordersite game, String ipAddress) {
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
        while (!game.window.shouldClose()) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                System.err.println("[ERROR] Error receiving packet: " + e);
            }
            parsePacket(packet.getData());
        }
    }

    private void parsePacket(byte[] data) {
        String message = new String(data).trim();
        Packet.PacketType type = Packet.lookupPacket(message.substring(0, 2));

        switch (type) {
            case INVALID:
                System.err.println("[ERROR] Invalid packet!");
                break;
            case CONNECT:
                playerConnect(data);
            case PLAYERDATA:
                playerData(data);
                break;
        }
    }

    private void playerConnect(byte[] data) {

        if (!connected) {
            return;
        }

        int playerId = ByteUtil.byteToInt(new byte[]{0, 0, data[2], data[3]});
        this.playerId = playerId;
        connected = true;
        System.out.println("[INFO] Connected to server!");
    }

    private void playerData(byte[] data) {
        int playerId = ByteUtil.byteToInt(new byte[]{0, 0, data[2], data[3]});
        int posX = ByteUtil.byteToInt(new byte[]{data[4], data[5], data[6], data[7]});
        int posY = ByteUtil.byteToInt(new byte[]{data[8], data[9], data[10], data[11]});
        int posZ = ByteUtil.byteToInt(new byte[]{data[12], data[13], data[14], data[15]});
        System.out.println("PLAYER ID: " + playerId + "; POS X: " + posX);
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 7777);
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("[ERROR] Error sending packet: " + e);
        }
    }

}
