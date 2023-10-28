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
                    String durationInSecFormula,
                    int coolDownInSec,
                    int intervalInSec,
                    boolean healthRegen) {
        addBuff(job, skill.getId(), new BuffData(stat, calcFormula, durationInSecFormula, coolDownInSec, intervalInSec, healthRegen));
    }

    static void add(Job job,
                    Skills skill,
                    CharacterTemporaryStat stat,
                    String calcFormula,
                    String durationInSecFormula) {
        add(job, skill, stat, calcFormula, durationInSecFormula, 0, 0, false);
    }

    static void loadCustomBuffsData() {
        // Beginner -
        add(Job.Beginner, BEGINNER_RECOVERY, Regen, "4 * x", "30", 10 * 60, 10, true);
        add(Job.Beginner, BEGINNER_NIMBLE_FEET, Speed, "5 + 5 * x", "4 * x");
        // Warrior:
        // Fighter
        add(Job.Fighter, FIGHTER_WEAPON_BOOSTER, Booster, "-2", "10 * x");
        add(Job.Fighter, FIGHTER_POWER_GUARD, PowerGuard, "10 + 2 * x", "10 * u(x / 2)");
        // Crusader
        add(Job.Crusader, CRUSADER_COMBO_ATTACK, ComboCounter, "1", "100 + 10 * d(x / 2)"); // TODO: need server side handle combo attack counter and if have advance one and such.. also consuming the stacks is server sided!
        // Hero
        add(Job.Hero, HERO_STANCE, Stance, "30 + 2 * x", "10 * x");
        add(Job.Hero, HERO_MAPLE_HERO, BasicStatUp, "u (x / 2)", "30 * x");
        add(Job.Hero, HERO_ENRAGE, Enrage, "2 * x", "60 + 4 * x");
        // Page -
        add(Job.Page, PAGE_WEAPON_BOOSTER, Booster, "-2", "10 * x");
        add(Job.Page, PAGE_POWER_GUARD, PowerGuard, "10 + 2 * x", "10 * u(x / 2)");
        // White Knight
        add(Job.WhiteKnight, WHITE_KNIGHT_COMBAT_ORDERS, CombatOrders, "u (x / 19)", "60 + (6 * x)");
        add(Job.WhiteKnight, WHITE_KNIGHT_ICE_CHARGE, WeaponCharge, "1", "10 * x");
        add(Job.WhiteKnight, WHITE_KNIGHT_LIGHTNING_CHARGE, WeaponCharge, "1", "10 * x");
        add(Job.WhiteKnight, WHITE_KNIGHT_FIRE_CHARGE, WeaponCharge, "1", "10 * x");
        // Paladin
        // need to handle Guardian stuff (check if shield equipped and such & need to handle divine shield!)
        // TODO: Divine shield works simialr to combo attack - raise stacks till 5 and it give 2 stats - blessing armor and PAD (epad) and after max stacks the buff being removed and cooldown to x time, notice the regenarte of the buff is precentage base!
        add(Job.Paladin, PALADIN_DIVINE_CHARGE, WeaponCharge, "1", "10 * x");
        add(Job.Paladin, PALADIN_DIVINE_CHARGE, Mad, "40", "10 * x");

        // Spear-man
        add(Job.Spearman, SPEARMAN_IRON_WALL, Pdd, "15 * x", "15 * x");
        add(Job.Spearman, SPEARMAN_IRON_WALL, Mdd, "5 * x", "15 * x");
        add(Job.Spearman, SPEARMAN_HYPER_BODY, MaxHp, "3 * x", "15 * x");
        add(Job.Spearman, SPEARMAN_HYPER_BODY, MaxMp, "3 * x", "15 * x");
    }
}
