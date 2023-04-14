package com.dori.SpringStory.enums;

import java.util.Arrays;

public enum PICType {
    UnRegistered(0),
    Registered(1),
    Disabled(2)
    ;
    private final int val;

    PICType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static PICType getTypeByInt(int i) {
        return Arrays.stream(values()).filter(p -> p.getVal() == i).findFirst().orElse(null);
    }
}
