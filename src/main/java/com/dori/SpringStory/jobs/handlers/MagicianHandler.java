package com.dori.SpringStory.jobs.handlers;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.character.Skill;
import com.dori.SpringStory.dataHandlers.dataEntities.SkillData;
import com.dori.SpringStory.enums.SkillStat;
import com.dori.SpringStory.enums.Skills;
import com.dori.SpringStory.jobs.JobHandler;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.FormulaCalcUtils;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MagicianHandler implements JobHandler {

    private static final Logger logger = new Logger(MagicianHandler.class);

    private static MagicianHandler instance;

    public static MagicianHandler getInstance() {
        if (instance == null) {
            instance = new MagicianHandler();
        }
        return instance;
    }

    public Integer getDmgAfterMagicGuardReduction(MapleChar chr,
                                               SkillData skillData,
                                               Skill skill,
                                               int dmg) {
        int percentOfMpToReduce = FormulaCalcUtils.calcValueFromFormula(skillData.getSkillStatInfo().get(SkillStat.x), skill.getCurrentLevel()) / 100;
        int mpToReduce = dmg * percentOfMpToReduce;

        if (chr.getMp() >= mpToReduce) {
            chr.modifyMp(-mpToReduce);
            return dmg * (1 - percentOfMpToReduce);
        }
        return null;
    }

    @Override
    public boolean handleSkill(MapleChar chr, SkillData skillData, int slv) {
        Skills skill = Skills.getSkillById(skillData.getSkillId());
        switch (skill) {

            default -> {
                logger.warning("The Skill: " + skillData.getSkillId() + ", isn't handle by the MagicianHandler!");
                return false;
            }
        }
        //return true;
    }
}
