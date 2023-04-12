package com.dori.Dori90v.enums;

import java.util.Arrays;

public enum JobType {
    Cygnus(0),
    Adventurer(1),
    Aran(2),
    Evan(3);

    private final int val;

    JobType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static JobType getTypeByVal(int val){
        return Arrays.stream(JobType.values()).filter(jobType -> jobType.getVal() == val).findFirst().orElse(Adventurer);
    }

    public Job getStartJobByType(){
        Job job = Job.Beginner;
        switch (this){
            case Cygnus -> job = Job.Noblesse;
            case Aran -> job = Job.Legend;
            case Evan -> job = Job.Evan;
        }
        return job;
    }
}
