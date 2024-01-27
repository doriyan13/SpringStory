package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.utils.utilEntities.Position;

public interface CTownPortalPool {

    static OutPacket townPortalCreated(int chrID,
                                     boolean noAnimation,
                                     Position townPortalPosition) {
        OutPacket outPacket = new OutPacket(OutHeader.TownPortalCreated);

        outPacket.encodeByte(noAnimation); // No Animation  (false = Animation : true = No Animation)
        outPacket.encodeInt(chrID); // dwCharacterID
        outPacket.encodePosition(townPortalPosition);
        outPacket.encodePosition(townPortalPosition);

        return outPacket;
    }

    static OutPacket townPortalRemoved(int chrID,
                                     boolean activateAnimation) {
        OutPacket outPacket = new OutPacket(OutHeader.TownPortalRemoved);

        outPacket.encodeBool(activateAnimation); // Animation
        outPacket.encodeInt(chrID);

        return outPacket;
    }
}
