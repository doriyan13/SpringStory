package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum DropLeaveType {
    TIME_OUT(0),
    SCREEN_SCROLL(1),
    USER_PICKUP(2),
    MOB_PICKUP(3),
    EXPLODE(4),
    PET_PICKUP(5),
    PASS_CONVEX(6), // idk?
    PET_SKILL(7)
    ;

    private final byte val;

    DropLeaveType(int  val) {
        this.val = (byte) val;
    }
}
