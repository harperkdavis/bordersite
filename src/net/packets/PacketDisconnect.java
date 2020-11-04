package net.packets;

import net.Client;
import org.json.simple.JSONObject;

public class PacketDisconnect extends Packet {

    public PacketDisconnect() {
        super(1);
    }

    @Override
    public JSONObject getJsonData() {
        JSONObject packet = new JSONObject();
        packet.put("id", packetId);
        return packet;
    }

}
