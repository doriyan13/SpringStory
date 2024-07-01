package com.dori.SpringStory.dataHandlers.dataEntities.quest.act;

import com.dori.SpringStory.client.character.MapleChar;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("popAct")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestPopAct(int pop) implements QuestAct {

    private boolean canAddFame(@NotNull MapleChar chr) {
        int newAmount = chr.getPop() + pop();
        return newAmount >= Short.MIN_VALUE && newAmount <= Short.MAX_VALUE;
    }

    @Override
    public boolean canAct(@NotNull MapleChar chr, int rewardIndex) {
        return canAddFame(chr);
    }

    @Override
    public boolean doAct(@NotNull MapleChar chr, int rewardIndex) {
        boolean act = canAddFame(chr);
        if (act) {
            chr.modifyPop(pop);
        }
        return false;
    }
}
