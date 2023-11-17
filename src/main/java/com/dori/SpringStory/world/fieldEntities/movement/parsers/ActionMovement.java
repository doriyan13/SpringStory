package com.dori.SpringStory.world.fieldEntities.movement.parsers;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Life;
import com.dori.SpringStory.world.fieldEntities.movement.BaseMovement;

public class ActionMovement extends BaseMovement {
    public ActionMovement(InPacket inPacket, byte attr) {
        super();
        this.attr = attr;
        this.position = new Position(0, 0);
        super.decode(inPacket);
    }

    @Override
    public void encode(OutPacket outPacket) {
        super.encode(outPacket);
    }

    @Override
    public void applyTo(MapleChar chr) {
        chr.setMoveAction(moveAction);
    }

    @Override
    public void applyTo(Life life) {
        life.setMoveAction(moveAction);
    }
}
