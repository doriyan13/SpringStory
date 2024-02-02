package com.dori.SpringStory.temporaryStats.characters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * For manual handled buffs :D
 */
public class BuffData {
    private CharacterTemporaryStat tempStat;
    private String calcFormula;
    private String durationInSecFormula;
    private int cooldownInSec;
    private int intervalInSec;
    private boolean health;
}
