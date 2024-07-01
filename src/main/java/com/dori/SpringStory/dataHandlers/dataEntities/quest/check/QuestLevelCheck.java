package com.dori.SpringStory.dataHandlers.dataEntities.quest.check;

import com.dori.SpringStory.client.character.MapleChar;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("levelCheck")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestLevelCheck(int level,
                              boolean minimum) implements QuestCheck {
    @Override
    public boolean check(MapleChar chr) {
        return minimum ? chr.getLevel() >= level : chr.getLevel() <= level;
    }
}
