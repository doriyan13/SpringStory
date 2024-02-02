package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;

public interface CClientSocket {
    static OutPacket migrateCommand(boolean succeed, byte[] machineID, int port) {
        OutPacket outPacket = new OutPacket(OutHeader.MigrateCommand);

        outPacket.encodeByte(succeed); // will disconnect if false
        if(succeed) {
            outPacket.encodeArr(machineID);
            outPacket.encodeShort(port);
        }

        return outPacket;
    }
}
