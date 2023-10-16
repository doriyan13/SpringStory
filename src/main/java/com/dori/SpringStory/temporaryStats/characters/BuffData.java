package com.dori.SpringStory.temporaryStats.characters;

import com.dori.SpringStory.enums.SkillConsumeStatType;
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
    private boolean additionalValue; // is it being added to the base value? for example base speed + the value here
    private String durationInSecFormula;
    private int cooldownInSec;
    private int intervalInSec;
    private SkillConsumeStatType consumeStatType;
}
