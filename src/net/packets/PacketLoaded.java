package net.packets;

import org.json.simple.JSONObject;

public class PacketLoaded extends Packet {

    public PacketLoaded() {
        super(6);
    }

    @Override
    public JSONObject getJsonData() {
        JSONObject object = new JSONObject();

        object.put("id", 6);

        return object;
    }
}
