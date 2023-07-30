package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.world.fieldEntities.movement.MovementData;

public interface CUserRemote {
    // Logger -
    Logger logger = new Logger(CUserRemote.class);

    static OutPacket move(MapleChar chr, MovementData movementInfo) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserRemoteMove);

        outPacket.encodeInt(chr.getId());
        outPacket.encode(movementInfo);
        return outPacket;
    }
}
