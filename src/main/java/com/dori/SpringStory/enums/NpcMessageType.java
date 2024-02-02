package com.dori.SpringStory.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum NpcMessageType {
    SayOk(0, false, false),
    SayNext(0, false, true),
    SayPrev(0, true, false),
    Say(0, true, true),
    SayImage(1, false, false),
    AskYesNo(2, false, false),
    AskText(3, false, false),
    AskNumber(4, false, false),

    AskMenu(5, false, false),
    AskQuiz(6, false, false),
    AskSpeedQuiz(7, false, false),
    AskAvatar(8, false, false),
    AskMemberShopAvatar(9, false, false),
    AskPet(10, false, false),
    AskPetAll(11, false, false),
    AskAccept(13, false, false),
    AskBoxText(14, false, false),
    AskSlideMenu(15, false, false),
    ;
    private final byte val;
    private final boolean prevPossible;
    private final boolean nextPossible;

    NpcMessageType(int val, boolean prevPossible, boolean nextPossible) {
        this.val = (byte) val;
        this.prevPossible = prevPossible;
        this.nextPossible = nextPossible;
    }

    public static NpcMessageType getNpcMsgTypeByVal(byte type) {
        return Arrays
                .stream(values())
                .filter(npcMsgType -> npcMsgType.val == type)
                .findFirst()
                .orElse(null);
    }
}
