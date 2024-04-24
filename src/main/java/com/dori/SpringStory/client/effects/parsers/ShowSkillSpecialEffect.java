package com.dori.SpringStory.client.effects.parsers;

import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.Skills;
import com.dori.SpringStory.enums.UserEffectTypes;
import com.dori.SpringStory.utils.utilEntities.Position;

public record ShowSkillSpecialEffect(int skillID, int slv, Position timeBombPos) implements Effect {
    // when using 4341003 as skillid, shows explosion on certain position based on timeBombX/timeBombY
    // using a skill other than 4341003 seems to do nothin
    @Override
    public UserEffectTypes getType() {
        return UserEffectTypes.ShowSkillSpecialEffect;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(skillID);
        if (skillID == Skills.DUAL5_MONSTER_BOMB.getId()) {
            outPacket.encodeInt(timeBombPos.getX());
            outPacket.encodeInt(timeBombPos.getY());
            outPacket.encodeInt(slv);
            outPacket.encodeInt(0);
        }
    }
}
