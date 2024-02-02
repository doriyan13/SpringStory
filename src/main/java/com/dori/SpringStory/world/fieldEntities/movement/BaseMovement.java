package com.dori.SpringStory.world.fieldEntities.movement;


import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.MovementPathAttr;
import com.dori.SpringStory.utils.utilEntities.Position;
import lombok.Data;

@Data
public abstract class BaseMovement implements Movement {
    protected byte command;
    protected byte moveAction;
    protected byte forcedStop;
    protected byte stat;

    protected short fh;
    protected short footStart;
    protected short elapse;

    protected Position position;
    protected Position vPosition;
    protected Position offset;
    protected byte attr = MovementPathAttr.NONE;

    @Override
    public byte getCommand() {
        return command;
    }

    @Override
    public byte getMoveAction() {
        return moveAction;
    }

    @Override
    public byte getForcedStop() {
        return forcedStop;
    }

    @Override
    public byte getStat() {
        return stat;
    }

    @Override
    public short getFh() {
        return fh;
    }

    @Override
    public short getFootStart() {
        return footStart;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Position getVPosition() {
        return vPosition;
    }

    @Override
    public Position getOffset() {
        return offset;
    }

    @Override
    public short getDuration() {
        return elapse;
    }

    @Override
    public byte getAttr() {
        return attr;
    }

    public void decode(InPacket inPacket) {
        moveAction = inPacket.decodeByte();
        elapse = inPacket.decodeShort();
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(getMoveAction());
        outPacket.encodeShort(getElapse());
    }
}
