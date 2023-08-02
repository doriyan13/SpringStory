package com.dori.SpringStory.wzHandlers;

import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.JsonUtils;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.XMLApi;
import com.dori.SpringStory.world.fieldEntities.mob.Mob;
import com.dori.SpringStory.world.fieldEntities.mob.MobSkill;
import com.dori.SpringStory.wzHandlers.wzEntities.EquipData;
import com.dori.SpringStory.wzHandlers.wzEntities.MobData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.dori.SpringStory.constants.ServerConstants.*;

public class MobDataHandler {
    // Logger -
    private static final Logger logger = new Logger(MapDataHandler.class);
    private static final String[] deathNodesNames = {"die1", "die2", "dieF"};
    // Map Cache of all the Mobs -
    private static final Map<Integer, MobData> mobs = new HashMap<>();

    public static MobData getMobDataByID(Integer mobID){
        return mobs.get(mobID);
    }

    public static Mob getMobByID(Integer mobID) {
        MobData mobData = mobs.getOrDefault(mobID, null);
        return mobData != null ? new Mob(mobData) : null;
    }

    private static long calcRespawnDelay(Node mobNode) {
        long respawnDelay = 0;
        for (String deathNodeName : deathNodesNames) {
            Node deathNode = XMLApi.getFirstChildByNameBF(mobNode, deathNodeName);
            if (deathNode != null) {
                for (Node n : XMLApi.getAllChildren(deathNode)) {
                    for (Node n2 : XMLApi.getAllChildren(n)) {
                        String name = XMLApi.getNamedAttribute(n2, "name");
                        if (!name.equalsIgnoreCase("delay"))
                            continue;
                        String value = XMLApi.getNamedAttribute(n2, "value");
                        respawnDelay += Integer.parseInt(value);
                    }
                }
            }
        }
        return respawnDelay;
    }

    private static void handleMaxHpNode(String value, MobData mob) {
        if (MapleUtils.isNumber(value)) {
            mob.setMaxHp(Long.parseLong(value));
        } else {
            mob.setMaxHp(1337);
        }
    }

    private static void handleSkillNode(Node mobAttribute, MobData mob) {
        for (Node skillIDNode : XMLApi.getAllChildren(mobAttribute)) {
            if (!MapleUtils.isNumber(XMLApi.getNamedAttribute(skillIDNode, "name"))) {
                continue;
            }
            MobSkill mobSkill = new MobSkill();
            mobSkill.setSkillSN(Integer.parseInt(XMLApi.getNamedAttribute(skillIDNode, "name")));
            for (Node skillInfoNode : XMLApi.getAllChildren(skillIDNode)) {
                String skillNodeName = XMLApi.getNamedAttribute(skillInfoNode, "name");
                String skillNodeValue = XMLApi.getNamedAttribute(skillInfoNode, "value");
                switch (skillNodeName) {
                    case "skill" -> mobSkill.setSkillID(Integer.parseInt(skillNodeValue));
                    case "action" -> mobSkill.setAction(Byte.parseByte(skillNodeValue));
                    case "level" -> mobSkill.setLevel(Integer.parseInt(skillNodeValue));
                    case "effectAfter" -> {
                        if (!skillNodeValue.equals("")) {
                            mobSkill.setEffectAfter(Integer.parseInt(skillNodeValue));
                        }
                    }
                }
            }
            mob.addSkill(mobSkill);
        }
    }

    private static MobData handleInfoNode(int id, long respawnDelay, Node infoNode) {
        MobData mob = new MobData(id);
        for (Node mobAttribute : XMLApi.getAllChildren(infoNode)) {
            String name = XMLApi.getNamedAttribute(mobAttribute, "name");
            String value = XMLApi.getNamedAttribute(mobAttribute, "value");
            switch (name) {
                case "level", "Level" -> mob.setLevel(Integer.parseInt(value));
                case "firstAttack", "firstattack" -> mob.setFirstAttack((int) Double.parseDouble(value));
                //case "bodyattack", "bodyAttack" -> handle in the future
                case "maxHP", "finalmaxHP" -> handleMaxHpNode(value, mob);
                case "maxMP" -> mob.setMaxMp(Integer.parseInt(value));
                case "PADamage" -> mob.setPad(Integer.parseInt(value));
                case "PDRate" -> mob.setPdr(Integer.parseInt(value));
                case "MADamage" -> mob.setMad(Integer.parseInt(value));
                case "MDRate" -> mob.setMdr(Integer.parseInt(value));
                case "acc" -> mob.setAcc(Integer.parseInt(value));
                case "eva" -> mob.setEva(Integer.parseInt(value));
                case "pushed" -> mob.setPushed(Integer.parseInt(value));
                case "exp" -> mob.setExp(Integer.parseInt(value));
                case "summonType" -> mob.setSummonType(Integer.parseInt(value));
                case "category" -> mob.setCategory(Integer.parseInt(value));
                case "mobType" -> mob.setMobType(value);
                case "speed", "Speed" -> mob.setSpeed(Integer.parseInt(value));
                case "fs" -> mob.setFs(Double.parseDouble(value));
                case "elemAttr" -> mob.setElemAttr(value);
                case "hpTagColor" -> mob.setHpTagColor(Integer.parseInt(value));
                case "hpTagBgcolor" -> mob.setHpTagBgColor(Integer.parseInt(value));
                case "HPgaugeHide" -> mob.setHpGaugeHide(Integer.parseInt(value) == 1);
                case "boss" -> mob.setBoss(Integer.parseInt(value) == 1);
                case "undead", "Undead" -> mob.setUndead(Integer.parseInt(value) == 1);
                case "hideName", "hidename" -> mob.setHideName(Integer.parseInt(value) == 1);
                case "hideHP" -> mob.setHideHP(Integer.parseInt(value) == 1);
                case "noFlip" -> mob.setNoFlip(Integer.parseInt(value) == 1);
                case "rareItemDropLevel" -> mob.setRareItemDropLevel(Integer.parseInt(value));
                case "hpRecovery" -> mob.setHpRecovery(Integer.parseInt(value));
                case "mpRecovery" -> mob.setMpRecovery(Integer.parseInt(value));
                case "revive" -> mob.setRespawnDelay(respawnDelay);
                case "skill" -> handleSkillNode(mobAttribute, mob);
            }
        }
        return mob;
    }

    public static void loadMobsFromWZ() {
        File dir = new File(ServerConstants.MOB_WZ_DIR);

        File[] files = dir.listFiles();
        if(files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    Document doc = XMLApi.getRoot(file);
                    Node node = XMLApi.getAllChildren(doc).get(0);
                    if (node == null) {
                        continue;
                    }
                    // handle death delay calculation -
                    long respawnDelay = calcRespawnDelay(node);

                    int id = Integer.parseInt(XMLApi.getNamedAttribute(node, "name").replace(".img", ""));
                    Node infoNode = XMLApi.getFirstChildByNameBF(node, "info");

                    if (infoNode != null) {
                        MobData mob = handleInfoNode(id, respawnDelay, infoNode);
                        mobs.putIfAbsent(mob.getId(), mob);
                    }
                }
            }
        }
    }

    public static void loadMobData() {
        logger.serverNotice("Start loading Mob WZ data...");
        long startTime = System.currentTimeMillis();
        loadMobsFromWZ();
        logger.serverNotice("~ Finished loading " + mobs.size() + " Mob WZ data in : " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
    }

    private static void exportMobsToJson() {
        logger.serverNotice("Start creating the JSONs for mobs..");
        MapleUtils.makeDirIfAbsent(JSON_DIR);
        MapleUtils.makeDirIfAbsent(MOB_JSON_DIR);
        mobs.values().forEach(mob -> JsonUtils.createJsonFile(mob, MOB_JSON_DIR + mob.getId() + ".json"));
        logger.serverNotice("~ Finished creating the mobs JSONs files! ~");
    }

    public static void loadJsonMobs() {
        long startTime = System.currentTimeMillis();
        File dir = new File(MOB_JSON_DIR);
        File[] files = dir.listFiles();
        logger.serverNotice("Start loading the JSONs for mobs..");
        if (files != null) {
            for (File file : files) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    MobData field = mapper.readValue(file, MobData.class);
                    mobs.put(field.getId(), field);
                } catch (Exception e) {
                    logger.error("Error occurred while trying to load the file: " + file.getName());
                    e.printStackTrace();
                }
            }
            logger.serverNotice("~ Finished loading " + files.length + " maps JSONs files! in: " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
        } else {
            logger.error("Didn't found mobs JSONs to load!");
        }
    }

    private static boolean isJsonDataExist() {
        File mobDir = new File(MOB_JSON_DIR);
        return mobDir.exists();
    }

    public static void load() {
        if (isJsonDataExist()) {
            loadJsonMobs();
        } else {
            loadMobData();
            exportMobsToJson();
        }
    }
}
