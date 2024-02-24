package com.dori.SpringStory.dataHandlers;

import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.dataHandlers.dataEntities.NpcData;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.JsonUtils;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.XMLApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.dori.SpringStory.constants.ServerConstants.*;

@Service
public class NpcDataHandler {
    // Logger -
    private static final Logger logger = new Logger(MapDataHandler.class);
    private static final Map<Integer, NpcData> npcsData = new HashMap<>();

    public static NpcData getMobDataByID(Integer npcID){
        return npcsData.get(npcID);
    }

    private static void handleScriptNode(Node scriptNode, NpcData npcData) {
        for (Node idNode : XMLApi.getAllChildren(scriptNode)) {
            String scriptIDString = XMLApi.getNamedAttribute(idNode, "name");
            if (!MapleUtils.isNumber(scriptIDString)) {
                continue;
            }
            int scriptID = Integer.parseInt(XMLApi.getNamedAttribute(idNode, "name"));
            Node scriptValueNode = XMLApi.getFirstChildByNameDF(idNode, "script");
            if (scriptValueNode != null) {
                String scriptName = XMLApi.getNamedAttribute(scriptValueNode, "value");
                npcData.getScripts().put(scriptID, scriptName);
            }
        }
    }

    private static void loadNpcsDataFromWZ() {
        File dir = new File(ServerConstants.NPC_WZ_DIR);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                NpcData npcData = new NpcData();
                Node node = XMLApi.getRoot(file);
                Node mainNode = XMLApi.getAllChildren(node).getFirst();
                int id = Integer.parseInt(XMLApi.getNamedAttribute(mainNode, "name")
                        .replace(".xml", "").replace(".img", ""));
                npcData.setNpcID(id);
                npcData.setMove(XMLApi.getFirstChildByNameBF(mainNode, "move") != null);
                Node scriptNode = XMLApi.getFirstChildByNameBF(mainNode, "script");
                if (scriptNode != null) {
                    handleScriptNode(scriptNode, npcData);
                }
                npcsData.put(npcData.getNpcID(), npcData);
            }
        }
    }

    public static void loadNpcsData() {
        logger.serverNotice("Start loading Npcs data...");
        long startTime = System.currentTimeMillis();
        loadNpcsDataFromWZ();
        logger.serverNotice("~ Finished loading " + npcsData.size() + " Npcs in : " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
    }

    private static void exportNpcsDataToJson() {
        logger.serverNotice("Start creating the JSONs for Npcs..");
        MapleUtils.makeDirIfAbsent(JSON_DIR);
        MapleUtils.makeDirIfAbsent(NPC_JSON_DIR);
        npcsData.values().forEach(npc -> JsonUtils.createJsonFile(npc, NPC_JSON_DIR + npc.getNpcID() + ".json"));
        logger.serverNotice("~ Finished creating the Npcs JSON files! ~");
    }

    public static void loadJsonNpcsData() {
        long startTime = System.currentTimeMillis();
        File dir = new File(NPC_JSON_DIR);
        File[] files = dir.listFiles();
        logger.serverNotice("Start loading the JSONs for Npcs..");
        if (files != null) {
            for (File file : files) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    NpcData npcData = mapper.readValue(file, NpcData.class);
                    npcsData.put(npcData.getNpcID(), npcData);
                } catch (Exception e) {
                    logger.error("Error occurred while trying to load the file: " + file.getName());
                    e.printStackTrace();
                }
            }
            logger.serverNotice("~ Finished loading " + files.length + " Npcs JSON files! in: " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
        } else {
            logger.error("Didn't found Npcs JSONs to load!");
        }
    }

    private static boolean isJsonDataExist() {
        File skillDir = new File(NPC_JSON_DIR);
        return skillDir.exists();
    }

    public static void load() {
        try {
            if (isJsonDataExist()) {
                loadJsonNpcsData();
            } else {
                loadNpcsData();
                exportNpcsDataToJson();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }
}
