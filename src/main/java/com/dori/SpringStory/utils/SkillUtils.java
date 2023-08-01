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

    static boolean isKeyDownSkill(int skillId) {
        return skillId == 2321001 || skillId == 80001836 || skillId == 37121052 || skillId == 36121000 ||
                skillId == 37121003 || skillId == 36101001 || skillId == 33121114 || skillId == 33121214 ||
                skillId == 35121015 || skillId == 33121009 || skillId == 32121003 || skillId == 31211001 ||
                skillId == 31111005 || skillId == 30021238 || skillId == 31001000 || skillId == 31101000 ||
                skillId == 80001887 || skillId == 80001880 || skillId == 80001629 || skillId == 20041226 ||
                skillId == 60011216 || skillId == 65121003 || skillId == 80001587 || skillId == 131001008 ||
                skillId == 142111010 || skillId == 131001004 || skillId == 95001001 || skillId == 101110100 ||
                skillId == 101110101 || skillId == 101110102 || skillId == 27111100 || skillId == 12121054 ||
                skillId == 11121052 || skillId == 11121055 || skillId == 5311002 || skillId == 4341002 ||
                skillId == 5221004 || skillId == 5221022 || skillId == 3121020 || skillId == 3101008 ||
                skillId == 3111013 || skillId == 1311011 || skillId == 2221011 || skillId == 2221052 ||
                skillId == 25121030 || skillId == 27101202 || skillId == 25111005 || skillId == 23121000 ||
                skillId == 22171083 || skillId == 14121004 || skillId == 13111020 || skillId == 13121001 ||
                skillId == 14111006 || (skillId >= 80001389 && skillId <= 80001392) || skillId == 42121000 ||
                skillId == 42120003 || skillId == 5700010 || skillId == 5711021 || skillId == 5721001 ||
                skillId == 5721061 || skillId == 21120018 || skillId == 21120019 || skillId == 24121000 ||
                skillId == 24121005;
    }

    static boolean isShikigamiHauntingSkill(int skillID) {
        return switch (skillID) {
            case 80001850, 42001000, 42001005, 42001006, 40021185, 80011067 -> true;
            default -> false;
        };
    }
}
