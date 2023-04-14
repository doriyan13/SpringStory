package com.dori.SpringStory.enums;

public enum BaseStat {
    unk,
    str,
    strR,
    dex,
    dexR,
    inte,
    intR,
    luk,
    lukR,
    pad,
    padR,
    mad,
    madR,
    pdd,
    pddR,
    mdd,
    mddR,
    mhp,
    mhpR,
    mmp,
    mmpR,
    cr, // Crit rate
    minCd, // Min crit damage
    maxCd, // Max crit damage
    fd, // Final damage (total damage)
    bd, // Boss damage
    ied, // Ignore enemy defense
    asr, // All status resistance
    ter, // Status time minus
    acc,
    accR,
    eva,
    evaR,
    jump,
    speed,
    expR,
    dropR,
    mesoR,
    booster,
    stance,
    mastery,
    damageOver, // max damage
    allStat,
    allStatR,
    hpRecovery,
    mpRecovery,
    incAllSkill,
    strLv,
    dexLv,
    intLv,
    lukLv,
    buffTimeR, // Buff Duration multiplier
    recoveryUp, mpconReduce, reduceCooltime, padLv, madLv, mhpLv, mmpLv; // % increase in heal potion use


    public static BaseStat getFromStat(Stat s) {
        return switch (s) {
            case str -> str;
            case dex -> dex;
            case inte -> inte;
            case luk -> luk;
            case mhp -> mhp;
            case mmp -> mmp;
            default -> unk;
        };
    }

    public BaseStat getRateVar() {
        return switch (this) {
            case str -> strR;
            case dex -> dexR;
            case inte -> intR;
            case luk -> lukR;
            case pad -> padR;
            case mad -> madR;
            case pdd -> pddR;
            case mdd -> mddR;
            case mhp -> mhpR;
            case mmp -> mmpR;
            case acc -> accR;
            case eva -> evaR;
            default -> null;
        };
    }

    public BaseStat getLevelVar() {
        return switch (this) {
            case str -> strLv;
            case dex -> dexLv;
            case inte -> intLv;
            case luk -> lukLv;
            case pad -> padLv;
            case mad -> madLv;
            case mhp -> mhpLv;
            case mmp -> mmpLv;
            default -> null;
        };
    }

    //TODO: CTS!

    public Stat toStat() {
        return switch (this) {
            case str -> Stat.str;
            case dex -> Stat.dex;
            case inte -> Stat.inte;
            case luk -> Stat.luk;
            case mhp -> Stat.mhp;
            case mmp -> Stat.mmp;
            default -> null;
        };
    }
}
