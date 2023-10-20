package com.dori.SpringStory.temporaryStats.characters;

import com.dori.SpringStory.enums.Job;
import com.dori.SpringStory.enums.Skills;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.JsonUtils;
import com.dori.SpringStory.utils.MapleUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.dori.SpringStory.constants.ServerConstants.*;
import static com.dori.SpringStory.enums.Skills.NONE;
import static com.dori.SpringStory.temporaryStats.characters.BuffsDataLoader.loadCustomBuffsData;

@Component
public class BuffDataHandler {
    private static final Logger logger = new Logger(BuffDataHandler.class);

    private static final HashMap<Job, JobBuffData> buffsDataByJob = new HashMap<>();

    public static void addBuff(Job job, int skillID, BuffData buffData) {
        if (!buffsDataByJob.containsKey(job)) {
            buffsDataByJob.put(job, new JobBuffData(job));
        }
        Skills skill = Skills.getSkillById(skillID);
        if (skill != NONE) {
            Skills skills = Skills.getSkillById(skillID);
            buffsDataByJob.get(job).getBuffs().computeIfAbsent(skills, k -> new HashSet<>());
            buffsDataByJob.get(job).getBuffs().get(skills).add(buffData);
        }
    }

    public static Set<BuffData> getBuffsByJobAndSkillID(Job job, int skillID) {
        Skills skill = Skills.getSkillById(skillID);
        // always need to check beginner if it's a common skill
        Set<BuffData> buffData = buffsDataByJob.get(Job.Beginner).getBuffs().get(skill);
        if (buffData != null) {
            return buffData;
        }
        // Skill of your current job -
        JobBuffData jobBuffData = buffsDataByJob.get(job);
        return jobBuffData != null ?
                jobBuffData.getBuffs().get(skill)
                : null;
    }

    public static void loadBuffsData() {
        loadCustomBuffsData();
    }

    private static void exportBuffsToJson() {
        logger.serverNotice("Start creating the JSONs for items..");
        MapleUtils.makeDirIfAbsent(JSON_DIR);
        MapleUtils.makeDirIfAbsent(ITEM_JSON_DIR);
        buffsDataByJob.values().forEach(jobBuffData -> JsonUtils.createJsonFile(jobBuffData, BUFF_JSON_DIR + jobBuffData.getJob().name() + ".json"));
        logger.serverNotice("~ Finished creating the items JSON files! ~");
    }
}
