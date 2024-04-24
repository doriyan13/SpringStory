package com.dori.SpringStory.client.effects.parsers;


import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;

public record BuffItemEffect(int itemID) implements Effect {
    // effect like ExpItemConsumedEffect, but also plays the sound of the default item consumed
    // 2000000 (red potion) sounds like taking a regular pot, shows effect like ExpItemConsumedEffect
    // 4000000 (blue snail shell) no sound, no crash, effect like ExpItemConsumedEffect

    @Override
    public UserEffectTypes getType() {
        return UserEffectTypes.BuffItemEffect;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(itemID);
    }
}
