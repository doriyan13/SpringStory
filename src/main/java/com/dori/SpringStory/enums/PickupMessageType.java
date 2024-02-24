package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum PickupMessageType {
    CANT_GET_ANYMORE_ITEMS_3(-5),
    CANT_GET_ANYMORE_ITEMS_2(-4),
    FAILED_ITEM_GAIN_DAMAGED_GAME_FILES(-3),
    ITEM_UNAVAILABLE_TO_PICKUP(-2),
    CANT_GET_ANYMORE_ITEMS(-1),
    ITEM_WITH_QUANTITY(0),
    MESO(1),
    ITEM_WITHOUT_QUANTITY(2)
    ;
    private final int val;

    PickupMessageType(int val) {
        this.val = val;
    }
}
