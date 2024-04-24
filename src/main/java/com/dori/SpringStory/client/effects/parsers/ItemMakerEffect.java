package com.dori.SpringStory.client.effects.parsers;


import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;

public record ItemMakerEffect(boolean success) implements Effect {
    @Override
    public UserEffectTypes getType() {
        return UserEffectTypes.ItemMaker;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(!success ? 1 : 0);
    }
}
