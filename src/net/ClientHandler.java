package net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import engine.io.Input;
import engine.io.Window;
import engine.math.Mathf;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.GameObject;
import game.Player;
import game.PlayerMovement;
import game.WorldState;
import game.scene.Scene;
import main.Global;
import main.Main;
import net.packets.Packet;
import net.packets.client.ChatRequestPacket;
import net.packets.client.ConnectPacket;
import net.packets.client.TeamSelectPacket;
import net.packets.client.UserInputPacket;
import net.packets.server.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientHandler {

    public static Client client;
    protected static int playerId;

    protected static boolean serverRegistered = false;
    protected static boolean hasRegisteredTeam = false;
    protected static int team;

    private static boolean hasSentConnectedPacket = false;

    private static final ConcurrentMap<Packet, Float> fakeLagPacketBuffer = new ConcurrentHashMap<>();
    protected static final boolean FAKE_LAG = true;
    protected static float FAKE_LAG_MS = 100;

    public static InputSender inputSender;
    private static Timer inputSenderTimer;

    private static WorldState previousState, currentState;
    private static long previousStamp, currentStamp;

    static int test = 0;

    public static void init() {
        Log.set(Log.LEVEL_DEBUG);

        client = new Client();
        client.start();

        Kryo kryo = client.getKryo();
        kryo.register(List.class);
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);

        kryo.register(Vector3f.class);
        kryo.register(Vector2f.class);

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

        kryo.register(WorldState.class);
        kryo.register(WorldStatePacket.class);

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
                    Packet newPacket = (Packet) packet;
                    if (FAKE_LAG) {
                        fakeLagPacketBuffer.put(newPacket, FAKE_LAG_MS / 1000.0f);
                    } else {
                        PacketInterpreter.interpret(newPacket);
                    }
                }
            }
        });
    }

    public static void update() {

        FAKE_LAG_MS = 100 + new Random().nextInt(10);

        if (FAKE_LAG) {
            for (Packet p : fakeLagPacketBuffer.keySet()) {
                float newTime = fakeLagPacketBuffer.get(p) - Main.getDeltaTime();
                if (newTime < 0) {
                    PacketInterpreter.interpret(p);
                    fakeLagPacketBuffer.remove(p);
                } else {
                    fakeLagPacketBuffer.put(p, newTime);
                }
            }
        }

        if (client.isConnected() && !hasSentConnectedPacket) {
            client.sendTCP(new ConnectPacket(Main.getUsername()));
            hasSentConnectedPacket = true;
            return;
        }
        if (client.isConnected() && !serverRegistered) {
            return;
        }
        if (client.isConnected() && !hasRegisteredTeam) {
            return;
        }

        if (previousState != null && currentState != null) {
            float difference = (float) (currentStamp - previousStamp);
            float interp = (float) (System.currentTimeMillis() - currentStamp) / difference;
            interp = Mathf.clamp(interp, 0, 1);
            for (Player prev : previousState.getPlayers()) {
                if (prev.getPlayerId() != ClientHandler.playerId) {
                    Player curr = null;
                    for (Player c : currentState.getPlayers()) {
                        if (prev.getPlayerId() == c.getPlayerId()) {
                            curr = c;
                        }
                    }

                    if (curr != null) {
                        GameObject playerObject = Scene.getPlayer(curr.getUuid());
                        if (playerObject != null) {
                            playerObject.setPosition(Vector3f.lerp(prev.getPosition(), curr.getPosition(), interp).minus(new Vector3f(0, 0.5f, 0)));
                            playerObject.setRotation(Vector3f.lerp(new Vector3f(0, -prev.getRotation().getY(), 0), new Vector3f(0, -curr.getRotation().getY(), 0), interp));
                        }
                    }
                }
            }
        }

        PlayerMovement.updateCamera();
    }

    public static void addWorldState(WorldState worldState) {
        if (currentState == null) {
            currentState = worldState;
            currentStamp = System.currentTimeMillis();
            return;
        }

        previousState = currentState;
        currentState = worldState;

        previousStamp = currentStamp;
        currentStamp = System.currentTimeMillis();
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

    public static void startInputSender(int tick) {
        inputSenderTimer = new Timer();
        inputSender = new InputSender(tick);
        inputSenderTimer.scheduleAtFixedRate(inputSender, 0, 2);
    }

    public static void stopInputSender() {
        if (inputSenderTimer != null) {
            inputSenderTimer.cancel();
            inputSenderTimer.purge();
            inputSenderTimer = null;
        }
    }


}
