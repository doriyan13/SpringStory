package com.dori.SpringStory.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Stat {
    Skin(0x1),
    Face(0x2),
    Hair(0x4),
    Pet(0x8),
    Level(0x10),
    SubJob(0x20),
    Str(0x40),
    Dex(0x80),
    Inte(0x100),
    Luk(0x200),
    Hp(0x400),
    MaxHp(0x800),
    Mp(0x1000),
    MaxMp(0x2000),
    AbilityPoint(0x4000),
    SkillPoint(0x8000),
    Exp(0x10000),
    Pop(0x20000), // fame
    Money(0x40000), // meso
    Pet2(0x80000),
    Pet3(0x100000),
    TempExp(0x200000)
    ;

    private final int val;

    Stat(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static Stat getByVal(int stat) {
        return Arrays.stream(values()).filter(s -> s.getVal() == stat).findFirst().orElse(null);
    }

    public static List<Stat> getStatsByFlag(int mask) {
        List<Stat> stats = new ArrayList<>();
        List<Stat> allStats = Arrays.asList(values());
        Collections.sort(allStats);
        for(Stat stat : allStats) {
            if((stat.getVal() & mask) != 0) {
                stats.add(stat);
            }
        }
        return stats;
    }

    public static PassiveBuffStat getStatBySkillStat(SkillStat skillStat){
        return switch (skillStat) {
            case mhpR -> PassiveBuffStat.MAX_HP;
            case mmpR -> PassiveBuffStat.MAX_MP;
            case hp -> PassiveBuffStat.HP_REGEN;
            case mp -> PassiveBuffStat.MP_REGEN;
            default -> null;
        };
    }
}
