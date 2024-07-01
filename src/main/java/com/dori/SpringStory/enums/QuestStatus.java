package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum QuestStatus {
    NotStarted(0),
    Started(1),
    Completed(2),
    PartyQuest(3),
    No(4)
    ;

    private final byte val;

    QuestStatus(int val) {
        this.val = (byte) val;
    }
}
