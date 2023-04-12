package com.dori.Dori90v.inventory;

import java.util.Arrays;

public enum ItemType {
    EQUIP(1),
    ITEM(2),
    PET(3)
    ;

    private final byte val;

    ItemType(byte val) {
        this.val = val;
    }

    ItemType(int val) {
        this((byte) val);
    }

    public byte getVal() {
        return val;
    }

    public static ItemType getTypeById(int id) {
        return Arrays.stream(ItemType.values()).filter(type -> type.getVal() == id).findFirst().orElse(null);
    }
}
