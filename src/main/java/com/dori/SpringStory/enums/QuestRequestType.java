package com.dori.SpringStory.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum QuestRequestType {
    LostItem(0),
    AcceptQuest(1),
    CompleteQuest(2),
    ResignQuest(3),
    OpeningScript(4),
    CompleteScript(5)
    ;

    private final int val;

    QuestRequestType(int val) {
        this.val = val;
    }

    public static QuestRequestType getQuestTypeByVal(byte val) {
        return Arrays.stream(values())
                .filter(questRequestType -> questRequestType.getVal() == val)
                .findFirst()
                .orElse(null);
    }
}
