package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.handlers.MovementPathHandler;
import com.dori.SpringStory.enums.MovementPathAttr;
import com.dori.SpringStory.utils.utilEntities.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MovementElement {
    // Fields -
    private MovementPathAttr attribute;
    private Position currPos;
    private Position vPos;
    private Position offSet;
    private short fh;
    private short fhFallStart;
    private short fhLast;
    private boolean stat;
    private byte moveAction;
    private short elapse;

    public void resetPosTo(MovementElement from) {
        currPos.setX(from.getCurrPos().getX());
        currPos.setY(from.getCurrPos().getY() - 20); // yOffSet is 20
        fh = from.getFh();
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodePosition(getCurrPos());
        outPacket.encodeByte(getMoveAction());
        outPacket.encodeShort(getFh());
    }
}
