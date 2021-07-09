package net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import game.ui.InGameMenu;
import net.packets.Packet;
import net.packets.server.ChatPacket;
import net.packets.server.ConnectionReceivedPacket;

public class PacketInterpreter {

    public static void interpret(Connection connection, Packet packet) {

        if (packet instanceof ConnectionReceivedPacket) {
            ConnectionReceivedPacket crp = (ConnectionReceivedPacket) packet;
            ClientHandler.serverRegistered = true;
            ClientHandler.playerId = crp.getPlayerId();
            System.out.println("[INFO] Connected to server! Player Id: " + crp.getPlayerId());
        } else if (packet instanceof ChatPacket) {
            handleChatPacket(connection, (ChatPacket) packet);
        }

    }

    private static void handleChatPacket(Connection connection, ChatPacket packet) {
        InGameMenu.displayChatMessage(packet.getMessage(), packet.getRed(), packet.getGreen(), packet.getBlue());
    }
}
