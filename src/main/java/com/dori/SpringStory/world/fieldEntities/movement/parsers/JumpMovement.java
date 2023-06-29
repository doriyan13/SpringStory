package com.dori.SpringStory.world.fieldEntities.movement.parsers;


import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.MovementPathAttr;
import com.dori.SpringStory.world.fieldEntities.Life;
import com.dori.SpringStory.world.fieldEntities.movement.BaseMovement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JumpMovement extends BaseMovement {
    public JumpMovement(InPacket inPacket, MovementPathAttr attr) {
        super();
        this.attr = attr;
        position = inPacket.decodePosition();

        super.decode(inPacket);
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodePosition(getVPosition());

        super.encode(outPacket);
    }

    @Override
    public void applyTo(MapleChar chr) {
        chr.setPosition(getPosition());
        //TODO: need to handle!
        //chr.setMoveAction(getMoveAction());
    }

    @Override
    public void applyTo(Life life) {
        life.setPosition(getPosition());
        life.setMoveAction(getMoveAction());
    }
}
