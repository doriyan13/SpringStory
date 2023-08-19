package com.dori.SpringStory.enums;

import java.util.Arrays;

public enum DamageType {
    Magic(0x00),
    Physical(-0x01),
    Counter(-0x02),
    Obstacle(-0x03),
    Stat(-0x04),
    None(999)
    ;

    private final int val;

    DamageType(int val){
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static DamageType getTypeByVal(byte type){
        return Arrays.stream(values())
                .filter(damageType -> damageType.getVal() == type)
                .findFirst()
                .orElse(None);
    }
}
