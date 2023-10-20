package com.dori.SpringStory.temporaryStats.characters;

import com.dori.SpringStory.enums.Job;
import com.dori.SpringStory.enums.Skills;
import org.springframework.stereotype.Service;

import static com.dori.SpringStory.enums.Skills.*;
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
        // Warrior:
        // Fighter
        add(Job.Fighter, FIGHTER_WEAPON_BOOSTER, Booster, "-2", false, "10 * x");
        add(Job.Fighter, FIGHTER_POWER_GUARD, PowerGuard, "10 + 2 * x", false, "10 * u(x / 2)");
        // Crusader
        //TODO: need to make it a list of stats cuz it have two parts?
        add(Job.Crusader, CRUSADER_COMBO_ATTACK, ComboCounter, "1", false, "100 + 10 * d(x / 2)"); // TODO: need server side handle combo attack counter and if have advance one and such.. also consuming the stacks is server sided!
        add(Job.Crusader, CRUSADER_COMBO_ATTACK, DamR, "u(x/4)", false, "100 + 10 * d(x / 2)");
        //TODO: magic crash is a mts, need to handle it there i think?
        add(Job.Hero, HERO_STANCE, Stance, "30 + 2 * x", false, "10 * x");
    }
}
