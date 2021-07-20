package game;

import java.util.ArrayList;

public class WorldState {

    private ArrayList<Player> players;
    private long timestamp;

    public WorldState() {
        players = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }

    public static WorldState createWorldState() {
        return null;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
