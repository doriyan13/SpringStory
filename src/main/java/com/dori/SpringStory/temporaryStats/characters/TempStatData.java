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
    private Map<Integer, Integer> skillsDataDistribution = new ConcurrentHashMap<>();
    private int total;
    private boolean modified = true;
    private boolean deleted = false;

    public void addSkillStats(int skillID, int statValue) {
        int skillStatValue = skillsDataDistribution.getOrDefault(skillID, 0);
        skillsDataDistribution.put(skillID, statValue);
        skillStatValue -= statValue;
        total += Math.abs(skillStatValue);
        modified = true;
    }

    public void removeSkillStats(int skillID) {
        Integer valueToDeduct = skillsDataDistribution.remove(skillID);
        if (valueToDeduct != null) {
            total -= valueToDeduct;
            modified = true;
        }
    }
}
