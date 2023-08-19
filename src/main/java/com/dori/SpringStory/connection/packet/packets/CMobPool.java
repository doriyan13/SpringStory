package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.enums.MobControllerType;
import com.dori.SpringStory.world.fieldEntities.mob.Mob;
import com.dori.SpringStory.world.fieldEntities.movement.MovementData;

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

    static OutPacket mobMoveAck(int mobID, short mobCtrlSN, boolean isNextAtkPossible, int mp, byte skillID, byte slv) {
        OutPacket outPacket = new OutPacket(OutHeader.MobCtrlAck);

        outPacket.encodeInt(mobID);
        outPacket.encodeShort(mobCtrlSN);
        outPacket.encodeBool(isNextAtkPossible);
        outPacket.encodeShort(Math.min(Short.MAX_VALUE, mp));
        outPacket.encodeByte(skillID); // pCommand.nSkillID
        outPacket.encodeByte(slv); // pCommand.nSLV

        return outPacket;
    }

    static OutPacket mobMove(int mobID, boolean isNextAtkPossible, byte actionAndDir, int skillData, MovementData movementData){
        // CMob::OnMove
        OutPacket outPacket = new OutPacket(OutHeader.MobMove);

        outPacket.encodeInt(mobID);
        outPacket.encodeBool(false); // bNotForceLanding | bNotForceLandingWhenDiscard
        outPacket.encodeBool(isNextAtkPossible); // bNotChangeAction
        outPacket.encodeBool(false); // bNextAttackPossible
        outPacket.encodeByte(actionAndDir); // bLeft
        outPacket.encodeInt(skillData); // skill information
        outPacket.encodeInt(0); // aMultiTargetForBall LOOP
        outPacket.encodeInt(0); // aRandTimeforAreaAttack LOOP

        outPacket.encode(movementData);

        return outPacket;
    }

    static OutPacket hpIndicator(int mobObjID, byte percDamage) {
        OutPacket outPacket = new OutPacket(OutHeader.MobHPIndicator);

        outPacket.encodeInt(mobObjID);
        outPacket.encodeByte(percDamage);

        return outPacket;
    }

    static OutPacket mobLeaveField(int mobObjID) {
        OutPacket outPacket = new OutPacket(OutHeader.MobLeaveField);

        outPacket.encodeInt(mobObjID);
        outPacket.encodeByte(1); // 0 = dissapear, 1 = fade out, 2+ = special

        return outPacket;
    }
}
