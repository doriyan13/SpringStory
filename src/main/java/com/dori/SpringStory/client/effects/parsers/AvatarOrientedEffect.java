package com.dori.SpringStory.client.effects.parsers;


import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;

public record AvatarOrientedEffect(String path) implements Effect {

    @Override
    public UserEffectTypes getType() {
        return UserEffectTypes.AvatarOriented;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeString(path);
        outPacket.encodeInt(0); // unused int
    }
}
