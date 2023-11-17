package com.dori.SpringStory.world.fieldEntities.movement;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Life;

public interface Movement {
    void encode(OutPacket outPacket);
    Position getPosition();

    byte getCommand();

    byte getMoveAction();

    byte getForcedStop();

    byte getStat();

    short getFh();

    short getFootStart();

    short getDuration();

    Position getVPosition();

    Position getOffset();

    void applyTo(MapleChar chr);

    void applyTo(Life life);

    byte getAttr();
}
