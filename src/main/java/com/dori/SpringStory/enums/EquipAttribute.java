package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum EquipAttribute {
    Locked(0x1),
    PreventSlipping(0x2),
    PreventColdness(0x4),
    Untradable(0x8),
    UntradableAfterTransaction(0x10),
    NoNonCombatStatGain(0x20),

    Crafted(0x80),
    ProtectionScroll(0x100),
    LuckyDay(0x200),

    TradedOnceWithinAccount(0x1000),
    UpgradeCountProtection(0x2000),
    ScrollProtection(0x4000),
    ReturnScroll(0x8000);

    private final int val;

    EquipAttribute(int val) {
        this.val = val;
    }

}
