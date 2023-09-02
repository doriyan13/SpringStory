package com.dori.SpringStory.wzHandlers;

import com.dori.SpringStory.client.character.Skill;
import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.enums.SkillStat;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.JsonUtils;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.XMLApi;
import com.dori.SpringStory.utils.utilEntities.Rect;
import com.dori.SpringStory.wzHandlers.wzEntities.SkillData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.dori.SpringStory.constants.ServerConstants.*;

@Service
public class SkillDataHandler {
    // Logger -
    private static final Logger logger = new Logger(MapDataHandler.class);
    // Map Cache of all the maps -
    private static final Map<Integer, SkillData> skills = new LinkedHashMap<>();
    // TODO: need to handle MobSkillInfo!

    public static Skill getSkillByID(int skillID) {
        SkillData skillData = skills.getOrDefault(skillID, null);
        return skillData != null ? new Skill(skillData) : null;
    }

    public static SkillData getSkillDataByID(int skillID) {
        return skills.getOrDefault(skillID, null);
    }

    public static Set<SkillData> getSkillsDataByJobID(int jobID) {
        return skills.values().stream().filter(skill -> skill.getRootId() == jobID).collect(Collectors.toSet());
    }

    public static Set<Skill> getSkillsByJobID(int jobID) {
        return skills.values()
                .stream()
                .filter(skill -> skill.getRootId() == jobID)
                .map(Skill::new)
                .collect(Collectors.toSet());
    }

    private static void loadRectFromNodeToSkill(Node commonNode, Node rbNode, SkillData skill) {
        int top = Integer.parseInt(XMLApi.getNamedAttribute(commonNode, "y"));
        int bottom = Integer.parseInt(XMLApi.getNamedAttribute(rbNode, "y"));
        int left = Integer.parseInt(XMLApi.getNamedAttribute(commonNode, "x"));
        int right = Integer.parseInt(XMLApi.getNamedAttribute(rbNode, "x"));
        skill.addRect(new Rect(top, bottom, left, right));
    }

    private static void loadReqNodeToSkill(Node mainSkillChildNode, SkillData skill) {
        for (Node reqChild : XMLApi.getAllChildren(mainSkillChildNode)) {
            String childName = XMLApi.getNamedAttribute(reqChild, "name");
            String childValue = XMLApi.getNamedAttribute(reqChild, "value");
            if (MapleUtils.isNumber(childName)) {
                skill.addReqSkill(Integer.parseInt(childName), Integer.parseInt(childValue));
            }
        }
    }

    private static void loadCommonNodeToSkill(Node mainSkillChildNode, SkillData skill) {
        for (Node commonNode : XMLApi.getAllChildren(mainSkillChildNode)) {
            Map<String, String> commonAttr = XMLApi.getAttributes(commonNode);
            String nodeName = commonAttr.get("name");
            if (nodeName.equals("maxLevel")) {
                skill.setMaxLevel(Integer.parseInt(XMLApi.getNamedAttribute(commonNode, "value")));
            } else if (nodeName.contains("lt") && nodeName.length() <= 3) {
                Node rbNode = XMLApi.getFirstChildByNameBF(mainSkillChildNode, nodeName.replace("lt", "rb"));
                loadRectFromNodeToSkill(commonNode, rbNode, skill);
            } else {
                SkillStat skillStat = SkillStat.getSkillStatByString(nodeName);
                if (skillStat != null) {
                    skill.addSkillStatInfo(skillStat, commonAttr.get("value"));
                } else if (PRINT_WZ_UNK) {
                    logger.warning("Unknown SkillStat " + nodeName);
                }
            }
        }
    }

    private static void loadLevelNodeToSkill(Node mainSkillChildNode, SkillData skill) {
        for (Node levelNode : XMLApi.getAllChildren(mainSkillChildNode)) {
            Map<String, String> levels = XMLApi.getAttributes(levelNode);
            String nodeName = levels.get("name");
            try {
                int lvl = Integer.parseInt(nodeName);
                for (Node lvlNode : XMLApi.getAllChildren(levelNode)) {
                    Map<String, String> lvlAttr = XMLApi.getAttributes(lvlNode);
                    String lvlNodeName = lvlAttr.get("name");
                    if (lvlNodeName.equalsIgnoreCase("mpCon")) {
                        skill.addMpCostByLevel(lvl, Integer.parseInt(lvlAttr.get("value")));
                    }
                }
            } catch (Exception e) {
                logger.error("this value isn't a number! thus cannot handle mpCon lvl!");
                e.printStackTrace();
            }
        }
    }

    private static void loadSkillChildNodeToSkill(Node mainSkillChildNode, SkillData skill) {
        String mainName = XMLApi.getNamedAttribute(mainSkillChildNode, "name");
        String mainValue = XMLApi.getNamedAttribute(mainSkillChildNode, "value");
        int intVal = -1337;
        if (MapleUtils.isNumber(mainValue)) {
            intVal = Integer.parseInt(mainValue);
        }
        switch (mainName) {
            case "masterLevel" -> skill.setMasterLevel(intVal);
            case "req" -> loadReqNodeToSkill(mainSkillChildNode, skill);
            case "common" -> loadCommonNodeToSkill(mainSkillChildNode, skill);
            case "level" -> loadLevelNodeToSkill(mainSkillChildNode, skill);
        }
    }

    private static void handleSkillNode(Node skillChild, int rootId) {
        for (Node skillNode : XMLApi.getAllChildren(skillChild)) {
            Map<String, String> skillAttributes = XMLApi.getAttributes(skillNode);
            String skillIdStr = skillAttributes.get("name").replace(".img", "");
            if (MapleUtils.isNumber(skillIdStr)) {
                SkillData skill = new SkillData();
                skill.setRootId(rootId);
                int skillId = Integer.parseInt(skillIdStr);
                skill.setSkillId(skillId);
                for (Node mainSkillChildNode : XMLApi.getAllChildren(skillNode)) {
                    // Handle loading each of the skill node data into the SkillData instance -
                    loadSkillChildNodeToSkill(mainSkillChildNode, skill);
                }
                // Add to the list of Skills -
                skills.put(skillId, skill);
            } else {
                if (PRINT_WZ_UNK) {
                    logger.warning(skillIdStr + " is not a number!");
                }
            }
        }
    }

    private static void loadSkillsFromWZ() {
        File dir = new File(ServerConstants.SKILL_WZ_DIR);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().contains("Dragon")) {
                    continue;
                }
                Node node = XMLApi.getRoot(file);
                if (node == null) {
                    continue;
                }
                List<Node> nodes = XMLApi.getAllChildren(node);
                for (Node mainNode : nodes) {
                    Map<String, String> attributes = XMLApi.getAttributes(mainNode);
                    String rootIdStr = attributes.get("name").replace(".img", "");
                    int rootId;
                    if (MapleUtils.isNumber(rootIdStr)) {
                        rootId = Integer.parseInt(rootIdStr);
                    } else {
                        continue;
                    }
                    Node skillChild = XMLApi.getFirstChildByNameBF(mainNode, "skill");
                    // Handle the Skill node -
                    handleSkillNode(skillChild, rootId);
                }
            }
        }
    }

    public static void loadSkillData() {
        logger.serverNotice("Start loading Skill data...");
        long startTime = System.currentTimeMillis();
        loadSkillsFromWZ();
        logger.serverNotice("~ Finished loading " + skills.size() + " Skills in : " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
    }

    private static void exportSkillsToJson() {
        logger.serverNotice("Start creating the JSONs for skills..");
        MapleUtils.makeDirIfAbsent(JSON_DIR);
        MapleUtils.makeDirIfAbsent(SKILL_JSON_DIR);
        skills.values().forEach(skill -> JsonUtils.createJsonFile(skill, SKILL_JSON_DIR + skill.getSkillId() + ".json"));
        logger.serverNotice("~ Finished creating the skills JSON files! ~");
    }

    public static void loadJsonSkills() {
        long startTime = System.currentTimeMillis();
        File dir = new File(SKILL_JSON_DIR);
        File[] files = dir.listFiles();
        logger.serverNotice("Start loading the JSONs for skills..");
        if (files != null) {
            for (File file : files) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    SkillData skill = mapper.readValue(file, SkillData.class);
                    skills.put(skill.getSkillId(), skill);
                } catch (Exception e) {
                    logger.error("Error occurred while trying to load the file: " + file.getName());
                    e.printStackTrace();
                }
            }
            logger.serverNotice("~ Finished loading " + files.length + " skills JSON files! in: " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
        } else {
            logger.error("Didn't found skills JSONs to load!");
        }
    }

    private static boolean isJsonDataExist() {
        File skillDir = new File(SKILL_JSON_DIR);
        return skillDir.exists();
    }

    public static void load() {
        if (isJsonDataExist()) {
            loadJsonSkills();
        } else {
            loadSkillData();
            exportSkillsToJson();
        }
    }
}
