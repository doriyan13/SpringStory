package com.dori.SpringStory.enums;

import com.dori.SpringStory.utils.ItemUtils;
import lombok.Getter;

import java.util.Arrays;

@Getter
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

    public static InventoryType getTypeByItemId(int itemId) {
        if (ItemUtils.isEquip(itemId)) {
            return EQUIP;
        } else if (ItemUtils.isConsume(itemId)) {
            return CONSUME;
        } else if (ItemUtils.isInstall(itemId)) {
            return INSTALL;
        } else if (ItemUtils.isEtc(itemId)) {
            return ETC;
        } else {
            return CASH;
        }
    }
}
