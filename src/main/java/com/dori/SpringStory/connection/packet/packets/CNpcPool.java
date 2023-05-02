package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.world.fieldEntities.Npc;

public interface CNpcPool {
    static OutPacket npcEnterField(Npc npc) {
        OutPacket outPacket = new OutPacket(OutHeader.NpcEnterField);

        outPacket.encodeInt(npc.getObjectId());
        outPacket.encodeInt(npc.getTemplateId());
        npc.encode(outPacket);

        return outPacket;
    }
}
