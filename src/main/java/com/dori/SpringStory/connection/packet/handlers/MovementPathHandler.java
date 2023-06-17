package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.world.fieldEntities.MovementElement;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.MovementPathAttr;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Life;

import java.util.ArrayList;
import java.util.List;

public class MovementPathHandler {
    // Logger -
    private static final Logger logger = new Logger(MovementPathHandler.class);

    public static void encodePath(OutPacket outPacket, List<MovementElement> movementElements) {
        outPacket.encodeByte(movementElements.size());

        for (MovementElement element : movementElements) {
            outPacket.encodeByte(element.getAttribute().getVal());
            switch (element.getAttribute()) {
                case Normal, HangOnBack, FallDown, Wings, MobAtkRush, MobAtkRushStop -> {
                    outPacket.encodePosition(element.getCurrPos());
                    outPacket.encodePosition(element.getVPos());
                    outPacket.encodeShort(element.getFh());
                    if (element.getAttribute() == MovementPathAttr.FallDown) {
                        outPacket.encodeShort(element.getFhFallStart());
                    }
                    outPacket.encodePosition(element.getOffSet());
                }
                case Jump, Impact, StartWings, MobToss, DashSlide, MobLadder, MobRightAngle, MobStopNodeStart, MobBeforeNode -> {
                    outPacket.encodePosition(element.getVPos());
                }
                case Immediate, Teleport, Assaulter, Assassination, Rush, SitDown -> {
                    outPacket.encodePosition(element.getCurrPos());
                    outPacket.encodeShort(element.getFh());
                }
                case StatChange -> outPacket.encodeByte(element.isStat());
                case StartFallDown -> {
                    outPacket.encodePosition(element.getVPos());
                    outPacket.encodeShort(element.getFhFallStart());
                }
                case FlyingBlock -> {
                    outPacket.encodePosition(element.getCurrPos());
                    outPacket.encodePosition(element.getVPos());
                }
                default -> logger.warning("Case not handled: " + element.getAttribute());
            }

            if (element.getAttribute() != MovementPathAttr.StatChange) {
                outPacket.encodeByte(element.getMoveAction());
                outPacket.encodeShort(element.getElapse());
            }
        }
    }

    public static List<MovementElement> decodePath(InPacket inPacket, Life lifeObj) {
        byte amountOfElements = inPacket.decodeByte();
        Position oldCurrPos = new Position(lifeObj.getPosition().getX(), lifeObj.getPosition().getY());
        Position oldVPos = new Position(lifeObj.getVPosition().getX(), lifeObj.getVPosition().getY());
        short currFh = lifeObj.getFh();
        byte moveAction = lifeObj.getMoveAction();

        List<MovementElement> movementElements = new ArrayList<>();
        for (int i = 0; i < amountOfElements; i++) {
            MovementElement element = new MovementElement();
            element.setAttribute(MovementPathAttr.getElementByNum(inPacket.decodeByte()));
            switch (element.getAttribute()) {
                case Normal, HangOnBack, FallDown, Wings, MobAtkRush, MobAtkRushStop -> {
                    element.setCurrPos(new Position(inPacket.decodeShort(), inPacket.decodeShort()));
                    element.setVPos(new Position(inPacket.decodeShort(), inPacket.decodeShort()));
                    element.setFh(inPacket.decodeShort());
                    element.setFhLast(element.getFh());
                    if (element.getAttribute() == MovementPathAttr.FallDown) {
                        element.setFhFallStart(inPacket.decodeShort());
                    }
                    element.setOffSet(new Position(inPacket.decodeShort(), inPacket.decodeShort()));
                }
                case Jump, Impact, StartWings, MobToss, DashSlide, MobLadder, MobRightAngle, MobStopNodeStart, MobBeforeNode -> {
                    element.setCurrPos(oldCurrPos);
                    element.setVPos(new Position(inPacket.decodeShort(), inPacket.decodeShort()));
                }
                case Immediate, Teleport, Assaulter, Assassination, Rush, SitDown -> {
                    element.setCurrPos(new Position(inPacket.decodeShort(), inPacket.decodeShort()));
                    element.setFh(inPacket.decodeShort());
                    element.setFhLast(element.getFh());
                }
                case StatChange -> {
                    element.setStat(inPacket.decodeBool());
                    element.setCurrPos(oldCurrPos);
                    lifeObj.setFh((short) 0); // Not sure about it?
                }
                case StartFallDown -> {
                    element.setCurrPos(oldCurrPos);
                    element.setVPos(new Position(inPacket.decodeShort(), inPacket.decodeShort()));
                    element.setFhFallStart(inPacket.decodeShort());
                }
                case FlyingBlock -> {
                    element.setCurrPos(new Position(inPacket.decodeShort(), inPacket.decodeShort()));
                    element.setVPos(new Position(inPacket.decodeShort(), inPacket.decodeShort()));
                }
                case FlashJump, RocketBooster, BackStepShot, MobPowerKnockBack, VerticalJump, CustomImpact,
                        CombatStep, Hit, TimeBombAtk, SnowBallTouch, BuffZoneEffect -> {
                    element.setCurrPos(oldCurrPos);
                    element.setVPos(oldVPos);
                }
                default -> logger.warning("Case not handled: " + element.getAttribute());
            }
            if (element.getAttribute() != MovementPathAttr.StatChange) {
                moveAction = inPacket.decodeByte();
                element.setMoveAction(moveAction);
                element.setElapse(inPacket.decodeShort());

                oldCurrPos = new Position(element.getCurrPos().getX(), element.getCurrPos().getY());
                oldVPos = new Position(element.getVPos().getX(), element.getVPos().getY());
                if (element.getFh() != 0) {
                    currFh = element.getFh();
                }
            }
            movementElements.add(element);
        }
        if (movementElements.size() > 0) {
            lifeObj.getPosition().setX(oldCurrPos.getX());
            lifeObj.getPosition().setY(oldCurrPos.getY());
            lifeObj.setMoveAction(moveAction);
            lifeObj.setFh(currFh);
        }
        return movementElements;
    }

    public static int updateMovementPath(InPacket inPacket, OutPacket outPacket, Life lifeObj) {
        Position currPos = new Position(inPacket.decodeShort(), inPacket.decodeShort());
        Position vPos = new Position(inPacket.decodeShort(), inPacket.decodeShort());

        outPacket.encodePosition(currPos);
        outPacket.encodePosition(vPos);

        List<MovementElement> elements = MovementPathHandler.decodePath(inPacket, lifeObj);
        MovementPathHandler.encodePath(outPacket, elements);

        return elements.size();
    }
}
