package net.packets;

import net.Client;
import org.json.simple.JSONObject;

public class PacketInputs extends Packet {

    public PacketInputs(int packetId) {
        super(3);
    }

    @Override
    public JSONObject getJsonData() {
        return null;
    }

}
