package com.dori.SpringStory.utils;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.Job;
import com.dori.SpringStory.enums.SkillStat;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.wzHandlers.SkillDataHandler;
import com.dori.SpringStory.wzHandlers.wzEntities.SkillData;
import org.springframework.stereotype.Component;

import static com.dori.SpringStory.enums.Skills.*;

@Component
public interface SkillUtils {
    static Logger logger = new Logger(SkillUtils.class);

    private static boolean isIgnoreMasterLevelForCommon(int nSkillID) {
        return nSkillID == CROSSBOWMASTER_MARKMAN_SHIP.getId()
                || nSkillID == CROSSBOWMASTER_ULTIMATE_STRAFE.getId()
                || nSkillID == NIGHTLORD_EXPERT_JAVELIN.getId()
                || nSkillID == SHADOWER_GRID.getId()
                || nSkillID == CAPTAIN_COUNTER_ATTACK.getId()
                || nSkillID == ARCHMAGE1_MASTER_MAGIC.getId()
                || nSkillID == HERO_COMBAT_MASTERY.getId()
                || nSkillID == PALADIN_BLESSING_ARMOR.getId()
                || nSkillID == DARKKNIGHT_BEHOLDERS_REVENGE.getId()
                || nSkillID == ARCHMAGE2_MASTER_MAGIC.getId()
                || nSkillID == BISHOP_MASTER_MAGIC.getId()
                || nSkillID == BOWMASTER_VENGEANCE.getId()
                || nSkillID == BOWMASTER_MARKMAN_SHIP.getId()
                || nSkillID == BMAGE_ENERGIZE.getId()
                || nSkillID == WILDHUNTER_WILD_INSTINCT.getId()
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
                    || skillId == EVAN_MAGIC_GUARD.getId()
                    || skillId == EVAN_MAGIC_BOOSTER.getId()
                    || skillId == EVAN_MAGIC_CRITICAL.getId();
        }
        if (nJob / 10 == 43) {
            return JobUtils.getJobLevel(nJob) == 4
                    || skillId == 4130004
                    || skillId == DUAL3_HUSTLE_DASH.getId()
                    || skillId == DUAL4_UPPER_STAB.getId()
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

    static boolean isAntiRepeatBuffSkill(int skillID) {
        return skillID == 1001003 || skillID == 1101006 || skillID == 11001001 || skillID == 1111007 || skillID == 11101003 ||
                skillID == 1121000 || skillID == 1201006 || skillID == 1211009 || skillID == 1211010 || skillID == 12101000 ||
                skillID == 12101001 || skillID == 1221000 || skillID == 1301006 || skillID == 1301007 || skillID == 1311007 ||
                skillID == 1321000 || skillID == 14101003 || skillID == 15111005 || skillID == 2101001 || skillID == 2101003 ||
                skillID == 21121000 || skillID == 2121000 || skillID == 2201001 || skillID == 2201003 || skillID == 22141003 ||
                skillID == 22171000 || skillID == 22181000 || skillID == 2221000 || skillID == 2301004 || skillID == 2311001 ||
                skillID == 2311003 || skillID == 2321000 || skillID == 2321005 || skillID == 3121000 || skillID == 3121002 ||
                skillID == 32111004 || skillID == 32121007 || skillID == 3221000 || skillID == 33121007 || skillID == 35111013 ||
                skillID == 4101004 || skillID == 4111001 || skillID == 4121000 || skillID == 4201003 || skillID == 4221000 ||
                skillID == 4311001 || skillID == 4341000 || skillID == 4341007 || skillID == 5111007 || skillID == 5121009 ||
                skillID == 5121000 || skillID == 5211007 || skillID == 5221000;
    }

    static void applySkillConsumptionToChar(int skillID, int slv, MapleChar chr){
        int amountToConsume = 0;
        SkillData skillData = SkillDataHandler.getSkillDataByID(skillID);
        if (skillData != null) {
            // wz base handling for the skill -
            String mpConsumptionFormula = skillData.getSkillStatInfo().getOrDefault(SkillStat.mpCon, "");

            if (skillData.getMpCostByLevel().isEmpty() && !mpConsumptionFormula.isEmpty()) {
                amountToConsume = FormulaCalcUtils.calcValueFromFormula(mpConsumptionFormula, slv);
            } else if (!skillData.getMpCostByLevel().isEmpty()) {
                amountToConsume = skillData.getMpCostByLevel().getOrDefault(slv, 0);
            } else {
                // TODO: here i will need to manage other type of consume - HP / Meso and such! | or maybe customSkill handling?
                logger.error("Cannot clac mpConsume / mpCostByLvl for this skill -" + skillID);
            }
        }
        if(amountToConsume != 0){
            chr.modifyMp(-amountToConsume);
        }
    }
}
