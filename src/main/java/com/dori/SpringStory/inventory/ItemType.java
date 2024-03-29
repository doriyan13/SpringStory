package com.dori.SpringStory.inventory;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ItemType {
    EQUIP(0x01),
    BUNDLE(0x02),
    PET(0x03)
    ;

    private final byte val;

    ItemType(byte val) {
        this.val = val;
    }

    ItemType(int val) {
        this((byte) val);
    }

    public static ItemType getTypeById(int id) {
        return Arrays.stream(ItemType.values()).filter(type -> type.getVal() == id).findFirst().orElse(null);
    }
}
