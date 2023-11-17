package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum DropOwnType {
    USER_OWN(0),
    PARTY_OWN(1),
    NO_OWN(2),
    EXPLOSIVE_NO_OWN(3);

    private final byte val;

    DropOwnType(int val) {
        this.val = (byte) val;
    }
}
