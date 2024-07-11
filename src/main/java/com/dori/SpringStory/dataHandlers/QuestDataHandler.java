package com.dori.SpringStory.dataHandlers;

import com.dori.SpringStory.dataHandlers.dataEntities.quest.QuestData;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.QuestItemData;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.QuestMobData;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.QuestSkillData;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.act.*;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.check.*;
import com.dori.SpringStory.dataHandlers.wzData.*;
import com.dori.SpringStory.dataHandlers.wzData.property.WzListProperty;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.JsonUtils;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.WzUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static com.dori.SpringStory.constants.ServerConstants.*;

@Service
public class QuestDataHandler {
    // Logger -
    private static final Logger logger = new Logger(MapDataHandler.class);
    // Map Cache of all the quests -
    private static final Map<Integer, QuestData> questsData = new HashMap<>();

    public static Optional<QuestData> getQuestData(int questId) {
        return Optional.ofNullable(questsData.get(questId));
    }

    private static QuestItemData loadQuestItemData(@NotNull WzListProperty itemProp,
                                                   int defaultCount) {
        return new QuestItemData(
                WzUtils.getInteger(itemProp.get("id")),
                WzUtils.getInteger(itemProp.get("count"), defaultCount),
                WzUtils.getInteger(itemProp.get("prop"), 0),
                WzUtils.getInteger(itemProp.get("gender"), 2),
                WzUtils.getInteger(itemProp.get("job"), -1),
                WzUtils.getInteger(itemProp.get("jobEx"), -1),
                WzUtils.getInteger(itemProp.get("resignRemove"), 0) != 0
        );
    }

    private static Set<QuestItemData> loadQuestItemsData(@NotNull WzListProperty itemList,
                                                         int defaultCount) {
        Set<QuestItemData> items = new HashSet<>();
        for (var itemEntry : itemList.items().entrySet()) {
            if (!(itemEntry.getValue() instanceof WzListProperty itemProp)) {
                logger.error("Failed to resolve quest item list");
                return Set.of();
            }
            QuestItemData itemData = loadQuestItemData(itemProp, defaultCount);
            items.add(itemData);
        }
        return items;
    }

    private static List<QuestItemData> loadChoiceItemsData(@NotNull WzListProperty itemList) {
        List<QuestItemData> items = new ArrayList<>();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (!(itemList.get(String.valueOf(i)) instanceof WzListProperty itemProp)) {
                break;
            }
            if (WzUtils.getInteger(itemProp.get("prop"), 0) != -1) {
                continue;
            }
            QuestItemData itemData = loadQuestItemData(itemProp, 1);
            items.add(itemData);
        }
        return items;
    }

    private static Set<QuestSkillData> loadQuestSkillData(@NotNull WzListProperty skillList) {
        Set<QuestSkillData> skills = new HashSet<>();
        for (var skillEntry : skillList.items().entrySet()) {
            if (!(skillEntry.getValue() instanceof WzListProperty skillProp)) {
                logger.error("Failed to resolve quest skill list");
                return Set.of();
            }
            if (!(skillProp.get("job") instanceof WzListProperty jobList)) {
                logger.error("Failed to resolve quest skill job list");
                return Set.of();
            }
            Set<Integer> jobs = new HashSet<>();
            for (var jobEntry : jobList.items().entrySet()) {
                jobs.add(WzUtils.getInteger(jobEntry.getValue()));
            }
            skills.add(new QuestSkillData(
                    WzUtils.getInteger(skillProp.get("id")),
                    WzUtils.getInteger(skillProp.get("skillLevel")),
                    WzUtils.getInteger(skillProp.get("masterLevel")),
                    Collections.unmodifiableSet(jobs)
            ));
        }
        return skills;
    }

    private static List<QuestMobData> loadQuestMobData(@NotNull WzListProperty mobList) {
        List<QuestMobData> mobs = new ArrayList<>();
        for (var mobEntry : mobList.items().entrySet()) {
            if (!(mobEntry.getValue() instanceof WzListProperty mobProp)) {
                logger.error("Failed to resolve quest mob list");
                return null;
            }
            int order = Integer.parseInt(mobEntry.getKey());
            int mobId = WzUtils.getInteger(mobProp.get("id"));
            int count = WzUtils.getInteger(mobProp.get("count"));
            QuestMobData mobData = new QuestMobData(order, mobId, count);
            mobs.add(mobData);
        }
        for (int i = 0; i < mobs.size() - 1; i++) {
            assert (mobs.get(i).order() < mobs.get(i + 1).order());
        }
        return mobs;
    }

    private static QuestSkillAct loadQuestSkillAct(@NotNull WzListProperty skillList) {
        Set<QuestSkillData> skills = loadQuestSkillData(skillList);
        return new QuestSkillAct(
                Collections.unmodifiableSet(skills)
        );
    }

    private static QuestItemAct loadQuestItemAct(int questId,
                                                 WzListProperty itemList) {
        Set<QuestItemData> items = loadQuestItemsData(itemList, -1);
        List<QuestItemData> choices = loadChoiceItemsData(itemList);
        return new QuestItemAct(
                questId,
                Collections.unmodifiableSet(items),
                Collections.unmodifiableList(choices)
        );
    }

    private static Set<QuestAct> loadQuestActs(int questId,
                                               @NotNull WzListProperty actProps) {
        final Set<QuestAct> questActs = new HashSet<>();
        for (var entry : actProps.items().entrySet()) {
            String actType = entry.getKey();
            switch (actType) {
                case "item" -> {
                    if (!(entry.getValue() instanceof WzListProperty itemList)) {
                        logger.error("Failed to resolve quest act item list");
                        return null;
                    }
                    questActs.add(loadQuestItemAct(questId, itemList));
                }
                case "money" -> questActs.add(new QuestMoneyAct(WzUtils.getInteger(entry.getValue())));
                case "exp" -> questActs.add(new QuestExpAct(WzUtils.getInteger(entry.getValue())));
                case "pop" -> questActs.add(new QuestPopAct(WzUtils.getInteger(entry.getValue())));
                case "skill" -> {
                    if (!(entry.getValue() instanceof WzListProperty skillList)) {
                        logger.error("Failed to resolve quest act skill list");
                        return null;
                    }
                    if (questId == 6034) {
                        // What Moren Dropped
                        continue;
                    }
                    questActs.add(loadQuestSkillAct(skillList));
                }
                case "nextQuest" -> {
                    // handled in QuestInfo.from
                }
            }
        }
        return questActs;
    }

    private static QuestItemCheck loadQuestItemCheck(@NotNull WzListProperty itemList) {
        return new QuestItemCheck(
                Collections.unmodifiableSet(loadQuestItemsData(itemList, 0))
        );
    }

    private static QuestMobCheck loadQuestMobCheck(int questID,
                                                   @NotNull WzListProperty mobList) {
        return new QuestMobCheck(
                questID,
                Collections.unmodifiableList(Objects.requireNonNull(loadQuestMobData(mobList)))
        );
    }

    private static QuestJobCheck loadQuestJobCheck(@NotNull WzListProperty jobList) {
        Set<Integer> jobs = new HashSet<>();
        for (var jobEntry : jobList.items().entrySet()) {
            jobs.add(WzUtils.getInteger(jobEntry.getValue()));
        }
        return new QuestJobCheck(
                Collections.unmodifiableSet(jobs)
        );
    }

    private static QuestExCheck loadQuestExCheck(int questId,
                                                 @NotNull WzListProperty exList) {
        Set<String> allowedValues = new HashSet<>();
        for (var exEntry : exList.items().entrySet()) {
            if (!(exEntry.getValue() instanceof WzListProperty exProp)) {
                logger.error("Failed to resolve quest ex list");
                return null;
            }
            allowedValues.add(WzUtils.getString(exProp.get("value")));
        }
        return new QuestExCheck(questId, Collections.unmodifiableSet(allowedValues));
    }

    private static Set<QuestCheck> loadQuestChecks(int questId, WzListProperty checkProps) {
        Set<QuestCheck> questChecks = new HashSet<>();
        for (var entry : checkProps.items().entrySet()) {
            String checkType = entry.getKey();
            switch (checkType) {
                case "item" -> {
                    if (!(entry.getValue() instanceof WzListProperty itemList)) {
                        logger.error("Failed to resolve quest check item list");
                        return null;
                    }
                    questChecks.add(loadQuestItemCheck(itemList));
                }
                case "mob" -> {
                    if (!(entry.getValue() instanceof WzListProperty mobList)) {
                        logger.error("Failed to resolve quest check mob list");
                        return null;
                    }
                    questChecks.add(loadQuestMobCheck(questId, mobList));
                }
                case "job" -> {
                    if (!(entry.getValue() instanceof WzListProperty jobList)) {
                        logger.error("Failed to resolve quest check job list");
                        return null;
                    }
                    questChecks.add(loadQuestJobCheck(jobList));
                }
                case "lvmin", "lvmax" -> {
                    int level = WzUtils.getInteger(entry.getValue());
                    boolean isMinimum = checkType.equals("lvmin");
                    questChecks.add(new QuestLevelCheck(level, isMinimum));
                }
                case "infoex" -> {
                    int infoQuestId = WzUtils.getInteger(checkProps.get("infoNumber"), questId);
                    if (!(entry.getValue() instanceof WzListProperty exList)) {
                        logger.error("Failed to resolve quest check ex list");
                        return null;
                    }
                    questChecks.add(loadQuestExCheck(infoQuestId, exList));
                }
            }
        }
        return questChecks;
    }

    private static QuestData loadQuestData(int questId,
                                           @NotNull WzListProperty questInfo,
                                           @NotNull WzListProperty questAct,
                                           @NotNull WzListProperty questCheck) {
        boolean autoStart = false;
        boolean autoComplete = false;
        for (var infoEntry : questInfo.items().entrySet()) {
            switch (infoEntry.getKey()) {
                case "autoStart" -> {
                    autoStart = (int) infoEntry.getValue() != 0;
                }
                case "autoComplete" -> {
                    autoComplete = (int) infoEntry.getValue() != 0;
                }
            }
        }
        // extract nextQuest from Act.img/%d/1
        int nextQuest = WzUtils.getInteger(((WzListProperty) questAct.get("1")).items().get("nextQuest"), 0);
        return new QuestData(
                questId,
                nextQuest,
                autoStart,
                autoComplete,
                Collections.unmodifiableSet(Objects.requireNonNull(loadQuestActs(questId, questAct.get("0")))),
                Collections.unmodifiableSet(Objects.requireNonNull(loadQuestActs(questId, questAct.get("1")))),
                Collections.unmodifiableSet(Objects.requireNonNull(loadQuestChecks(questId, questCheck.get("0")))),
                Collections.unmodifiableSet(Objects.requireNonNull(loadQuestChecks(questId, questCheck.get("1"))))
        );
    }

    public static void loadImgFiles() {
        Path questInfoPath = Path.of(QUEST_IMG_DIR, QUEST_INFO_IMG);
        Path questActPath = Path.of(QUEST_IMG_DIR, QUEST_ACT_IMG);
        Path questCheckPath = Path.of(QUEST_IMG_DIR, QUEST_CHECK_IMG);
        WzImage infoImage = ImgReader.readImage(questInfoPath);
        WzImage actImage = ImgReader.readImage(questActPath);
        WzImage checkImage = ImgReader.readImage(questCheckPath);
        if (infoImage == null || actImage == null || checkImage == null) {
            logger.error("Failed to resolve quest Images!");
            return;
        }
        for (var entry : infoImage.getProperty().items().entrySet()) {
            int questId = Integer.parseInt(entry.getKey());
            if (!(entry.getValue() instanceof WzListProperty infoProp)) {
                logger.error("Failed to resolve quest info");
                return;
            }
            QuestData questData = loadQuestData(
                    questId,
                    infoProp,
                    actImage.getProperty().get(entry.getKey()),
                    checkImage.getProperty().get(entry.getKey())
            );
            questsData.put(questId, questData);
        }
    }

    private static void loadQuestData() {
        logger.startLoad("IMG", "Quest Data");
        long startTime = System.currentTimeMillis();
        loadImgFiles();
        logger.finishLoad(questsData.size(), "IMG", "Quest Data", ((System.currentTimeMillis() - startTime) / 1000.0));
    }

    private static void exportQuestsToJson() {
        logger.serverNotice("Start creating the JSONs for quests..");
        MapleUtils.makeDirIfAbsent(JSON_DIR);
        MapleUtils.makeDirIfAbsent(QUEST_JSON_DIR);
        questsData.values().forEach(quest -> JsonUtils.createJsonFile(quest, QUEST_JSON_DIR + quest.id() + ".json"));
        logger.serverNotice("~ Finished creating the quests JSON files! ~");
    }

    public static void loadJsonQuests() {
        long startTime = System.currentTimeMillis();
        File dir = new File(QUEST_JSON_DIR);
        File[] files = dir.listFiles();
        logger.startLoad("JSON", "Quest");
        if (files != null) {
            for (File file : files) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    QuestData quest = mapper.readValue(file, QuestData.class);
                    questsData.put(quest.id(), quest);
                } catch (Exception e) {
                    logger.error("Error occurred while trying to load the file: " + file.getName());
                    e.printStackTrace();
                }
            }
            logger.finishLoad(files.length, "JSON", "Quests Data", ((System.currentTimeMillis() - startTime) / 1000.0));
        } else {
            logger.error("Didn't found quests JSONs to load!");
        }
    }

    private static boolean isJsonDataExist() {
        File questDir = new File(QUEST_JSON_DIR);
        return questDir.exists();
    }

    public static void load() {
        if (isJsonDataExist()) {
            loadJsonQuests();
        } else {
            loadQuestData();
            exportQuestsToJson();
        }
    }

}
