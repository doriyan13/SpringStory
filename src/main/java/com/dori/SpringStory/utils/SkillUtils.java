package com.dori.SpringStory.utils;

import com.dori.SpringStory.enums.Job;
import org.springframework.stereotype.Component;

@Component
public interface SkillUtils {

    static boolean isIgnoreMasterLevelForCommon(int nSkillID)
    {
        boolean retVal;

        if (nSkillID > 3220010)
        {
            if (nSkillID <= 5220012)
            {
                if (nSkillID == 5220012 || nSkillID == 4120010 || nSkillID == 4220009)
                    return true;
                retVal = nSkillID == 5120011;
                return retVal;
            }
            if (nSkillID != 32120009)
            {
                retVal = nSkillID == 33120010;
                return retVal;
            }
            return true;
        }
        if (nSkillID >= 3220009)
            return true;
        if (nSkillID > 2120009)
        {
            if (nSkillID > 2320010)
            {
                return nSkillID >= 3120010 && nSkillID <= 3120011;
            }
            else if (nSkillID != 2320010)
            {
                retVal = nSkillID == 2220009;
                return retVal;
            }
            return true;
        }
        if (nSkillID == 2120009 || nSkillID == 1120012 || nSkillID == 1220013)
            return true;

        return nSkillID == 1320011;
    }

    static boolean isSkillNeedMasterLevel(int skillId) {
        // TODO: need to refactor this all part (currently it's raw ida stuff!)
        if(isIgnoreMasterLevelForCommon(skillId)){
            return false;
        }
        int nJob = skillId / 10_000;
        if(skillId / 10_000 / 100 == 22 || nJob == Job.Evan.getId()){
            int jobLvl = JobUtils.getJobLevel(nJob);
            return jobLvl == 9
                    || jobLvl == 10
                    || skillId == 22111001
                    || skillId == 22141002
                    || skillId == 22140000 ;
        }
        if(nJob / 10 == 43){
            return JobUtils.getJobLevel(nJob) == 4
                    || skillId == 4311003
                    || skillId == 4321000
                    || skillId == 4331002
                    || skillId == 4331005 ;
        }
        if(nJob == 100 * skillId / 10_000 / 100){
            return false;
        }
        return nJob % 10 == 2;
    }


}
