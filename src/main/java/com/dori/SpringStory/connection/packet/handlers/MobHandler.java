package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.connection.packet.packets.CMobPool;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.world.fieldEntities.mob.Mob;
import com.dori.SpringStory.world.fieldEntities.movement.MovementData;


import static com.dori.SpringStory.connection.packet.headers.InHeader.MobMove;

public class MobHandler {
    // Logger -
    private static final Logger logger = new Logger(MobHandler.class);

    @Handler(op = MobMove)
    public static void handleMobMove(MapleClient c, InPacket inPacket) {
        // CMob::GenerateMovePath
        int mobObjID = inPacket.decodeInt();
        Mob mob = c.getChr().getField().getMobs().get(mobObjID);
        if (mob != null) {
            short mobCtrlSN = inPacket.decodeShort(); // move id
            byte dwFlag = inPacket.decodeByte(); // bSomeRand | 4 * (bRushMove | 2 * (bRiseByToss | 2 * nMobCtrlState));
            boolean isNextAtkPossible = (dwFlag & 0xF0) != 0; // is mob should use skill? (saw chronos did 'dwFlag > 0')
            boolean mobMoveStartResult = dwFlag > 0;
            byte nActionAndDir = inPacket.decodeByte();
            //TODO: need to handle Mob skill properly -> require to wz read MobSkills and then finish the handling! (and apply in mts)
            int skillData = inPacket.decodeInt(); // !CMob::DoSkill(v7, (unsigned __int8)dwData, BYTE1(dwData), dwData >> 16)
            byte skillID = (byte) (skillData & 0xFF);
            byte slv = (byte) (skillData >> 8 & 0xFF);
            int delay = skillData >> 16;
            byte v8 = inPacket.decodeByte();
            boolean cheatedRandom = (v8 & 0xF0) != 0;
            boolean cheatedCtrlMove = (v8 & 0xF) != 0;

            int nMultiTargetSize = inPacket.decodeInt();
            for (int i = 0; i < nMultiTargetSize; i++) {
                inPacket.decodeInt(); // aMultiTargetForBall[i].x
                inPacket.decodeInt(); // aMultiTargetForBall[i].y
            }
            int nRandTimeSize = inPacket.decodeInt();
            for (int i = 0; i < nRandTimeSize; i++) {
                inPacket.decodeInt(); // m_aRandTimeforAreaAttack[i]
            }
//            byte moveFlags = inPacket.decodeByte();
            // Hack stuff decode from the client -
            inPacket.decodeInt(); // getHackedCode
            inPacket.decodeInt(); // flyCtxTargetX
            inPacket.decodeInt(); // flyCtxTargetY
            inPacket.decodeInt(); // dwHackedCodeCRC
            // Encode the mob movement data -
            MovementData movementData = new MovementData(inPacket);
            // TODO: need to manage mob mp!
            c.write(CMobPool.mobMoveAck(mobObjID, mobCtrlSN, mobMoveStartResult, 0, skillID, slv));
            // Apply the encoding movement to the mob instance -
            movementData.applyTo(mob);
            // Send the updated move of the mob to the other clients in the field -
            mob.getController().getField().broadcastPacket(CMobPool.mobMove(mobObjID, mobMoveStartResult, nActionAndDir, skillData, movementData), mob.getController());

            inPacket.decodeBool(); // bChasing
            inPacket.decodeBool(); // hasTarget | pTarget != 0
            inPacket.decodeBool(); // bChasing 2
            inPacket.decodeBool(); // bChasingHack
            inPacket.decodeInt(); // tChaseDuration
        }
    }
}
