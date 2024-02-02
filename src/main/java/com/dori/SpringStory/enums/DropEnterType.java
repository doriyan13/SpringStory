package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum DropEnterType {
    DEFAULT(0),
    FLOATING(1),
    INSTANT(2),
    FADE_AWAY(3),
    SACRIFICE_ITEM(4);

    private final byte val;

    DropEnterType(int val) {
        this.val = (byte) val;
    }
}
