package com.dori.SpringStory.temporaryStats.characters;

import com.dori.SpringStory.enums.Job;
import com.dori.SpringStory.enums.Skills;
import com.dori.SpringStory.temporaryStats.TempStatValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobBuffData {
    private Job job;
    private Map<Skills, Set<BuffData>> buffs;

    public JobBuffData(Job job){
        this.job = job;
        this.buffs = new HashMap<>();
    }
}
