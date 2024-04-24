package com.dori.SpringStory.client.effects.parsers;


import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;

public record UpgradeTombItemUseEffect(int amountLeft) implements Effect {
    // grey text in chatbox that says "You have used 1 Wheel of Destiny in order to revive at the current map. (3 left)
    @Override
    public UserEffectTypes getType() {
        return UserEffectTypes.UpgradeTombItemUse;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(amountLeft);
    }
}
