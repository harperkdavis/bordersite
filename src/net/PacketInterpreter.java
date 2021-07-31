package net;

import engine.math.Vector3f;
import engine.objects.camera.Camera;
import game.Player;
import game.PlayerMovement;
import game.scene.Scene;
import game.ui.InGameMenu;
import net.packets.Packet;
import net.packets.server.*;

import java.util.List;

public class PacketInterpreter {

    public static void interpret(Packet packet) {

        if (packet instanceof ConnectionReceivedPacket) {
            System.out.println("[PACKET] Incoming packet with type: " + packet.getPacketType());
            packet.printData();
            ConnectionReceivedPacket crp = (ConnectionReceivedPacket) packet;
            ClientHandler.serverRegistered = true;
            ClientHandler.playerId = crp.getPlayerId();
            Camera.setActiveCamera(Scene.getOrbitCamera());
            System.out.println("[INFO] Connected to server! Player Id: " + crp.getPlayerId());
        } else if (packet instanceof ChatPacket) {
            System.out.println("[PACKET] Incoming packet with type: " + packet.getPacketType());
            packet.printData();
            handleChatPacket((ChatPacket) packet);
        } else if (packet instanceof WorldSpawnPacket) {
            System.out.println("[PACKET] Incoming packet with type: " + packet.getPacketType());
            packet.printData();
            handleWorldSpawnPacket((WorldSpawnPacket) packet);
        } else if (packet instanceof PlayerSpawnPacket) {
            System.out.println("[PACKET] Incoming packet with type: " + packet.getPacketType());
            packet.printData();
            handlePlayerSpawnPacket((PlayerSpawnPacket) packet);
        } else if (packet instanceof PlayerRemovePacket) {
            System.out.println("[PACKET] Incoming packet with type: " + packet.getPacketType());
            packet.printData();
            handlePlayerRemovePacket((PlayerRemovePacket) packet);
        } else if (packet instanceof WorldStatePacket) {
            handleWorldStatePacket((WorldStatePacket) packet);
        }

    }

    private static void handleChatPacket(ChatPacket packet) {
        InGameMenu.displayChatMessage(packet.getMessage(), packet.getRed(), packet.getGreen(), packet.getBlue());
    }

    private static void handleWorldSpawnPacket(WorldSpawnPacket packet) {
        ClientHandler.hasRegisteredTeam = true;
        ClientHandler.team = packet.getTeam();
        Camera.setActiveCamera(PlayerMovement.getCamera());
        List<Player> players = packet.getPlayerList();
        for (Player p : players) {
            if (p.getPlayerId() != ClientHandler.getPlayerId()) {
                addPlayer(p.getUuid());
            }
        }

        long difference = System.currentTimeMillis() - packet.getTimestamp();
        int ticksAdd = (int) difference / 10;
        ClientHandler.startInputSender(packet.getTick() + ticksAdd);
    }

    private static void handlePlayerSpawnPacket(PlayerSpawnPacket packet) {
        addPlayer(packet.getPlayerUUID());
    }

    private static void handlePlayerRemovePacket(PlayerRemovePacket packet) {
        Scene.removePlayer(packet.getPlayerUUID());
    }

    private static void handleWorldStatePacket(WorldStatePacket packet) {
        for (Player p : packet.getWorldState().getPlayers()) {
            if (p.getPlayerId() == ClientHandler.playerId) {

                Vector3f start = PlayerMovement.getPosition().copy();
                PlayerMovement.applyPlayer(p);

                Vector3f tempRotation = PlayerMovement.getCameraRotation();
                int startSequence = p.getInputSequence();

                int i = 0;
                while (!(i >= 512 || InputSender.getPendingInputs()[i] == null)) {
                    InputState current = InputSender.getPendingInputs()[i];
                    if (current.getSequence() <= startSequence) {
                        InputSender.removeRecentPending();
                        continue;
                    }
                    PlayerMovement.setCameraRotation(current.getRotation());
                    PlayerMovement.applyMovement(current.getPrevInputs(), current.getInputs());
                    i++;
                }

                Vector3f correction = start.minus(PlayerMovement.getPosition());
                PlayerMovement.getCorrectionOffset().add(correction);

                PlayerMovement.setCameraRotation(tempRotation);
            }
        }
        ClientHandler.addWorldState(packet.getWorldState());
    }

    private static void addPlayer(String uuid) {
        Scene.addBufferedPlayer(uuid);
    }
}
