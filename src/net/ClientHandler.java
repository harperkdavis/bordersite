package net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import game.Player;
import main.Main;
import net.packets.Packet;
import net.packets.client.ChatRequestPacket;
import net.packets.client.ConnectPacket;
import net.packets.client.TeamSelectPacket;
import net.packets.client.UserInputPacket;
import net.packets.server.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ClientHandler {

    public static Client client;
    protected static int playerId;

    protected static boolean serverRegistered = false;
    protected static boolean hasRegisteredTeam = false;
    protected static int team;

    private static boolean hasSentConnectedPacket = false;

    private static ScheduledExecutorService fakeLag;
    private static final int FAKE_LAG_MS = 100;

    public static SynchronizedInputSender inputSender;
    public static Thread inputSenderThread;

    public static void init() {
        Log.set(Log.LEVEL_DEBUG);

        client = new Client();
        client.start();

        Kryo kryo = client.getKryo();
        kryo.register(List.class);
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);

        kryo.register(Player.class);

        kryo.register(Packet.class);
        kryo.register(ConnectPacket.class);
        kryo.register(ConnectionReceivedPacket.class);

        kryo.register(ChatRequestPacket.class);
        kryo.register(ChatPacket.class);

        kryo.register(TeamSelectPacket.class);
        kryo.register(PlayerRemovePacket.class);
        kryo.register(PlayerSpawnPacket.class);
        kryo.register(WorldSpawnPacket.class);

        kryo.register(InputSnapshot.class);
        kryo.register(UserInputPacket.class);

        fakeLag = Executors.newSingleThreadScheduledExecutor();

        inputSender = new SynchronizedInputSender();
        inputSenderThread = new Thread(inputSender);

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

    public static boolean hasRegisteredTeam() {
        return hasRegisteredTeam;
    }

}
