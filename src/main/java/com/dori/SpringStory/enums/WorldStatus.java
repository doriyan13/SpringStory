package com.dori.SpringStory.enums;

public enum WorldStatus {
    NORMAL(0), // Select world normally -> "Since there are many users, you may encounter some..."
    BUSY(1), //Highly populated -> "The concurrent users in this world have reached the max"
    FULL(2);

    private final byte value;

    WorldStatus(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
