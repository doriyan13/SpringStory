package com.dori.SpringStory.enums;

import java.util.Arrays;

import static com.dori.SpringStory.enums.EquipBaseStat.*;

public enum ScrollStat {
    success,
    incSTR,
    incDEX,
    incINT,
    incLUK,
    incPAD,
    incMAD,
    incPDD,
    incMDD,
    incACC,
    incEVA,
    incMHP,
    incMMP,
    incSpeed,
    incJump,
    incIUC,
    incPERIOD,
    incReqLevel,
    reqRUC,
    randOption,
    randStat,
    tuc,
    speed,
    forceUpgrade,
    cursed,
    maxSuperiorEqp,
    noNegative,
    incRandVol,
    reqEquipLevelMax,
    createType,
    optionType, recover, reset, perfectReset, reduceCooltime,
    boss,
    ignoreTargetDEF,
    incSTRr,
    incDEXr,
    incINTr,
    incLUKr,
    incCriticaldamageMin,
    incCriticaldamageMax,
    cCr,
    incDAMr,
    incPDDr,
    incMDDr,
    incEVAr,
    incACCr,
    incMHPr,
    incMMPr,
    incTerR,
    incAsrR,
    incMesoProp,
    incRewardProp,
    setItemCategory,
    ;

    public static ScrollStat getScrollStatByString(String name) {
        return Arrays.stream(values()).filter(ss -> ss.toString().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static EquipBaseStat[] getRandStats() {
        return new EquipBaseStat[]{iStr, iDex, iInt, iLuk, iMaxHP, iMaxMP, iPAD, iMAD, iPDD, iMDD, iACC, iEVA};
    }

    public EquipBaseStat getEquipStat() {
        return switch (this) {
            case incSTR -> iStr;
            case incDEX -> iDex;
            case incINT -> iInt;
            case incLUK -> iLuk;
            case incPAD -> iPAD;
            case incMAD -> iMAD;
            case incPDD -> iPDD;
            case incMDD -> iMDD;
            case incACC -> iACC;
            case incEVA -> iEVA;
            case incMHP -> iMaxHP;
            case incMMP -> iMaxMP;
            case incSpeed, speed -> iSpeed;
            case incJump -> iJump;
            case incReqLevel -> iReduceReq;
            default -> null;
        };
    }

    public BaseStat getBaseStat() {
        return switch (this) {
            case incSTR -> BaseStat.str;
            case incDEX -> BaseStat.dex;
            case incINT -> BaseStat.inte;
            case incLUK -> BaseStat.luk;
            case incPAD -> BaseStat.pad;
            case incMAD -> BaseStat.mad;
            case incPDD -> BaseStat.pdd;
            case incMDD -> BaseStat.mdd;
            case incACC -> BaseStat.acc;
            case incEVA -> BaseStat.eva;
            case incMHP -> BaseStat.mhp;
            case incMMP -> BaseStat.mmp;
            case incSpeed, speed -> BaseStat.speed;
            case incJump -> BaseStat.jump;
            case incSTRr -> BaseStat.strR;
            case incDEXr -> BaseStat.dexR;
            case incINTr -> BaseStat.intR;
            case incLUKr -> BaseStat.lukR;
            case incMHPr -> BaseStat.mhpR;
            case incMMPr -> BaseStat.mmpR;
            default -> null;
        };
    }
}
