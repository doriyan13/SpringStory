package com.dori.SpringStory.jobs.handlers;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.dataHandlers.dataEntities.SkillData;
import com.dori.SpringStory.enums.Skills;
import com.dori.SpringStory.jobs.JobHandler;
import com.dori.SpringStory.logger.Logger;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ThiefHandler implements JobHandler {

    private static final Logger logger = new Logger(ThiefHandler.class);

    private static ThiefHandler instance;

    public static ThiefHandler getInstance() {
        if (instance == null) {
            instance = new ThiefHandler();
        }
        return instance;
    }

    @Override
    public boolean handleSkill(MapleChar chr, SkillData skillData, int slv) {
        //TODO: need to handle!
        return false;
    }
}
