package com.dori.Dori90v.enums;

public enum EnchantStat implements Comparable<EnchantStat> {
    PAD(0x1),
    MAD(0x2),
    STR(0x4),
    DEX(0x8),
    INT(0x10),
    LUK(0x20),
    PDD(0x40),
    MDD(0x80),
    MHP(0x100),
    MMP(0x200),
    ACC(0x400),
    EVA(0x800),
    JUMP(0x1000),
    SPEED(0x2000);
    
    private final int val;

    EnchantStat(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public EquipBaseStat getEquipBaseStat() {
        return switch (this) {
            case PAD -> EquipBaseStat.iPAD;
            case MAD -> EquipBaseStat.iMAD;
            case STR -> EquipBaseStat.iStr;
            case DEX -> EquipBaseStat.iDex;
            case INT -> EquipBaseStat.iInt;
            case LUK -> EquipBaseStat.iLuk;
            case PDD -> EquipBaseStat.iPDD;
            case MDD -> EquipBaseStat.iMDD;
            case MHP -> EquipBaseStat.iMaxHP;
            case MMP -> EquipBaseStat.iMaxMP;
            case ACC -> EquipBaseStat.iACC;
            case EVA -> EquipBaseStat.iEVA;
            case JUMP -> EquipBaseStat.iJump;
            case SPEED -> EquipBaseStat.iSpeed;
        };
    }

    public static EnchantStat getByEquipBaseStat(EquipBaseStat ebs) {
        return switch (ebs) {
            case iPAD -> PAD;
            case iMAD -> MAD;
            case iStr -> STR;
            case iDex -> DEX;
            case iInt -> INT;
            case iLuk -> LUK;
            case iPDD -> PDD;
            case iMDD -> MDD;
            case iMaxHP -> MHP;
            case iMaxMP -> MMP;
            case iACC -> ACC;
            case iEVA -> EVA;
            case iJump -> JUMP;
            case iSpeed -> SPEED;
            default -> null;
        };
    }

    public boolean isAttackType() {
        return this == PAD || this == MAD;
    }

    public boolean isHpOrMp() {
        return this == MHP || this == MMP;
    }

}
