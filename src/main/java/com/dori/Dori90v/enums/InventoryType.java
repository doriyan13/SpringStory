package com.dori.Dori90v.enums;

import java.util.Arrays;

public enum InventoryType {
    EQUIPPED(-1),
    EQUIP(1),
    CONSUME(2),
    ETC(4),
    INSTALL(3),
    CASH(5);

    private final byte val;

    InventoryType(int val) {
        this.val = (byte) val;
    }

    InventoryType(byte val) {
        this.val = val;
    }

    public byte getVal() {
        return val;
    }

    public static InventoryType getInventoryByVal(int val) {
        return Arrays.stream(InventoryType.values()).filter(t -> t.getVal() == val).findFirst().orElse(null);
    }

    public static InventoryType getInvTypeByString(String subMap) {
        subMap = subMap.toLowerCase();
        return switch (subMap) {
            case "cash", "pet" -> CASH;
            case "consume", "special", "use" -> CONSUME;
            case "etc" -> ETC;
            case "install", "setup" -> INSTALL;
            case "eqp", "equip" -> EQUIP;
            default -> null;
        };
    }

    public boolean isStackable() {
        return this != EQUIP && this != EQUIPPED && this != CASH;
    }
}
