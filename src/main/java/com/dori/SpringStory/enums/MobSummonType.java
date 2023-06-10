package com.dori.SpringStory.enums;

public enum MobSummonType {
    Normal((byte) -1),
    Regen((byte) -2),
    Revived((byte) -3),
    Suspended((byte) -4),
    Delay((byte) -5),
    Effect((byte) 0);

    private final byte val;

    MobSummonType(byte val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
