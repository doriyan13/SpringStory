package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.utils.utilEntities.Position;

public interface COpenGatePool {

    static OutPacket openGateCreated(MapleChar chr,
                                     byte gateID,
                                     Position gatePosition,
                                     int partyID) {
        OutPacket outPacket = new OutPacket(OutHeader.OpenGateCreate);

        outPacket.encodeByte(1); // Animation
        outPacket.encodeInt(chr.getId()); // Character Id
        outPacket.encodePosition(gatePosition); // Position
        outPacket.encodeByte(gateID); // Gate Id
        outPacket.encodeInt(partyID); // Party Id

        return outPacket;
    }

    static OutPacket openGateRemoved(int chrID,
                                     byte gateID) {
        OutPacket outPacket = new OutPacket(OutHeader.OpenGateRemoved);

        outPacket.encodeByte(1); // Animation
        outPacket.encodeInt(chrID); // Character Id
        outPacket.encodeByte(gateID); // Gate Id

        return outPacket;
    }
}
