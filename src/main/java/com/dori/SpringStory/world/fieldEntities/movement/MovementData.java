package com.dori.SpringStory.world.fieldEntities.movement;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Encodable;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.MovementPathAttr;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Life;
import com.dori.SpringStory.world.fieldEntities.movement.parsers.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MovementData implements Encodable {
    private static final Logger logger = new Logger(MovementData.class);

    private Position oldPos;
    private Position oldVPos;
    private List<Movement> movements = new ArrayList<>();

    public MovementData(Position oldPos, Position oldVPos) {
        this.oldPos = oldPos;
        this.oldVPos = oldVPos;
    }

    public MovementData(InPacket inPacket) {
        decode(inPacket);
    }

    public void applyTo(MapleChar chr) {
        for (Movement m : getMovements()) {
            m.applyTo(chr);
        }
    }

    public void applyTo(Life life) {
        for (Movement m : getMovements()) {
            m.applyTo(life);
        }
    }

    public void decode(InPacket inPacket) {
        oldPos = inPacket.decodePosition();
        oldVPos = inPacket.decodePosition();
        movements = parseMovement(inPacket);
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodePosition(oldPos);
        outPacket.encodePosition(oldVPos);
        outPacket.encodeByte(movements.size());
        for(Movement m : movements) {
            m.encode(outPacket);
        }
    }

    private static List<Movement> parseMovement(InPacket inPacket) {
        List<Movement> movements = new ArrayList<>();
        byte size = inPacket.decodeByte();
        for (int i = 0; i < size; i++) {
            MovementPathAttr attr = MovementPathAttr.getElementByNum(inPacket.decodeByte());
            //  CMovePath::Decode -
            switch (attr) {
                case Normal, HangOnBack, FallDown, Wings, MobAtkRush, MobAtkRushStop
                        -> movements.add(new NormalMovement(inPacket,attr));
                case Jump, Impact, StartWings, MobToss, DashSlide, MobLadder,
                        MobRightAngle, MobStopNodeStart, MobBeforeNode
                        -> movements.add(new JumpMovement(inPacket,attr));
                case FlashJump, RocketBooster, BackStepShot, MobPowerKnockBack,
                        VerticalJump, CustomImpact, CombatStep, Hit, TimeBombAtk,
                        SnowBallTouch, BuffZoneEffect
                        -> movements.add(new ActionMovement(inPacket,attr));
                case Immediate, Teleport, Assaulter, Assassination, Rush, SitDown
                        -> movements.add(new TeleportMovement(inPacket,attr));
                case StatChange -> movements.add(new StatChangeMovement(inPacket,attr));
                case StartFallDown -> movements.add(new StartFallDownMovement(inPacket,attr));
                case FlyingBlock -> movements.add(new FlyingBlockMovement(inPacket,attr));
                default -> logger.warning("Movement not handled: " + attr.getVal());
            }
        }
        return movements;
    }

}
