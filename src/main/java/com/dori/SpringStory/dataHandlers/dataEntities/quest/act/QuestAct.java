package com.dori.SpringStory.dataHandlers.dataEntities.quest.act;

import com.dori.SpringStory.client.character.MapleChar;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.WRAPPER_OBJECT, property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=QuestExpAct.class, name="expAct"),
        @JsonSubTypes.Type(value= QuestItemAct.class, name="itemAct"),
        @JsonSubTypes.Type(value= QuestMoneyAct.class, name="moneyAct"),
        @JsonSubTypes.Type(value= QuestPopAct.class, name="popAct"),
        @JsonSubTypes.Type(value= QuestSkillAct.class, name="skillAct")
})
public interface QuestAct {
    boolean canAct(@NotNull MapleChar chr, int rewardIndex);
    boolean doAct(@NotNull MapleChar chr, int rewardIndex);
}
