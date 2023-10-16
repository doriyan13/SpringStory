package com.dori.SpringStory.temporaryStats.characters;

import com.dori.SpringStory.enums.Job;
import com.dori.SpringStory.enums.SkillConsumeStatType;
import com.dori.SpringStory.enums.Skills;
import org.springframework.stereotype.Service;

import static com.dori.SpringStory.enums.Skills.BEGINNER_NIMBLE_FEET;
import static com.dori.SpringStory.enums.Skills.BEGINNER_RECOVERY;
import static com.dori.SpringStory.temporaryStats.characters.BuffDataHandler.addBuff;
import static com.dori.SpringStory.temporaryStats.characters.CharacterTemporaryStat.*;

@Service
public interface BuffsDataLoader {

    static void add(Job job,
                    Skills skill,
                    CharacterTemporaryStat stat,
                    String calcFormula,
                    boolean additionalValue,
                    String durationInSecFormula,
                    int cooldownInSec,
                    int intervalInSec,
                    SkillConsumeStatType consumeStatType) {
        addBuff(job, skill.getId(), new BuffData(stat, calcFormula, additionalValue, durationInSecFormula, cooldownInSec, intervalInSec, consumeStatType));
    }

    static void add(Job job,
                    Skills skill,
                    CharacterTemporaryStat stat,
                    String calcFormula,
                    boolean additionalValue,
                    String durationInSecFormula,
                    int cooldownInSec,
                    int intervalInSec) {
        addBuff(job, skill.getId(), new BuffData(stat, calcFormula, additionalValue, durationInSecFormula, cooldownInSec, intervalInSec, SkillConsumeStatType.MP));
    }

    static void add(Job job,
                    Skills skill,
                    CharacterTemporaryStat stat,
                    String calcFormula,
                    boolean additionalValue,
                    String durationInSecFormula) {
        add(job, skill, stat, calcFormula, additionalValue, durationInSecFormula, 0, 0);
    }

    static void loadCustomBuffsData() {
        // Beginner -
        add(Job.Beginner, BEGINNER_RECOVERY, Regen, "24 * x / 3", false, "30", 10 * 60, 10, SkillConsumeStatType.MP);
        add(Job.Beginner, BEGINNER_NIMBLE_FEET, Speed, "5 + 5 * x", false, "4 * x");
    }
}
