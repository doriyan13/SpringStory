package com.dori.SpringStory.utils;

import com.dori.SpringStory.enums.Job;
import org.springframework.stereotype.Component;

@Component
public interface JobUtils {

    static boolean isEvanJob(int job) {
        return job / 100 == 22 || job == Job.Evan.getId();
    }

    static boolean isDualBlade(int job) {
        return job / 10 == 43;
    }

    static boolean isHero(int job) {
        return job / 10 == 11;
    }

    static boolean isPaladin(int job) {
        return job / 10 == 12;
    }

    static boolean isDarkKnight(int job) {
        return job / 10 == 13;
    }

    static boolean isFirePoison(int job) {
        return job / 10 == 21;
    }

    static boolean isIceLightning(int job) {
        return job / 10 == 22;
    }

    static boolean isBishop(int job) {
        return job / 10 == 23;
    }

    static boolean isBowmaster(int job) {
        return job / 10 == 31;
    }

    static boolean isMarksman(int job) {
        return job / 10 == 32;
    }

    static boolean isNightLord(int job) {
        return job / 10 == 41;
    }

    static boolean isShadower(int job) {
        return job / 10 == 42;
    }

    static boolean isBuccaneer(int job) {
        return job / 10 == 51;
    }

    static boolean isCorsair(int job) {
        return job / 10 == 52;
    }

    static boolean isDawnWarrior(int job) {
        return job / 100 == 11;
    }

    static boolean isBlazeWizard(int job) {
        return job / 100 == 12;
    }

    static boolean isWindArcher(int job) {
        return job / 100 == 13;
    }

    static boolean isNightWalker(int job) {
        return job / 100 == 14;
    }

    static boolean isThunderBreaker(int job) {
        return job / 100 == 15;
    }

    static boolean isAran(int job) {
        return job / 100 == 21
                || job == Job.Legend.getId();
    }

    static boolean isBattleMage(int job) {
        return job / 100 == 32;
    }

    static boolean isWildHunter(int job) {
        return job / 100 == 33;
    }

    static boolean isMechanic(int job) {
        return job / 100 == 35;
    }

    private static int getEvanJobLevel(int jobId) {
        return switch (jobId) {
            case 2200, 2210 -> 1;
            case 2211, 2212, 2213 -> 2;
            case 2214, 2215, 2216 -> 3;
            case 2217, 2218 -> 4;
            default -> 0;
        };
    }

    private static int getDualBladePrefix(int jobId) {
        return (jobId - 430) / 2;
    }

    static int getJobLevel(int jobId) {
        // Re-did this part base on ida - v95 :D
        int prefix;
        int nType;
        if (!(jobId % 100 == 0) || jobId == Job.Evan.getId()) {
            return 1;
        }
        if (isDualBlade(jobId)) {
            prefix = getDualBladePrefix(jobId);
        } else {
            prefix = jobId % 10;
        }
        nType = prefix + 2;

        return (nType >= 2) && (prefix <= 4 || (nType <= 10 && isEvanJob(jobId)))
                ? nType : 0;
    }

}
