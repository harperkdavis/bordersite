package net.event;

import game.Player;

public class RespawnEvent implements Event {

    private final Player respawned;
    private final int subtick;

    public RespawnEvent(Player respawned, int subtick) {
        this.respawned = respawned.copy();
        this.subtick = subtick;
    }

    public RespawnEvent() {
        this.respawned = null;
        this.subtick = 0;
    }

    public Player getRespawned() {
        return respawned;
    }

    public int getSubtick() {
        return subtick;
    }

}
