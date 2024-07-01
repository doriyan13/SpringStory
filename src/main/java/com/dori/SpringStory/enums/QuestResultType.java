package com.dori.SpringStory.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum QuestResultType {
    // QuestRes
    StartQuestTimer(6),
    EndQuestTimer(7),
    StartTimeKeepQuestTimer(8),
    EndTimeKeepQuestTimer(9),

    // QuestRes_Act
    Success(10),
    FailedUnknown(11),
    FailedInventory(12),
    FailedMeso(13),
    FailedPet(14),
    FailedEquipped(15),
    FailedOnlyItem(16),
    FailedTimeOver(17),
    ResetQuestTimer(18),
    ;

    private final int val;

    QuestResultType(int val) {
        this.val = val;
    }

    public static QuestResultType getQuestTypeByVal(byte val) {
        return Arrays.stream(values())
                .filter(questRequestType -> questRequestType.getVal() == val)
                .findFirst()
                .orElse(null);
    }
}
