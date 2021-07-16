package game;

import java.util.UUID;

public class Player {

    private final int playerId;
    private final String uuid;

    private final String username;

    private int team = 0;

    public Player(int playerId, String username) {
        this.playerId = playerId;
        this.uuid = UUID.randomUUID().toString();
        this.username = username;
    }

    public Player() {
        this.playerId = 0;
        this.uuid = "";
        this.username = "";
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
