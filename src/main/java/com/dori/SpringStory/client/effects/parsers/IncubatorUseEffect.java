package com.dori.SpringStory.client.effects.parsers;


import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;

public record IncubatorUseEffect(int itemID, String effectPath) implements Effect {
    @Override
    public UserEffectTypes getType() {
        return UserEffectTypes.IncubatorUse;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(itemID);
        outPacket.encodeString(effectPath);
    }
}
