package com.dori.SpringStory.dataHandlers.dataEntities.quest.act;

import com.dori.SpringStory.client.character.MapleChar;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("moneyAct")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestMoneyAct(int money) implements QuestAct {

    private boolean canAddMeso(@NotNull MapleChar chr) {
        long newAmount = ((long) chr.getMeso() + money());
        return newAmount >= 0 && newAmount <= Integer.MAX_VALUE;
    }

    @Override
    public boolean canAct(@NotNull MapleChar chr, int rewardIndex) {
        return canAddMeso(chr);
    }

    @Override
    public boolean doAct(@NotNull MapleChar chr, int rewardIndex) {
        boolean act = canAddMeso(chr);
        if (act) {
            chr.modifyMeso(money);
        }
        return act;
    }
}
