package com.dori.SpringStory.dataHandlers.dataEntities.quest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestSkillData(int skillID,
                             int skillLvl,
                             int masterLvl,
                             Set<Integer> jobs) {
}
