package net.packets;

import net.Client;

public class PacketDisconnect extends Packet {

    public PacketDisconnect() {
        super(01);
    }

    @Override
    public byte[] getData() {
        return "01".getBytes();
    }

    @Override
    public void writeData(Client client) {
        client.sendData(getData());
    }
}
