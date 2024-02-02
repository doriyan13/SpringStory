package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.character.attack.AttackInfo;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.enums.AttackType;
import com.dori.SpringStory.enums.DamageType;
import com.dori.SpringStory.enums.Skills;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.SkillUtils;
import com.dori.SpringStory.world.fieldEntities.movement.MovementData;

import java.util.Arrays;

import static com.dori.SpringStory.enums.Skills.*;

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
        // For easier to read skill handling -
        Skills atkSkill = Skills.getSkillById(ai.getSkillId());

        // Generic handling for all the attack types -
        OutPacket outPacket = new OutPacket(OutHeader.CUserRemoteAttack.getValue() + ai.getType().getValue());

        outPacket.encodeInt(chr.getId());

        outPacket.encodeByte(ai.getMobCount() << 4 | ai.getHits());
        outPacket.encodeByte(chr.getLevel());
        outPacket.encodeByte(ai.getSlv());
        if (ai.getSlv() > 0) {
            outPacket.encodeInt(ai.getSkillId());
        }
        if(atkSkill == SNIPER_STRAFE){
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
            outPacket.encodeShort(0); // ptBallStart.x
            outPacket.encodeShort(0); // ptBallStart.y
        }
        switch (atkSkill){
            case ARCHMAGE1_BIGBANG, ARCHMAGE2_BIGBANG, BISHOP_BIGBANG, EVAN_ICE_BREATH,
                    EVAN_BREATH -> outPacket.encodeInt(0); // tKeyDown
            case WILDHUNTER_SWALLOW_DUMMY_ATTACK -> {
                // mob dwid, not mob template id
                outPacket.encodeInt(0); // m_dwSwallowMobID
            }
        }
        return outPacket;
    }

    static OutPacket hit(MapleChar chr, DamageType type, int dmg, int mobID, boolean isLeft){
        OutPacket outPacket = new OutPacket(OutHeader.CUserRemoteHit);
        final int thiefDodgeSkillID = 4120002;

        outPacket.encodeInt(chr.getId());
        outPacket.encodeByte(type.getVal());
        outPacket.encodeInt(dmg);
        if( type.getVal() > DamageType.Counter.getVal()) {
            outPacket.encodeInt(mobID);
            outPacket.encodeBool(isLeft);
            boolean isStance = false;
            outPacket.encodeBool(isStance); // stance?
            if(isStance){
                outPacket.encodeByte(0); // bPowerGuard
                outPacket.encodeInt(0); // ptHit.x
                outPacket.encodeByte(0); // nHitAction
                outPacket.encodeShort(0); // ptHit.x
                outPacket.encodeShort(0); // ptHit.y
            }
            outPacket.encodeBool(false); // bGuard
            outPacket.encodeBool(false); // flag of 1 or 2 -> stance skill thing (stance_skill_id == 33110000)
        }
        outPacket.encodeInt(dmg); // nDelta
        if(dmg < 0){
            outPacket.encodeInt(thiefDodgeSkillID);
        }

        return outPacket;
    }
}
