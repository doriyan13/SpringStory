package com.dori.SpringStory.world.fieldEntities.movement.parsers;


import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.MovementPathAttr;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Life;
import com.dori.SpringStory.world.fieldEntities.movement.BaseMovement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StatChangeMovement extends BaseMovement {
    public StatChangeMovement(InPacket inPacket, MovementPathAttr attr) {
        super();
        this.attr = attr;
        this.position = new Position(0, 0);

        this.stat = inPacket.decodeByte();
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(getStat());
    }

    @Override
    @Deprecated
    public void applyTo(MapleChar chr) {

    }

    @Override
    @Deprecated
    public void applyTo(Life life) {

    }
}
