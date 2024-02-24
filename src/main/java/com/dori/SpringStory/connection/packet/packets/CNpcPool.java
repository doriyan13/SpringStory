package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.world.fieldEntities.Npc;
import com.dori.SpringStory.world.fieldEntities.movement.MovementData;

public interface CNpcPool {
    static OutPacket npcEnterField(Npc npc) {
        OutPacket outPacket = new OutPacket(OutHeader.NpcEnterField);

        outPacket.encodeInt(npc.getObjectId());
        outPacket.encodeInt(npc.getTemplateId());
        npc.encode(outPacket);

        return outPacket;
    }

    static OutPacket npcChangeController(Npc npc,
                                         boolean controller,
                                         boolean remove) {
        OutPacket outPacket = new OutPacket(OutHeader.NpcChangeController);

        outPacket.encodeByte(controller);
        outPacket.encodeInt(npc.getObjectId());
        // Remove - is when the controller leave the field!
        if (!remove) {
            outPacket.encodeInt(npc.getTemplateId());
            npc.encode(outPacket);
        }

        return outPacket;
    }

    static OutPacket npcMove(int objectID,
                             byte oneTimeAction,
                             byte chatIdx,
                             boolean move,
                             MovementData movementInfo) {
        OutPacket outPacket = new OutPacket(OutHeader.NpcMove);

        outPacket.encodeInt(objectID);
        outPacket.encodeByte(oneTimeAction);
        outPacket.encodeByte(chatIdx);
        if (move) {
            outPacket.encode(movementInfo);
        }
        return outPacket;
    }
}
