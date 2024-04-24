package com.dori.SpringStory.client.effects.parsers;


import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;

import java.util.Map;

import static com.dori.SpringStory.enums.UserEffectTypes.Quest;


public record QuestEffect(Map<Integer, Integer> quests) implements Effect {
    @Override
    public UserEffectTypes getType() {
        return Quest;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(quests.size());
        quests.forEach((id, quantity) -> {
            outPacket.encodeInt(id);
            outPacket.encodeInt(quantity);
        });
    }
}
