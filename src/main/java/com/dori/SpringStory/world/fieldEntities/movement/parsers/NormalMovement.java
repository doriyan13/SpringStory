package com.dori.SpringStory.world.fieldEntities.movement.parsers;


import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.MovementPathAttr;
import com.dori.SpringStory.world.fieldEntities.Life;
import com.dori.SpringStory.world.fieldEntities.movement.BaseMovement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NormalMovement extends BaseMovement {
    public NormalMovement(InPacket inPacket, byte attr) {
        super();
        this.attr = attr;

        position = inPacket.decodePosition();
        vPosition = inPacket.decodePosition();
        fh = inPacket.decodeShort();

        if(attr == MovementPathAttr.FALL_DOWN){
            footStart = inPacket.decodeShort();
        }
        offset = inPacket.decodePosition();

        super.decode(inPacket);
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodePosition(getPosition());
        outPacket.encodePosition(getVPosition());
        outPacket.encodeShort(getFh());
        if (attr == MovementPathAttr.FALL_DOWN) {
            outPacket.encodeShort(getFootStart());
        }
        outPacket.encodePosition(getOffset());

        super.encode(outPacket);
    }

    @Override
    public void applyTo(MapleChar chr) {
        chr.setPosition(getPosition());
        chr.setFoothold(getFh());
        chr.setMoveAction(getMoveAction());
    }

    @Override
    public void applyTo(Life life) {
        life.setPosition(getPosition());
        life.setVPosition(getVPosition());
        life.setFh(getFh());
        life.setMoveAction(getMoveAction());
    }
}
