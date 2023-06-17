package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.packets.CMobPool;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.world.fieldEntities.Mob;

import static com.dori.SpringStory.connection.packet.headers.InHeader.MobMove;

public class MobHandler {
    // Logger -
    private static final Logger logger = new Logger(MobHandler.class);

    @Handler(op = MobMove)
    public static void handleMobMove(MapleClient c, InPacket inPacket) {
        int mobObjID = inPacket.decodeInt();
        Mob mob = c.getChr().getField().getMobs().get(mobObjID);
        short mobCtrlSN = inPacket.decodeShort(); // move id
        byte dwFlag = inPacket.decodeByte(); // bSomeRand | 4 * (bRushMove | 2 * (bRiseByToss | 2 * nMobCtrlState));
        boolean isNextAtkPossible = (dwFlag & 0xF) != 0; // is mob should use skill? (saw chronos did 'dwFlag > 0')
        byte currSplit = inPacket.decodeByte(); // the skill?
        int illegalVelocity = inPacket.decodeInt();
        byte nActionAndDir = inPacket.decodeByte();
        int skillData = inPacket.decodeInt(); // !CMob::DoSkill(v7, (unsigned __int8)dwData, BYTE1(dwData), dwData >> 16)
        int nMultiTargetSize = inPacket.decodeInt();

        //TODO: NEED TO FINISH HANDLING!
        for (int i = 0; i < nMultiTargetSize; i++) {
            inPacket.decodeInt(); // aMultiTargetForBall[i].x
            inPacket.decodeInt(); // aMultiTargetForBall[i].y
        }
        int nRandTimeSize = inPacket.decodeInt();
        for (int i = 0; i < nRandTimeSize; i++) {
            inPacket.decodeInt(); // m_aRandTimeforAreaAttack[i]
        }

        int getHackedCode = inPacket.decodeInt();
        int flyCtxTargetX = inPacket.decodeInt();
        int flyCtxTargetY = inPacket.decodeInt();
        int dwHackedCodeCRC = inPacket.decodeInt();

        c.write(CMobPool.mobMoveAck(mobObjID,mobCtrlSN,isNextAtkPossible,0));
        OutPacket outPacket = CMobPool.mobMove(mobObjID,isNextAtkPossible,nActionAndDir,skillData);
        int amountOfMovements = MovementPathHandler.updateMovementPath(inPacket,outPacket,mob);

        byte isChasing = inPacket.decodeByte(); // bChasing
        byte pTarget = inPacket.decodeByte(); // pTarget != 0
        byte isChasing2 = inPacket.decodeByte(); // bChasing
        byte isChasingHack = inPacket.decodeByte(); // bChasingHack
        int chaseDuration = inPacket.decodeInt(); // tChaseDuration

        if(amountOfMovements > 0){
            c.write(outPacket);
        }
    }
}
