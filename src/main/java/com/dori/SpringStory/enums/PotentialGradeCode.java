package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum PotentialGradeCode {
    Normal(0),
    HiddenRare(1),
    HiddenEpic(2),
    HiddenUnique(3),
    HiddenLegendary(4),
    Rare(5),
    Epic(6),
    Unique(7),
    Legendary(8)
    ;

    private final int val;

    PotentialGradeCode(int val) {
        this.val = val;
    }

    public static boolean isHiddenPotential(PotentialGradeCode potentialGrade) {
        return potentialGrade == HiddenRare || potentialGrade == HiddenEpic || potentialGrade == HiddenUnique || potentialGrade == HiddenLegendary;
    }
}
