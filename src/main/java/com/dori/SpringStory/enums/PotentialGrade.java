package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum PotentialGrade {
    Normal(0),
    HiddenRare(1),
    HiddenEpic(2),
    HiddenUnique(3),
    HiddenLegendary(4),
    Rare(5),
    Epic(6),
    Unique(7),
    Legendary(8);

    private final int val;

    PotentialGrade(int val) {
        this.val = val;
    }

    public static boolean isHiddenPotential(PotentialGrade potentialGrade) {
        return potentialGrade == HiddenRare || potentialGrade == HiddenEpic || potentialGrade == HiddenUnique || potentialGrade == HiddenLegendary;
    }

    public static PotentialGrade getItemOptionPotentialGrade(int itemOptionID) {
        if (itemOptionID > 0 && itemOptionID < 10_000) {
            return Rare; // TODO: Secondary Rare - the lowest tier of rare (basically rlly bad stats) maybe will separate in the future
        } else if (itemOptionID > 10_000 && itemOptionID < 20_000) {
            return Rare;
        } else if (itemOptionID > 20_000 && itemOptionID < 30_000) {
            return Epic;
        } else if (itemOptionID > 30_000 && itemOptionID < 40_000) {
            return Unique;
        }
        return Normal;
    }

    public static PotentialGrade transformHiddenPotentialToRevealed(PotentialGrade potentialGrade) {
        return switch (potentialGrade) {
            case HiddenRare -> Rare;
            case HiddenEpic -> Epic;
            case HiddenUnique -> Unique;
            case HiddenLegendary -> Legendary;
            default -> potentialGrade;
        };
    }
}
