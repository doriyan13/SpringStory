package com.dori.SpringStory.client.effects.parsers;


import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;

public record PetShowEffect(int option, int petID) implements Effect {

    @Override
    public UserEffectTypes getType() {
        return UserEffectTypes.PetShowEffect;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(option);
        outPacket.encodeByte(petID);
    }
}
