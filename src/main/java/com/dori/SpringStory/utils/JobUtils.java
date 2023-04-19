package com.dori.SpringStory.utils;

import com.dori.SpringStory.enums.Job;
import org.springframework.stereotype.Component;

@Component
public interface JobUtils {

    static boolean isEvanJob(int job) {
        return job / 100 == 22 || job == Job.Evan.getId();
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

    static boolean isDualBlade(int job) {
        return job / 10 == 43;
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
