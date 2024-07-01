package com.dori.SpringStory.temporaryStats.characters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TempStatData {
    private Map<Integer, Integer> dataDistribution = new ConcurrentHashMap<>();
    private int total;
    private boolean modified = true;
    private boolean deleted = false;

    public void addStat(int id, int value) {
        int skillStatValue = dataDistribution.getOrDefault(id, 0);
        dataDistribution.put(id, value);
        if(skillStatValue != 0) {
            total -= skillStatValue;
        }
        total += value;
        modified = true;
    }

    public void removeSkillStats(int skillID) {
        Integer valueToDeduct = dataDistribution.remove(skillID);
        if (valueToDeduct != null) {
            total -= valueToDeduct;
            modified = true;
        }
    }
}
