package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.world.fieldEntities.Mob;
import com.dori.SpringStory.world.fieldEntities.Npc;

public interface CMobPool {
    static OutPacket mobEnterField(Mob mob) {
        OutPacket outPacket = new OutPacket(OutHeader.MobEnterField);

        outPacket.encodeInt(mob.getObjectId());
        outPacket.encodeByte(1); // nCalcDamageIndex | Controller
        outPacket.encodeInt(mob.getTemplateId());
        // CMob::Init -
        mob.encode(outPacket);

        return outPacket;
    }
}
