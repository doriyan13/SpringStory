package com.dori.SpringStory.temporaryStats.characters;

import com.dori.SpringStory.enums.Job;
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
                    int coolDownInSec,
                    int intervalInSec,
                    boolean healthRegen) {
        addBuff(job, skill.getId(), new BuffData(stat, calcFormula, additionalValue, durationInSecFormula, coolDownInSec, intervalInSec, healthRegen));
    }

    static void add(Job job,
                    Skills skill,
                    CharacterTemporaryStat stat,
                    String calcFormula,
                    boolean additionalValue,
                    String durationInSecFormula) {
        add(job, skill, stat, calcFormula, additionalValue, durationInSecFormula, 0, 0, false);
    }

    static void loadCustomBuffsData() {
        // Beginner -
        add(Job.Beginner, BEGINNER_RECOVERY, Regen, "4 * x", false, "30", 10 * 60, 10, true);
        add(Job.Beginner, BEGINNER_NIMBLE_FEET, Speed, "5 + 5 * x", false, "4 * x");

    }
}
