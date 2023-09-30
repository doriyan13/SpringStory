package com.dori.SpringStory.temporaryStats.characters;

import com.dori.SpringStory.client.character.Skill;
import com.dori.SpringStory.enums.Job;
import com.dori.SpringStory.enums.Skills;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.temporaryStats.TempStatValue;
import com.dori.SpringStory.utils.JsonUtils;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.wzHandlers.SkillDataHandler;
import com.dori.SpringStory.wzHandlers.wzEntities.SkillData;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static com.dori.SpringStory.constants.ServerConstants.*;
import static com.dori.SpringStory.enums.Skills.NONE;

@Component
public class BuffDataHandler {
    private static final Logger logger = new Logger(BuffDataHandler.class);

    private static final HashMap<Job, JobBuffData> buffsDataByJob = new HashMap<>();

    public static void addBuff(Job job, int skillID, TempStatValue tempStatValue) {
        if(!buffsDataByJob.containsKey(job)){
            buffsDataByJob.put(job, new JobBuffData(job));
        }
        Skills skill = Skills.getSkillById(skillID);
        if(skill != NONE) {
            buffsDataByJob.get(job).getBuffs().put(Skills.getSkillById(skillID), tempStatValue);
        }
    }

    private static void loadBuffsData(){

    }

    private static void exportBuffsToJson() {
        logger.serverNotice("Start creating the JSONs for items..");
        MapleUtils.makeDirIfAbsent(JSON_DIR);
        MapleUtils.makeDirIfAbsent(ITEM_JSON_DIR);
        buffsDataByJob.values().forEach(jobBuffData -> JsonUtils.createJsonFile(jobBuffData, BUFF_JSON_DIR + jobBuffData.getJob().name() + ".json"));
        logger.serverNotice("~ Finished creating the items JSON files! ~");
    }
}
