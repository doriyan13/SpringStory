package com.dori.SpringStory.jobs;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.Job;
import com.dori.SpringStory.jobs.handlers.WarriorHandler;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.dataHandlers.dataEntities.SkillData;

public interface JobHandler {

    Logger logger = new Logger(JobHandler.class);

    boolean handleSkill(MapleChar chr, SkillData skillData, int slv);

    static JobHandler getHandlerByJobID(int jobID) {
        Job job = Job.getJobById(jobID);
        return switch (job) {
            case Warrior,
                    Fighter, Crusader, Hero,
                    Page, WhiteKnight, Paladin,
                    Spearman, DragonKnight, DarkKnight -> WarriorHandler.getInstance();
            default -> null;
        };
    }
}
