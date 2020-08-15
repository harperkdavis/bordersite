package net.packets;

import net.Client;

public class PacketInputs extends Packet {

    public PacketInputs(int packetId) {
        super(03);
    }

    @Override
    public byte[] getData() {
        return new byte[0];
    }

    @Override
    public void writeData(Client client) {

    }
}
