package com.dori.SpringStory.utils;

import com.dori.SpringStory.enums.Job;

public interface FieldUtils {

    static int getStartingFieldByJob(int jobID) {
        int startingField = 10_000; // Maple Road - Mushroom Park
        Job job = Job.getJobById(jobID);
        switch (job) {
            case Citizen -> startingField = 310010000; // resistance headquarters
            case Noblesse -> startingField = 130010220; // kiridu's hatchery
            case Legend -> startingField = 140090000; // Snow Island: Ice Cave
            case Evan -> startingField = 100030100; // Utah's House: Small Attic
        }
        return startingField;
    }
}
