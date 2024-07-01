package com.dori.SpringStory.dataHandlers.dataEntities.quest.act;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.character.Skill;
import com.dori.SpringStory.dataHandlers.SkillDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.QuestSkillData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@JsonTypeName("skillAct")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestSkillAct(Set<QuestSkillData> skills) implements QuestAct {
    @Override
    public boolean canAct(@NotNull MapleChar chr, int rewardIndex) {
        return true;
    }

    @Override
    public boolean doAct(@NotNull MapleChar chr, int rewardIndex) {
        for (QuestSkillData questSkillData : skills) {
            if (!questSkillData.jobs().contains(chr.getJob())) {
                continue;
            }
            Skill skill = SkillDataHandler.getSkillByID(questSkillData.skillID());
            if (skill == null) {
                return false;
            }
            skill.setCurrentLevel(questSkillData.skillLvl());
            skill.setMasterLevel(questSkillData.skillLvl());
            chr.addSkill(skill);
        }
        return true;
    }
}
