package com.dori.SpringStory.dataHandlers;

import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.enums.StringDataType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.services.StringDataService;
import com.dori.SpringStory.utils.XMLApi;
import com.dori.SpringStory.dataHandlers.dataEntities.StringData;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StringDataHandler {
    // Logger -
    private static final Logger logger = new Logger(MapDataHandler.class);
    private static final String EQUIP_XML_FILE_NAME = "Eqp";
    private static final String ETC_XML_FILE_NAME = "Etc";

    private static void handleStringNode(Node mainNode, Set<StringData> stringsData, StringDataType type) {
        if (XMLApi.getFirstChildByNameBF(mainNode, "name") != null ||
                XMLApi.getFirstChildByNameBF(mainNode, "mapName") != null) {

            int id = Integer.parseInt(XMLApi.getNamedAttribute(mainNode, "name"));
            String name = XMLApi.getNamedAttribute(
                    XMLApi.getFirstChildByNameBF(mainNode, type == StringDataType.Map ? "mapName" : "name"), "value");
            StringData stringData = new StringData(id, name, type);
            stringsData.add(stringData);
        }
    }

    private static void handleStringParentNode(Node topNode, Set<StringData> stringData, StringDataType type) {
        XMLApi.getAllChildren(topNode).forEach(StringNode -> {
            handleStringNode(StringNode, stringData, type);
        });
    }

    private static boolean isBookSkill(Node node) {
        Node bookName = XMLApi.getFirstChildByNameBF(node, "bookName");
        return bookName != null;
    }

    private static void loadItemStringData(Set<StringData> stringData) {
        String[] files = new String[]{"Cash", "Consume", EQUIP_XML_FILE_NAME, "Ins", "Pet", ETC_XML_FILE_NAME};
        for (String fileDir : files) {
            File file = new File(ServerConstants.STRING_WZ_DIR + fileDir + ".img.xml");
            Node node = XMLApi.getRoot(file);
            List<Node> nodes = XMLApi.getAllChildren(node);
            for (Node topNode : nodes) {
                switch (fileDir) {
                    case EQUIP_XML_FILE_NAME -> {
                        Node equipNode = XMLApi.getFirstChildByNameBF(topNode, EQUIP_XML_FILE_NAME);
                        if (equipNode != null) {
                            // Each category is a node there -
                            XMLApi.getAllChildren(equipNode).forEach(category -> handleStringParentNode(category, stringData, StringDataType.Item));
                        }
                    }
                    case ETC_XML_FILE_NAME -> {
                        Node etcNode = XMLApi.getFirstChildByNameBF(topNode, ETC_XML_FILE_NAME);
                        if (etcNode != null) {
                            handleStringParentNode(etcNode, stringData, StringDataType.Item);
                        }
                    }
                    default -> handleStringParentNode(topNode, stringData, StringDataType.Item);
                }
            }
        }
    }

    private static void loadSkillsStringData(Set<StringData> stringData) {
        File file = new File(ServerConstants.STRING_WZ_DIR + "Skill.img.xml");
        Node node = XMLApi.getRoot(file);
        for (Node skillNode : XMLApi.getAllChildren(node.getFirstChild())) {
            if (!isBookSkill(skillNode)) {
                handleStringNode(skillNode, stringData, StringDataType.Skill);
            }
        }
    }

    private static void loadMobsStringData(Set<StringData> stringData) {
        File file = new File(ServerConstants.STRING_WZ_DIR + "Mob.img.xml");
        Node node = XMLApi.getRoot(file);
        XMLApi.getAllChildren(node.getFirstChild())
                .forEach(mobNode -> handleStringNode(mobNode, stringData, StringDataType.Mob));
    }

    private static void loadNPCsStringData(Set<StringData> stringData) {
        File file = new File(ServerConstants.STRING_WZ_DIR + "Npc.img.xml");
        Node node = XMLApi.getRoot(file);
        handleStringParentNode(node.getFirstChild(), stringData, StringDataType.Npc);
    }

    private static void loadMapsStringData(Set<StringData> stringData) {
        File file = new File(ServerConstants.STRING_WZ_DIR + "Map.img.xml");
        Node node = XMLApi.getRoot(file);
        XMLApi.getAllChildren(node.getFirstChild())
                .forEach(areaMapNode -> handleStringParentNode(areaMapNode,stringData,StringDataType.Map));
    }

    public static void load() {
        logger.serverNotice("Start loading Skill data...");
        long startTime = System.currentTimeMillis();
        Set<StringData> allStringData = new HashSet<>();
        // Load all the String Data -
        loadItemStringData(allStringData);
        loadSkillsStringData(allStringData);
        loadMobsStringData(allStringData);
        loadNPCsStringData(allStringData);
        loadMapsStringData(allStringData);
        // Save all the Strings to The DB -
        StringDataService.getInstance().saveAll(new ArrayList<>(allStringData));
        logger.serverNotice("~ Finished loading " + allStringData.size() + " Strings Data in : " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
    }
}
