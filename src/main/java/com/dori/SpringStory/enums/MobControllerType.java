package com.dori.SpringStory.enums;

public enum MobControllerType {
    Reset((byte) 0),
    ActiveInit((byte) 1),
    ActiveReq((byte) 2),
    ActivePerm0((byte) 3),
    ActivePerm1((byte) 4),
    Passive((byte) -1),
    Passive0((byte) -2),
    Passive1((byte) -3);

    private final byte val;

    MobControllerType(byte val) {
        this.val = val;
    }

    public byte getVal() {
        return val;
    }
}
