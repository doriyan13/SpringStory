package com.dori.SpringStory.temporaryStats.characters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * For manual handled buffs :D
 */
public class BuffData {
    private String calcFormula;
    private List<CharacterTemporaryStat> tempStats;
}
