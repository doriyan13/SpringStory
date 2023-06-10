package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.enums.MobControllerType;
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

    static OutPacket mobChangeController(Mob mob, MobControllerType mobCtrlType) {
        OutPacket outPacket = new OutPacket(OutHeader.MobChangeController);

        outPacket.encodeByte(mobCtrlType.getVal()); // 0 = None | 1 = Control | 2 = Aggro
        outPacket.encodeInt(mob.getObjectId());

        if(mobCtrlType.getVal() > MobControllerType.Reset.getVal()){
            outPacket.encodeByte(5); // nCalcDamageIndex | Controller
            outPacket.encodeInt(mob.getTemplateId());
            // Encode mob data -
            mob.encode(outPacket);
        }
        return outPacket;
    }
}
