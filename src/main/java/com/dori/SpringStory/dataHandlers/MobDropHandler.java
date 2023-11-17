package com.dori.SpringStory.dataHandlers;

import com.dori.SpringStory.dataHandlers.dataEntities.MobDropData;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.MapleUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

import static com.dori.SpringStory.constants.ServerConstants.MOB_DROP_JSON_DIR;

@Service
public class MobDropHandler {
    private static final Logger logger = new Logger(MobDropHandler.class);
    private static final Map<Integer, Set<MobDropData>> dropByMobsId = new HashMap<>();

    public static void loadJsonDrops() {
        long startTime = System.currentTimeMillis();
        MapleUtils.makeDirIfAbsent(MOB_DROP_JSON_DIR);
        File file = new File(MOB_DROP_JSON_DIR + "mobDrops.json");
        logger.serverNotice("Start loading the JSON of mob drops..");
        if (file.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<MobDropData> mobDrops = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, MobDropData.class));
                for (MobDropData mobDrop : mobDrops) {
                    if (mobDrop.getQuantity() != 0) {
                        if (!dropByMobsId.containsKey(mobDrop.getMobId())) {
                            dropByMobsId.put(mobDrop.getMobId(), new HashSet<>());
                        }
                        dropByMobsId.get(mobDrop.getMobId()).add(mobDrop);
                    }
                }
            } catch (Exception e) {
                logger.error("Error occurred while trying to load the file: " + file.getName());
                e.printStackTrace();
            }
            logger.serverNotice("~ Finished loading MobDrops JSON file! in: " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
        } else {
            logger.error("Didn't found MobDrops JSON to load!");
        }
    }

    public static Set<MobDropData> getDropsByMobID(int mobID) {
        return dropByMobsId.get(mobID);
    }
}
