package net.packets;

import net.Client;
import org.json.simple.JSONObject;

public class PacketLogin extends Packet {

    private String username;

    public PacketLogin(String username) {
        super(0);
        this.username = username;
    }

    @Override
    public JSONObject getJsonData() {
        JSONObject packet = new JSONObject();
        packet.put("id", packetId);
        packet.put("username", username);
        return packet;
    }
}