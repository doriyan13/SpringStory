package com.dori.SpringStory.enums;

public enum InventoryOperation {
    Add(0),
    UpdateQuantity(1),
    Move(2),
    Remove(3),
    ItemExp(4)
    ;

    private final byte val;

    InventoryOperation(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }
}
