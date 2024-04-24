package com.dori.SpringStory.client.effects.parsers;


import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;

public record SkillAffectedSelectEffect(int select, int skillID, int slv) implements Effect {
    @Override
    public UserEffectTypes getType() {
        return UserEffectTypes.SkillAffectedSelect;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(select);
        outPacket.encodeInt(skillID);
        outPacket.encodeByte(slv);
    }
}
