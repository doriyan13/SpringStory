package com.dori.SpringStory.enums;

public enum AttackType {
    None(-1),
    Melee(0x00),
    Shoot(0x01),
    Magic(0x02),
    Body(0x03)
    ;

    private final int value;

    AttackType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
