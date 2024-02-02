package com.dori.SpringStory.enums;

public enum WorldState {
    Normal(0),
    Event(1),
    New(2),
    Hot(3)
    ;
    private final byte value;

    WorldState(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
