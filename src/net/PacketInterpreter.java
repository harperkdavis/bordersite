package net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import engine.objects.camera.Camera;
import game.Player;
import game.PlayerMovement;
import game.scene.Scene;
import game.ui.InGameMenu;
import net.packets.Packet;
import net.packets.client.TeamSelectPacket;
import net.packets.server.ChatPacket;
import net.packets.server.ConnectionReceivedPacket;
import net.packets.server.PlayerSpawnPacket;
import net.packets.server.WorldSpawnPacket;
import java.util.List;

public class PacketInterpreter {

    public static void interpret(Connection connection, Packet packet) {

        System.out.println("[PACKET] Incoming packet with type: " + packet.getPacketType());
        packet.printData();

        if (packet instanceof ConnectionReceivedPacket) {
            ConnectionReceivedPacket crp = (ConnectionReceivedPacket) packet;
            ClientHandler.serverRegistered = true;
            ClientHandler.playerId = crp.getPlayerId();
            Camera.setActiveCamera(Scene.getOrbitCamera());
            System.out.println("[INFO] Connected to server! Player Id: " + crp.getPlayerId());
        } else if (packet instanceof ChatPacket) {
            handleChatPacket(connection, (ChatPacket) packet);
        } else if (packet instanceof WorldSpawnPacket) {
            handleWorldSpawnPacket(connection, (WorldSpawnPacket) packet);
        } else if (packet instanceof PlayerSpawnPacket) {
            handlePlayerSpawnPacket(connection, (PlayerSpawnPacket) packet);
        }

    }

    private static void handleChatPacket(Connection connection, ChatPacket packet) {
        InGameMenu.displayChatMessage(packet.getMessage(), packet.getRed(), packet.getGreen(), packet.getBlue());
    }

    private static void handleWorldSpawnPacket(Connection connection, WorldSpawnPacket packet) {
        ClientHandler.hasRegisteredTeam = true;
        ClientHandler.team = packet.getTeam();
        Camera.setActiveCamera(PlayerMovement.getCamera());
        List<Player> players = packet.getPlayerList();
        // TODO synchronize tick
        ClientHandler.inputSenderThread.start();
        for (Player p : players) {
            Scene.addBufferedPlayer(p.getUuid());
        }
    }

    private static void handlePlayerSpawnPacket(Connection connection, PlayerSpawnPacket packet) {
        Scene.addBufferedPlayer(packet.getPlayerUUID());
    }
}
