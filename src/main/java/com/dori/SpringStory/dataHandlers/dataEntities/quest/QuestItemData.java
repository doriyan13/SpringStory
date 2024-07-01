package com.dori.SpringStory.dataHandlers.dataEntities.quest;

import com.dori.SpringStory.enums.Job;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestItemData(int itemID,
                            int count,
                            int prop,
                            int gender,
                            int job,
                            int jobEx,
                            boolean resignRemove) {
    public boolean checkJob(int jobId) {
        final int jobFlag;
        if (jobId == Job.Evan.getId()) {
            jobFlag = 0x20000;
        } else {
            jobFlag = 1 << (jobId / 100);
        }
        final int jobExFlag = jobFlag & this.jobEx;
        return (jobExFlag | jobFlag & this.job) != 0 || jobId / 100 == 9;
    }

    public boolean checkGender(int gender) {
        return this.gender == 2 || this.gender == gender;
    }

    public boolean isRandom() {
        return prop > 0;
    }

    public boolean isStatic() {
        return prop == 0;
    }
}
