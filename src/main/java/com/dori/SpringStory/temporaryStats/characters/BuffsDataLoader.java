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
                    boolean health) {
        addBuff(job, skill.getId(), new BuffData(stat, calcFormula, durationInSecFormula, coolDownInSec, intervalInSec, health));
    }

    static void add(Job job,
                    Skills skill,
                    CharacterTemporaryStat stat,
                    String calcFormula,
                    String durationInSecFormula) {
        add(job, skill, stat, calcFormula, durationInSecFormula, 0, 0, false);
    }

    static void add(Job job,
                    Skills skill,
                    CharacterTemporaryStat stat,
                    String calcFormula,
                    String durationInSecFormula,
                    int intervalInSec,
                    boolean health) {
        add(job, skill, stat, calcFormula, durationInSecFormula, 0, intervalInSec, health);
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
        // TODO: Divine shield works similar to combo attack - raise stacks till 5 and it give 2 stats - blessing armor and PAD (epad) and after max stacks the buff being removed and cool down to x time, notice the regenerate of the buff is percentage base!
        add(Job.Paladin, PALADIN_DIVINE_CHARGE, WeaponCharge, "1", "10 * x");
        add(Job.Paladin, PALADIN_DIVINE_CHARGE, Mad, "40", "10 * x");

        // Spear-man
        add(Job.Spearman, SPEARMAN_WEAPON_BOOSTER, Booster, "-2", "10 * x");
        add(Job.Spearman, SPEARMAN_IRON_WALL, Pdd, "15 * x", "15 * x");
        add(Job.Spearman, SPEARMAN_IRON_WALL, Mdd, "5 * x", "15 * x");
        add(Job.Spearman, SPEARMAN_HYPER_BODY, MaxHp, "3 * x", "15 * x");
        add(Job.Spearman, SPEARMAN_HYPER_BODY, MaxMp, "3 * x", "15 * x");
        // Dragon Knight
        add(Job.DragonKnight, DRAGONKNIGHT_DRAGON_BLOOD, DragonBlood, "100 - 3 * x", "10 * x", 1, true);
        add(Job.DragonKnight, DRAGONKNIGHT_DRAGON_BLOOD, Pad, "5 + x", "10 * x");
        // Rogue
        add(Job.Thief, ROGUE_DARK_SIGHT, DarkSight, "1", "20 * x");
        // Assassin
        add(Job.Assasin, ASSASSIN_CLAW_BOOSTER, Booster, "-2", "10 * x");
        add(Job.Assasin, ASSASSIN_HASTE, Speed, "2 * x", "10 * x");
        add(Job.Assasin, ASSASSIN_HASTE, Jump, "x", "10 * x");
        // TODO: need to think how to implement Drain correctly? (i think i need to check skillId in the hitMob handling)
        // Hermit
        add(Job.Hermit, HERMIT_MESO_UP, MesoUp, "110 + 2 * x", "10 * x");
        add(Job.Hermit, HERMIT_SHADOW_PARTNER, ShadowPartner, "10 + 2 * x", "60 + 40 * d(x/6)");
        // Night Lord
        add(Job.Nightlord, NIGHTLORD_MAPLE_HERO, BasicStatUp, "u (x / 2)", "30 * x");
//        add(Job.Nightlord, NIGHTLORD_SHADOW_STARS, SpiritJavelin, "1", "90 + 3 * x"); TODO: need to make it a dynamic handling -> the value is the consumedItemId % 10_000 +1 | to let the client to know which star to use automaticlly ... MONKA

        // Machanic 1st Job -
        add(Job.Mechanic1, MECHANIC_HN07, Mechanic, "x", "99999999");
        add(Job.Mechanic1, MECHANIC_HN07, RideVehicle, "1932016", "-1000");

        // GM
        add(Job.GM, ADMIN_HASTE, Speed, "40", "1800");
        add(Job.GM, ADMIN_HASTE, Jump, "20", "1800");
        // Super GM
        add(Job.SuperGM, ADMIN_SUPER_HASTE, Speed, "40", "1800");
        add(Job.SuperGM, ADMIN_SUPER_HASTE, Jump, "20", "1800");
    }
}
