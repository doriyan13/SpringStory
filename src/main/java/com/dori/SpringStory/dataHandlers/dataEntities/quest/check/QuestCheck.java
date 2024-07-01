package com.dori.SpringStory.dataHandlers.dataEntities.quest.check;

import com.dori.SpringStory.client.character.MapleChar;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.WRAPPER_OBJECT, property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value= QuestExCheck.class, name="exCheck"),
        @JsonSubTypes.Type(value= QuestItemCheck.class, name="itemCheck"),
        @JsonSubTypes.Type(value= QuestJobCheck.class, name="jobCheck"),
        @JsonSubTypes.Type(value= QuestLevelCheck.class, name="levelCheck"),
        @JsonSubTypes.Type(value= QuestMobCheck.class, name="mobCheck")
})
public interface QuestCheck {
    boolean check(MapleChar chr);
}
