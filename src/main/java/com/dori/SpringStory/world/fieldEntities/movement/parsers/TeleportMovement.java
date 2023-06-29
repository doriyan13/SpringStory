package com.dori.SpringStory.world.fieldEntities.movement.parsers;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.MovementPathAttr;
import com.dori.SpringStory.world.fieldEntities.Life;
import com.dori.SpringStory.world.fieldEntities.movement.BaseMovement;


public class TeleportMovement extends BaseMovement {
    public TeleportMovement(InPacket inPacket, MovementPathAttr attr) {
        super();
        this.attr = attr;
        position = inPacket.decodePosition();
        fh = inPacket.decodeShort();

        super.decode(inPacket);
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodePosition(getPosition());
        outPacket.encodeShort(getFh());

        super.encode(outPacket);
    }

    @Override
    public void applyTo(MapleChar chr) {
        chr.setPosition(getPosition());
//        chr.setFoothold(getFh());
//        chr.setMoveAction(getMoveAction());
    }

    @Override
    public void applyTo(Life life) {
        life.setPosition(getPosition());
        life.setFh(getFh());
        life.setMoveAction(getMoveAction());
    }
}
