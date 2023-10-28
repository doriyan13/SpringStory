package com.dori.SpringStory.jobs.handlers;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.SkillStat;
import com.dori.SpringStory.enums.Skills;
import com.dori.SpringStory.jobs.JobHandler;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.FormulaCalcUtils;
import com.dori.SpringStory.wzHandlers.wzEntities.SkillData;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WarriorHandler implements JobHandler {
    private static final Logger logger = new Logger(WarriorHandler.class);

    private static WarriorHandler instance;

    public static WarriorHandler getInstance(){
        if (instance == null) {
            instance = new WarriorHandler();
        }
        return instance;
    }

    private void handleHpRecovery(MapleChar chr, SkillData skillData, int slv) {
        int percentageToHeal = FormulaCalcUtils.calcValueFromFormula(skillData.getSkillStatInfo().get(SkillStat.x), slv);
        int amountToHeal = chr.getMaxHp() * percentageToHeal / 100;
        chr.modifyHp(amountToHeal);
    }

    @Override
    public boolean handleSkill(MapleChar chr, SkillData skillData, int slv) {
        Skills skill = Skills.getSkillById(skillData.getSkillId());
        switch (skill) {
            case WHITE_KNIGHT_HP_RECOVERY -> handleHpRecovery(chr, skillData, slv);
            default -> {
                logger.warning("The Skill: " + skillData.getSkillId() + ", isn't handle by the WarriorHandler!");
                return false;
            }
        }
        return true;
    }
}
