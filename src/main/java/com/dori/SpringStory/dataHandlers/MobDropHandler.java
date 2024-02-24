package com.dori.SpringStory.dataHandlers;

import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.dataHandlers.dataEntities.MobDropData;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.JsonUtils;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.XMLApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;

import static com.dori.SpringStory.constants.ServerConstants.*;

@Service
public class MobDropHandler {
    private static final Logger logger = new Logger(MobDropHandler.class);
    private static final Map<Integer, List<MobDropData>> dropByMobsId = new HashMap<>();

    public static List<MobDropData> getDropsByMobID(int mobID) {
        List<MobDropData> drops = dropByMobsId.get(mobID);
        return drops != null ? new ArrayList<>(drops) : new ArrayList<>();
    }

    private static void loadCosmicAndBmsDefaultDrops(Set<MobDropData> fullListOfMobDrops) {
        MapleUtils.makeDirIfAbsent(MOB_DROP_JSON_DIR);
        File file = new File(MOB_DROP_JSON_DIR + "mobDrops.json");
        logger.serverNotice("Start loading the JSON of mob drops..");
        if (file.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<MobDropData> mobDrops = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, MobDropData.class));
                for (MobDropData mobDrop : mobDrops) {
                    if (mobDrop.getQuantity() != 0 && !mobDrop.isMoney()) {
                        if (!dropByMobsId.containsKey(mobDrop.getMobId())) {
                            dropByMobsId.put(mobDrop.getMobId(), new ArrayList<>());
                        }
                        dropByMobsId.get(mobDrop.getMobId()).add(mobDrop);
                        fullListOfMobDrops.add(mobDrop);
                    }
                }
            } catch (Exception e) {
                logger.error("Error occurred while trying to load the file: " + file.getName());
                e.printStackTrace();
            }
        } else {
            logger.error("Didn't found MobDrops JSON to load!");
        }
    }

    public static void loadJsonDrops() {
        Set<MobDropData> fullListOfMobDrops = new HashSet<>();
        long startTime = System.currentTimeMillis();
        loadCosmicAndBmsDefaultDrops(fullListOfMobDrops);
        loadMonsterBookMobsDropData(fullListOfMobDrops);
        exportSkillsToJson(fullListOfMobDrops);
        logger.serverNotice("~ Finished loading MobDrops JSON file! in: " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
    }

    private static void handleMonsterBookNode(Node mainNode, Set<MobDropData> mobDropsData) {
        Node mobDropsNode = XMLApi.getFirstChildByNameBF(mainNode, "reward");
        if (mobDropsNode != null) {
            int mobID = Integer.parseInt(XMLApi.getNamedAttribute(mainNode, "name"));
            for (Node mobDropNode : XMLApi.getAllChildren(mobDropsNode)) {
                int itemID = Integer.parseInt(XMLApi.getNamedAttribute(mobDropNode, "value"));
                List<MobDropData> mobDrops = dropByMobsId.get(mobID);
                if (mobDrops == null || mobDrops.stream().noneMatch(drop -> drop.getItemId() == itemID)) {
                    mobDropsData.add(new MobDropData(mobID, itemID, MOB_DROP_DEFAULT_MIN_QUANTITY, MOB_DROP_DEFAULT_MAX_QUANTITY, MOB_DROP_DEFAULT_CHANCE));
                }
            }
        }
    }

    public static void loadMonsterBookMobsDropData(Set<MobDropData> mobDropData) {
        File file = new File(ServerConstants.STRING_WZ_DIR + "MonsterBook.img.xml");
        Node node = XMLApi.getRoot(file);
        XMLApi.getAllChildren(node.getFirstChild()).forEach(monsterBookNode -> handleMonsterBookNode(monsterBookNode, mobDropData));
    }

    public static void loadFullMobDropJson() {
        long startTime = System.currentTimeMillis();
        MapleUtils.makeDirIfAbsent(MOB_DROP_JSON_DIR);
        File file = new File(MOB_DROP_JSON_DIR + "fullMobDrops.json");
        logger.serverNotice("Start loading the full JSON of mob drops..");
        if (file.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Set<MobDropData> mobDrops = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(Set.class, MobDropData.class));
                for (MobDropData mobDrop : mobDrops) {
                    if (mobDrop.getQuantity() != 0 && !mobDrop.isMoney()) {
                        if (!dropByMobsId.containsKey(mobDrop.getMobId())) {
                            dropByMobsId.put(mobDrop.getMobId(), new ArrayList<>());
                        }
                        dropByMobsId.get(mobDrop.getMobId()).add(mobDrop);
                    }
                }
            } catch (Exception e) {
                logger.error("Error occurred while trying to load the file: " + file.getName());
                e.printStackTrace();
            }
            logger.serverNotice("~ Finished loading " + dropByMobsId.size() + " MobsDrops JSON file! in: " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
        } else {
            logger.error("Didn't found FullMobDrops JSON to load!");
        }
    }

    private static void exportSkillsToJson(Set<MobDropData> fullListOfMobDrops) {
        logger.serverNotice("Start creating the JSON for Mob Drops..");
        MapleUtils.makeDirIfAbsent(JSON_DIR);
        MapleUtils.makeDirIfAbsent(MOB_DROP_JSON_DIR);
        JsonUtils.createJsonFile(fullListOfMobDrops, MOB_DROP_JSON_DIR + "fullMobDrops.json");
        logger.serverNotice("~ Finished creating the Mob Drops JSON files! ~");
    }

    private static boolean isJsonDataExist() {
        File skillDir = new File(MOB_DROP_JSON_DIR + "fullMobDrops.json");
        return skillDir.exists();
    }

    public static void load() {
        if (isJsonDataExist()) {
            loadFullMobDropJson();
        } else {
            loadJsonDrops();
        }
    }

}
