package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.character.attack.AttackInfo;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.enums.AttackType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.SkillUtils;
import com.dori.SpringStory.world.fieldEntities.movement.MovementData;

import java.util.Arrays;

public interface CUserRemote {
    // Logger -
    Logger logger = new Logger(CUserRemote.class);

    static OutPacket move(MapleChar chr, MovementData movementInfo) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserRemoteMove);

        outPacket.encodeInt(chr.getId());
        outPacket.encode(movementInfo);
        return outPacket;
    }

    static OutPacket attack(MapleChar chr, AttackInfo ai) {
        // Generic handling for all the attack types -
        OutPacket outPacket = new OutPacket(OutHeader.CUserRemoteAttack.getValue() + ai.getType().getValue());

        outPacket.encodeInt(chr.getId());
        outPacket.encodeByte(ai.getMobCount() << 4 | ai.getHits());
        outPacket.encodeByte(chr.getLevel());
        outPacket.encodeByte(ai.getSlv());
        if (ai.getSlv() > 0) {
            outPacket.encodeInt(ai.getSkillId());
        }
        if( ai.getType() == AttackType.Shoot && SkillUtils.isShikigamiHauntingSkill(ai.getSkillId())){
            outPacket.encodeByte(0); // passiveSLV
            if(false){
                outPacket.encodeInt(0); // passiveSkillID
            }
        }
        outPacket.encodeByte(ai.getOption());
        byte left = (byte) (ai.isLeft() ? 1 : 0);
        outPacket.encodeShort((left << 15) | ai.getAtkAction());

        if (ai.getAtkAction() <= 0x110) {
            outPacket.encodeByte(ai.getAtkActionType()); // nActionSpeed
            outPacket.encodeByte(0); // nMastery
            outPacket.encodeInt(0); // nBulletItemID
            ai.getMobAttackInfo().forEach(mai -> {
                outPacket.encodeInt(mai.getMobId());
                if(mai.getMobId() > 0){
                    outPacket.encodeByte(mai.getHitAction());
                    for(int dmg : mai.getDamages()) {
                        outPacket.encodeBool(false); // abCritical | isCritical
                        outPacket.encodeInt(dmg);
                    }
                }
            });
        }
        if(ai.getType() == AttackType.Shoot){
            outPacket.encodeShort(0);
            outPacket.encodeShort(0);
        }
        return outPacket;
    }
}
