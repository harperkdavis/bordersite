package net;

import engine.audio.AudioMaster;
import engine.audio.SoundEffect;
import engine.math.Vector3f;
import engine.objects.camera.Camera;
import game.Player;
import game.PlayerMovement;
import game.scene.Scene;
import game.ui.InGameMenu;
import game.ui.MainMenu;
import net.event.*;
import net.packets.Packet;
import net.packets.client.PongPacket;
import net.packets.server.*;

import java.util.List;

public class PacketInterpreter {

    public static void interpret(Packet packet) {

        if (packet instanceof ConnectionAcceptedPacket) {
            System.out.println("[PACKET] Incoming packet with type: " + packet.getPacketType());
            packet.printData();
            ConnectionAcceptedPacket crp = (ConnectionAcceptedPacket) packet;
            ClientHandler.serverRegistered = true;
            ClientHandler.playerId = crp.getPlayerId();
            Scene.createGameScene();
            Scene.setActiveScene(Scene.getGameScene());
            Camera.setActiveCamera(Scene.getGameScene().getOrbitCamera());
            System.out.println("[INFO] Connected to server! Player Id: " + crp.getPlayerId());
        } else if (packet instanceof ConnectionDeniedPacket) {
            System.out.println("[PACKET] Incoming packet with type: " + packet.getPacketType());
            packet.printData();
            ConnectionDeniedPacket crp = (ConnectionDeniedPacket) packet;
            ClientHandler.reset();
            MainMenu.connectResult.setText("Connection Failed: " + crp.getReason());
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
        } else if (packet instanceof PingRequestPacket) {
            handlePingRequestPacket((PingRequestPacket) packet);
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
                addPlayer(p.getUuid(), p.getUsername(), p.getTeam());
            }
        }

        long difference = System.currentTimeMillis() - packet.getTimestamp();
        int ticksAdd = (int) difference / 10;
        ClientHandler.startInputSender(packet.getTick() + ticksAdd);
    }

    private static void handlePlayerSpawnPacket(PlayerSpawnPacket packet) {
        addPlayer(packet.getPlayerUUID(), packet.getUsername(), packet.getTeam());
    }

    private static void handlePlayerRemovePacket(PlayerRemovePacket packet) {
        Scene.getGameScene().removePlayer(packet.getPlayerUUID());
    }

    private static void handleWorldStatePacket(WorldStatePacket packet) {

        for (Event e : packet.getWorldState().getStateEvents()) {
            if (e instanceof HitEvent) {
                HitEvent event = (HitEvent) e;
                if (event.getHit().getPlayerId() == ClientHandler.getPlayerId()) {
                    if (event.isHeadshot()) {
                        AudioMaster.playSound(SoundEffect.SHOT_HEADSHOT);
                    } else {
                        AudioMaster.playSound(SoundEffect.SHOT_HIT);
                    }
                }
            } else if (e instanceof DeathEvent) {
                DeathEvent event = (DeathEvent) e;
                if (event.getDead().getPlayerId() == ClientHandler.getPlayerId()) {
                    PlayerMovement.clientDead(event);
                } else if (event.getSource().getPlayerId() == ClientHandler.getPlayerId()) {
                    AudioMaster.playSound(SoundEffect.KILL);
                    InGameMenu.getKillsText().setText((event.getSource().getKills() + 1) + " kills");
                }
            } else if (e instanceof RespawnEvent) {
                RespawnEvent event = (RespawnEvent) e;
                if (event.getRespawned().getPlayerId() == ClientHandler.getPlayerId()) {
                    PlayerMovement.clientAlive(event);
                }
            } else if (e instanceof ShootEvent) {
                ShootEvent event = (ShootEvent) e;
            }
        }

        InGameMenu.getRedScoreText().setText(packet.getWorldState().getRedScore() + "");
        InGameMenu.getBlueScoreText().setText(packet.getWorldState().getBlueScore() + "");

        for (Player p : packet.getWorldState().getPlayers()) {
            if (p.getPlayerId() == ClientHandler.playerId) {

                if (p.isDead()) {
                    PlayerMovement.setDeathTimer(p.getDeathTimer());
                    continue;
                }

                Vector3f start = PlayerMovement.getPosition().copy();
                PlayerMovement.applyPlayer(p);

                if (p.isForceMove()) {
                    PlayerMovement.setCameraRotation(p.getRotation());
                    PlayerMovement.setCorrectionOffset(Vector3f.zero());
                    continue;
                }

                Vector3f tempRotation = PlayerMovement.getCameraRotation();
                int startSequence = p.getInputSequence();

                int i = 0;
                while (!(i >= 512 || InputSender.getPendingInputs()[i] == null)) {
                    InputState current = InputSender.getPendingInputs()[i];
                    if (current.getSequence() < startSequence) {
                        InputSender.removeRecentPending();
                        continue;
                    }
                    PlayerMovement.setCameraRotation(current.getRotation());
                    PlayerMovement.applyMovement(current.getPrevInputs(), current.getInputs(), true);
                    i++;
                }

                Vector3f correction = start.minus(PlayerMovement.getPosition());
                PlayerMovement.getCorrectionOffset().add(correction);

                PlayerMovement.setCameraRotation(tempRotation);
            }
        }
        ClientHandler.addWorldState(packet.getWorldState());
    }

    private static void addPlayer(String uuid, String username, int team) {
        Scene.getGameScene().addBufferedPlayer(uuid, team);
    }

    private static void handlePingRequestPacket(PingRequestPacket packet) {
        ClientHandler.client.sendUDP(new PongPacket(packet.getTimestamp()));
    }
}
