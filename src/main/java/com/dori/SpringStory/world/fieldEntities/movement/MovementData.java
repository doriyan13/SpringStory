package com.dori.SpringStory.world.fieldEntities.movement;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Encodable;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Life;
import com.dori.SpringStory.world.fieldEntities.movement.parsers.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.dori.SpringStory.enums.MovementPathAttr.*;

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
        for (Movement m : movements) {
            outPacket.encodeByte(m.getAttr());
            m.encode(outPacket);
        }
    }

    private static List<Movement> parseMovement(InPacket inPacket) {
        List<Movement> movements = new ArrayList<>();
        byte size = inPacket.decodeByte();
        for (int i = 0; i < size; i++) {
            byte attr = inPacket.decodeByte();
            //  CMovePath::Decode -
            switch (attr) {
                case NORMAL, HANG_ON_BACK, FALL_DOWN, WINGS, MOB_ATK_RUSH, MOB_ATK_RUSH_STOP ->
                        movements.add(new NormalMovement(inPacket, attr));
                case JUMP, IMPACT, START_WINGS, MOB_TOSS, DASH_SLIDE, MOB_LADDER,
                        MOB_RIGHT_ANGLE, MOB_STOP_NODE_START, MOB_BEFORE_NODE ->
                        movements.add(new JumpMovement(inPacket, attr));
                case FLASH_JUMP, ROCKET_BOOSTER, BACK_STEP_SHOT, MOB_POWER_KNOCK_BACK,
                        VERTICAL_JUMP, CUSTOM_IMPACT, COMBAT_STEP, HIT, TIME_BOMB_ATK,
                        SNOW_BALL_TOUCH, BUFF_ZONE_EFFECT -> movements.add(new ActionMovement(inPacket, attr));
                case IMMEDIATE, TELEPORT, ASSAULTER, ASSASSINATION, RUSH, SIT_DOWN ->
                        movements.add(new TeleportMovement(inPacket, attr));
                case STAT_CHANGE -> movements.add(new StatChangeMovement(inPacket, attr));
                case START_FALL_DOWN -> movements.add(new StartFallDownMovement(inPacket, attr));
                case FLYING_BLOCK -> movements.add(new FlyingBlockMovement(inPacket, attr));
                default -> logger.warning("Movement not handled: " + attr);
            }
        }
        return movements;
    }

}
