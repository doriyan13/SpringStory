package com.dori.SpringStory.dataHandlers.dataEntities.quest.act;

import com.dori.SpringStory.client.character.MapleChar;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("expAct")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestExpAct(int exp) implements QuestAct {
    @Override
    public boolean canAct(@NotNull MapleChar chr,
                          int rewardIndex) {
        return true;
    }

    @Override
    public boolean doAct(@NotNull MapleChar chr,
                         int rewardIndex) {
        chr.gainExp(exp);
        return true;
    }
}
