package com.dori.SpringStory.client.character.temporaryStats;

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

    public void addSkillStats(int skillID, int statValue) {
        int skillStatValue = skillsDataDistribution.getOrDefault(skillID, 0);
        skillsDataDistribution.put(skillID, statValue);
        skillStatValue -= statValue;
        total += skillStatValue;
    }

    public void removeSkillStats(int skillID) {
        Integer valueToDeduct = skillsDataDistribution.remove(skillID);
        if (valueToDeduct != null) {
            total -= valueToDeduct;
        }
    }
}
