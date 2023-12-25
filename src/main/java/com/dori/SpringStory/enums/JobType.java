package com.dori.SpringStory.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum JobType {
    Resistance(0),
    Adventurer(1),
    Cygnus(2),
    Aran(3),
    Evan(4);

    private final int val;

    JobType(int val) {
        this.val = val;
    }

    public static JobType getTypeByVal(int val){
        return Arrays.stream(JobType.values()).filter(jobType -> jobType.getVal() == val).findFirst().orElse(Adventurer);
    }

    public Job getStartJobByType(){
        Job job = Job.Beginner;
        switch (this){
            case Resistance -> job = Job.Citizen;
            case Cygnus -> job = Job.Noblesse;
            case Aran -> job = Job.Legend;
            case Evan -> job = Job.Evan;
        }
        return job;
    }
}
