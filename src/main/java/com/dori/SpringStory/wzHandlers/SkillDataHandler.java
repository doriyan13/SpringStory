package com.dori.SpringStory.wzHandlers;

import com.dori.SpringStory.client.character.Skill;
import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.enums.SkillStat;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.XMLApi;
import com.dori.SpringStory.utils.utilEntities.Rect;
import com.dori.SpringStory.wzHandlers.wzEntities.SkillData;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;

import static com.dori.SpringStory.constants.ServerConstants.PRINT_WZ_UNK;

@Service
public class SkillDataHandler {
    // Logger -
    private static final Logger logger = new Logger(MapDataHandler.class);
    // Map Cache of all the maps -
    private static final Map<Integer, SkillData> skills = new LinkedHashMap<>();
    // TODO: need to handle MobSkillInfo!

    public static Skill getSkillByID(Integer skillID) {
        SkillData skillData = skills.getOrDefault(skillID, null);
        return skillData != null ? new Skill(skillData) : null;
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

    public static void loadSkillsFromWZ() {
        File dir = new File(ServerConstants.SKILL_WZ_DIR);
        File[] files = dir.listFiles();
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

    public static void loadSkillData() {
        logger.serverNotice("Start loading Skill data...");
        long startTime = System.currentTimeMillis();
        //TODO: in the future to add dat files reading and loading which will be called here -
        loadSkillsFromWZ();
        logger.serverNotice("~ Finished loading " + skills.size() + " Skills in : " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
    }
}
