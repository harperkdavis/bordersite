package net.packets;

import net.Client;

public class PacketLogin extends Packet {

    private String username;

    public PacketLogin(String username) {
        super(00);
        this.username = username;
    }

    @Override
    public byte[] getData() {
        return ("00" + this.username).getBytes();
    }

    @Override
    public void writeData(Client client) {
        client.sendData(getData());
    }
}
