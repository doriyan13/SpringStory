package com.dori.SpringStory.dataHandlers.dataEntities.quest.check;

import com.dori.SpringStory.client.character.MapleChar;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Set;

@JsonTypeName("jobCheck")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestJobCheck(Set<Integer> jobs) implements QuestCheck {
    @Override
    public boolean check(MapleChar chr) {
        return jobs.contains(chr.getJob());
    }
}
