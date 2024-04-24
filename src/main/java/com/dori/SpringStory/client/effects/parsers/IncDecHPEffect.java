package com.dori.SpringStory.client.effects.parsers;


import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;

public record IncDecHPEffect(int delta) implements Effect {
    // blue number over characters head (healing)
    @Override
    public UserEffectTypes getType() {
        return UserEffectTypes.IncDecHPEffect;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(delta);
    }
}
