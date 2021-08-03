package game;

import net.event.Event;

import java.util.ArrayList;

public class WorldState {

    private ArrayList<Player> players;
    private ArrayList<Event> stateEvents;
    private int redScore, blueScore;
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

    public ArrayList<Event> getStateEvents() {
        return stateEvents;
    }

    public int getRedScore() {
        return redScore;
    }

    public int getBlueScore() {
        return blueScore;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
