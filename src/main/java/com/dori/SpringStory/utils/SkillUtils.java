package com.dori.SpringStory.utils;

import com.dori.SpringStory.enums.Job;
import org.springframework.stereotype.Component;

@Component
public interface SkillUtils {

    private static boolean isIgnoreMasterLevelForCommon(int nSkillID) {
        return nSkillID == 3220009
                || nSkillID == 3220010
                || nSkillID == 4120010
                || nSkillID == 4220009
                || nSkillID == 5220012
                || nSkillID == 2120009
                || nSkillID == 1120012
                || nSkillID == 1220013
                || nSkillID == 1320011
                || nSkillID == 2220009
                || nSkillID == 2320010
                || nSkillID == 3120010
                || nSkillID == 3120011
                || nSkillID == 32120009
                || nSkillID == 33120010
                ;
    }

    static boolean isSkillNeedMasterLevel(int skillId) {
        if (isIgnoreMasterLevelForCommon(skillId)) {
            return false;
        }
        int nJob = skillId / 10_000;

        if (nJob / 100 == 22 || nJob == Job.Evan.getId()) {
            int jobLvl = JobUtils.getJobLevel(nJob);
            return jobLvl == 9 || jobLvl == 10
                    || skillId == 22111001
                    || skillId == 22141002
                    || skillId == 22140000;
        }
        if (nJob / 10 == 43) {
            return JobUtils.getJobLevel(nJob) == 4
                    || skillId == 4130004
                    || skillId == 4321000
                    || skillId == 4331004
                    || skillId == 4331006;
        }
        return nJob % 10 == 2;
    }
}
