package com.dori.SpringStory.client.effects.parsers;

import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;
import org.jetbrains.annotations.Nullable;

public record ProtectOnDieItemUseEffect(boolean use, int days, int times, @Nullable Integer itemID) implements Effect {
    @Override
    public UserEffectTypes getType() {
        return UserEffectTypes.ProtectOnDieItemUse;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeBool(use);
        outPacket.encodeByte(days);
        outPacket.encodeByte(times);
        if(!use) {
            outPacket.encodeInt(itemID != null ? itemID : 400000);
        }
    }
}
