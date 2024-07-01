package com.dori.SpringStory.temporaryStats.characters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TempStatCollection<T> {
    private Map<T, TempStatData> stats = new ConcurrentHashMap<>();

    public Set<T> markAndGetStatsToRemoveById(int id) {
        return stats
                .entrySet()
                .stream()
                .filter(tempStatEntry -> {
                    tempStatEntry.getValue().removeSkillStats(id);
                    if (tempStatEntry.getValue().getDataDistribution().isEmpty()) {
                        tempStatEntry.getValue().setDeleted(true);
                        return true;
                    }
                    return false;
                }).map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public int getStat(T stat) {
        return stats.get(stat) != null ? stats.get(stat).getTotal() : 0;
    }

    public TempStatData getStatData(T stat) {
        return stats.get(stat);
    }

    public boolean hasStat(T stat) {
        return stats.containsKey(stat);
    }

    public void addStat(T cts,
                        int id,
                        int value) {
        if (!stats.containsKey(cts)) {
            stats.put(cts, new TempStatData());
        }
        stats.get(cts).addStat(id, value);
    }

    public void removeStatById(int id) {
        stats.keySet().removeAll(markAndGetStatsToRemoveById(id));
    }

    public void removeStats(Set<T> statsToRemove) {
        stats.keySet().removeAll(statsToRemove);
    }

    public void applyModifiedStats() {
        stats.values().forEach(statData -> statData.setModified(false));
    }
}
