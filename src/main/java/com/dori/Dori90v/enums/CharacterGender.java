package com.dori.Dori90v.enums;

import java.util.Arrays;

public enum CharacterGender {
    Boy(0),
    Girl(1);

    private final byte value;

    CharacterGender(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    public static CharacterGender getGenderByOrdinal(int ordinal){
        return Arrays.stream(CharacterGender.values())
                .filter(characterGender -> characterGender.getValue() == ordinal)
                .findFirst()
                .orElse(CharacterGender.Boy);
    }
}
