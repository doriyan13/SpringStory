package com.dori.SpringStory.client.effects.parsers;


import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;

public record ConsumeEffect(int itemID) implements Effect {
    // shows various effects from Item.wz/cash/0528.img
    // items under 0528: 5280000 (fart), 5281000 (???), 5281001 (fairy above head)
    @Override
    public UserEffectTypes getType() {
        return UserEffectTypes.ConsumeEffect;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // IDK why but chronos log this logic - itemID / 10_000 != 528
        outPacket.encodeInt(itemID);
    }
}
