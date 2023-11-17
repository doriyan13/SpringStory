package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum PickupMessageType {
    ITEM_WITH_QUANTITY(0),
    MESO(1),
    ITEM_WITHOUT_QUANTITY(2)
    ;
    private final int val;

    PickupMessageType(int val) {
        this.val = val;
    }
}
