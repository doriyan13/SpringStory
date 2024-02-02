package com.dori.SpringStory.enums;

public enum SkillStat {
    acc,
    emdd,
    criticaldamageMin,
    hp,
    bulletCount,
    subTime,
    itemConsume,
    mhpR,
    padX,
    pad,
    moneyCon,
    action,
    criticaldamageMax,
    jump,
    emhp,
    epad,
    mp,
    dotInterval,
    mdd,
    er,
    rb,
    selfDestruction,
    mmpR,
    hpCon,
    madX,
    mobCount,
    morph,
    damage,
    ignoreMobpdpR,
    itemConNo,
    epdd,
    dot,
    range,
    itemCon,
    speed,
    mastery,
    mad,
    eva,
    dotTime,
    pddR,
    prop,
    bulletConsume,
    subProp,
    attackCount,
    emmp,
    terR,
    mpCon,
    damR,
    cooltime,
    cr,
    pdd,
    mesoR,
    t,
    u,
    v,
    mddR,
    w,
    x,
    y,
    z,
    expR,
    time,
    asrR
    ;

    public static SkillStat getSkillStatByString(String s) {
        for(SkillStat skillStat : SkillStat.values()) {
            if(skillStat.toString().equals(s)) {
                return skillStat;
            }
        }
        return null;
    }

    public BaseStat getBaseStat() {
        return switch (this) {
            case pdd, epdd -> BaseStat.pdd;
            case pddR -> BaseStat.pddR;
            case emdd, mdd -> BaseStat.mdd;
            case mddR -> BaseStat.mddR;
            case emhp -> BaseStat.mhp;
            case mhpR -> BaseStat.mhpR;
            case emmp -> BaseStat.mmp;
            case mmpR -> BaseStat.mmpR;
            case speed -> BaseStat.speed;
            case jump -> BaseStat.jump;
            case asrR -> BaseStat.asr;
            case pad, padX, epad -> BaseStat.pad;
            case mad, madX -> BaseStat.mad;
            case terR -> BaseStat.ter;
            case eva -> BaseStat.eva;
            case mastery -> BaseStat.mastery;
            case ignoreMobpdpR -> BaseStat.ied;
            case criticaldamageMin -> BaseStat.minCd;
            case criticaldamageMax -> BaseStat.maxCd;
            case cr, expR, mesoR -> BaseStat.mesoR;
            case hp -> BaseStat.hpRecovery;
            case mp -> BaseStat.mpRecovery;
            default -> null;
        };
    }
}
