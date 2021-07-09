package net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import main.Main;
import net.packets.Packet;
import net.packets.client.ChatRequestPacket;
import net.packets.client.ConnectPacket;
import net.packets.server.ChatPacket;
import net.packets.server.ConnectionReceivedPacket;

import java.io.IOException;

public class ClientHandler {

    public static Client client;
    protected static int playerId;
    protected static boolean serverRegistered = false;
    private static boolean hasSentConnectedPacket = false;

    public static void init() {
        Log.set(Log.LEVEL_DEBUG);

        client = new Client();
        client.start();

        Kryo kryo = client.getKryo();
        kryo.register(Packet.class);
        kryo.register(ConnectPacket.class);
        kryo.register(ConnectionReceivedPacket.class);

        kryo.register(ChatRequestPacket.class);
        kryo.register(ChatPacket.class);

    }

    public static void connect(String ip) {
        try {
            client.connect(5000, ip, 27555, 27777);
        } catch(IOException e) {
            e.printStackTrace();
        }

        client.addListener(new Listener() {
            public void received(Connection connection, Object packet) {
                if (packet instanceof Packet) {
                    PacketInterpreter.interpret(connection, (Packet) packet);
                }
            }
        });
    }

    public static void fixedUpdate() {

        if (client.isConnected() && !hasSentConnectedPacket) {
            client.sendTCP(new ConnectPacket(Main.getUsername()));
            hasSentConnectedPacket = true;
            return;
        }
        if (client.isConnected() && !serverRegistered) {
            return;
        }
    }

    public static int getPlayerId() {
        return playerId;
    }

    public static void sendChatPacket(String message) {
        client.sendTCP(new ChatRequestPacket(message));
    }

}
