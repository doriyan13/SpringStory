package com.dori.SpringStory.wzHandlers;

import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.enums.FieldType;
import com.dori.SpringStory.enums.PortalType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.XMLApi;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.*;
import com.dori.SpringStory.wzHandlers.wzEntities.MapData;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;

import static com.dori.SpringStory.constants.ServerConstants.PRINT_WZ_UNK;

@Service
public class MapDataHandler {
    // Logger -
    private static final Logger logger = new Logger(MapDataHandler.class);
    // Map Cache of all the maps -
    private static final Map<Integer, MapData> fields = new LinkedHashMap<>();
    // List of world map fields -
    private static final List<Integer> worldMapFields = new ArrayList<>();
    // List of maps for goto command -
    private static final HashMap<String,Integer> goToMaps = new HashMap<>();

    public static void initGoToMaps(){
        //TODO:: will be removed after i will manage the string of maps and will create a !search command!!!
        goToMaps.put("ardent", 910001000);
        goToMaps.put("ariant", 260000100);
        goToMaps.put("amherst", 1010000);
        goToMaps.put("amoria", 680000000);
        goToMaps.put("aqua", 860000000);
        goToMaps.put("aquaroad", 230000000);
        goToMaps.put("boatquay", 541000000);
        goToMaps.put("cwk", 610030000);
        goToMaps.put("edelstein", 310000000);
        goToMaps.put("ellin", 300000000);
        goToMaps.put("ellinia", 101000000);
        goToMaps.put("elnath", 211000000);
        goToMaps.put("ereve", 130000000);
        goToMaps.put("florina", 120000300);
        goToMaps.put("fm", 910000000);
        goToMaps.put("gmmap", 180000000);
        goToMaps.put("happy", 209000000);
        goToMaps.put("harbor", 104000000);
        goToMaps.put("henesys", 100000000);
        goToMaps.put("kerning", 103000000);
        goToMaps.put("korean", 222000000);
        goToMaps.put("leafre", 240000000);
        goToMaps.put("ludi", 220000000);
        goToMaps.put("malaysia", 550000000);
        goToMaps.put("mulung", 250000000);
        goToMaps.put("nautilus", 120000000);
        goToMaps.put("nlc", 600000000);
        goToMaps.put("omega", 221000000);
        goToMaps.put("orbis", 200000000);
        goToMaps.put("pinkbean", 270050100);
        goToMaps.put("perion", 102000000);
        goToMaps.put("rien", 140000000);
        goToMaps.put("showatown", 801000000);
        goToMaps.put("singapore", 540000000);
        goToMaps.put("sixpath", 104020000);
        goToMaps.put("sleepywood", 105000000);
        goToMaps.put("tot", 270000000);
        goToMaps.put("tynerum", 301000000);
        goToMaps.put("zipangu", 800000000);
        goToMaps.put("pianus", 230040420);
        goToMaps.put("horntail", 240060200);
        goToMaps.put("chorntail", 240060201);
        goToMaps.put("griffey", 240020101);
        goToMaps.put("manon", 240020401);
        goToMaps.put("zakum", 280030000);
        goToMaps.put("czakum", 280030001);
        goToMaps.put("pap", 220080001);
        goToMaps.put("oxquiz", 109020001);
        goToMaps.put("ola", 109030101);
        goToMaps.put("fitness", 109040000);
        goToMaps.put("snowball", 109060000);
        goToMaps.put("dojo", 925020001);
        goToMaps.put("pq", 910002000);
        goToMaps.put("h", 100000000);
        goToMaps.put("ursus", 970072200);
        goToMaps.put("pno", 811000008);
        goToMaps.put("cygnus", 271040000);
        goToMaps.put("goldenbeach", 914200000);
        goToMaps.put("ardentmill", 910001000);
        goToMaps.put("oz", 992000000);
    }

    public static HashMap<String,Integer> getGoToMaps(){
        return goToMaps;
    }

    public static Field getMapByName(String mapName){
        Integer mapId = getGoToMaps().get(mapName.toLowerCase());
        return mapId != null ? getMapByID(mapId) : null;
    }

    public static Field getMapByID(Integer mapID) {
        MapData mapData = fields.get(mapID);
        return mapData != null ? new Field(mapData) : null;
    }

    private static void loadInfoNodeDataToField(Node infoNode, Field field) {
        for (Node n : XMLApi.getAllChildren(infoNode)) {
            Map<String, String> attr = XMLApi.getAttributes(n);
            String name = attr.get("name");
            String value = attr.get("value");
            switch (name) {
                case "link" -> field.setLink(Integer.parseInt(value));
                case "mobRate" -> field.setMobRate(Float.parseFloat(value));
                case "dropRate" -> field.setDropRate(Float.parseFloat(value));
                case "returnMap" -> field.setReturnMap(Integer.parseInt(value));
                case "createMobInterval" -> field.setCreateMobInterval(Integer.parseInt(value));
                case "everlast" -> field.setEverLast(Integer.parseInt(value) != 0);
                case "town" -> field.setTown(Integer.parseInt(value) != 0);
                case "needSkillForFly" -> field.setNeedSkillForFly(Integer.parseInt(value) != 0);
                case "fly" -> field.setFly(Integer.parseInt(value) != 0);
                case "swim" -> field.setSwim(Integer.parseInt(value) != 0);
                case "personalShop" -> field.setPersonalShop(Integer.parseInt(value) != 0);
                case "lvForceMove" -> field.setLvForceMove(Integer.parseInt(value));
                case "decHP" -> field.setDecHP(Integer.parseInt(value));
                case "decInterval" -> field.setDecInterval(Integer.parseInt(value));
                case "protectItem" -> field.setProtectItem(Integer.parseInt(value));
                case "forcedReturn" -> field.setForcedReturn(Integer.parseInt(value));
                case "timeLimit" -> field.setTimeLimit(Integer.parseInt(value));
                case "fieldLimit" -> field.setFieldLimit(Long.parseLong(value));
                case "reactorShuffle" -> field.setReactorShuffle(Integer.parseInt(value) != 0);
                case "expeditionOnly" -> field.setExpeditionOnly(Integer.parseInt(value) != 0);
                case "partyOnly" -> field.setPartyOnly(Integer.parseInt(value) != 0);
                case "onFirstUserEnter" -> field.setOnFirstUserEnter(value);
                case "onUserEnter" -> field.setOnUserEnter(value);
                case "fixedMobCapacity" -> field.setFixedMobCapacity(Integer.parseInt(value));
                case "recovery" -> field.setRecovery(Float.parseFloat(value));
                case "consumeItemCoolTime" -> field.setConsumeItemCoolTime(Integer.parseInt(value));
                case "timeOut" -> field.setTimeOut(Integer.parseInt(value));
                case "lvLimit" -> field.setLvLimit(Integer.parseInt(value));
                case "fieldType" -> {
                    if (value.equals("")) {
                        field.setFieldType(FieldType.DEAFULT);
                    } else {
                        FieldType fieldType = FieldType.getByVal(Integer.parseInt(value));
                        if (fieldType == null) {
                            field.setFieldType(FieldType.DEAFULT);
                            break;
                        }
                        field.setFieldType(fieldType);
                    }
                }
                case "VRTop" -> field.setVrTop(Integer.parseInt(value));
                case "VRLeft" -> field.setVrLeft(Integer.parseInt(value));
                case "VRBottom" -> field.setVrBottom(Integer.parseInt(value));
                case "VRRight" -> field.setVrRight(Integer.parseInt(value));
                //Skip Them! (don't really need that data)
                case "version", "bgm", "cloud", "hideMinimap", "mapMark", "noMapCmd", "mapDesc", "moveLimit",
                        "fs", "miniMapOnOff", "entrustedShop", "dropExpire", "allowedItem", "effect",
                        "reactorShuffleName", "EscortMinTime", "streetName", "mapName", "help", "autoLieDetector",
                        "snow", "rain", "VRLimit", "scrollDisable", "escort", "allMoveCheck",
                        "zakum2Hack", "decMP", "protectSetKey", "decRate", "fieldSubType", "phaseAlpha",
                        "phase", "phaseBG", "noRegenMap", "damageCheckFree", "blockPBossChange", "timeMob" -> {
                }
                default -> {
                    if (PRINT_WZ_UNK) {
                        logger.warning("missing handling to: " + name + " | value: " + value);
                    }
                }
            }
        }
    }

    private static void loadFootholdNodeDataToField(Node fhNode, Field field) {
        if (fhNode != null) {
            for (Node layerIDNode : XMLApi.getAllChildren(fhNode)) {
                int layerID = Integer.parseInt(XMLApi.getNamedAttribute(layerIDNode, "name"));
                for (Node groupIDNode : XMLApi.getAllChildren(layerIDNode)) {
                    int groupID = Integer.parseInt(XMLApi.getNamedAttribute(groupIDNode, "name"));
                    for (Node idNode : XMLApi.getAllChildren(groupIDNode)) {
                        int fhId = Integer.parseInt(XMLApi.getNamedAttribute(idNode, "name"));
                        Foothold fh = new Foothold(fhId, layerID, groupID);
                        for (Node n : XMLApi.getAllChildren(idNode)) {
                            String name = XMLApi.getNamedAttribute(n, "name");
                            String value = XMLApi.getNamedAttribute(n, "value");
                            switch (name) {
                                case "x1" -> fh.setX1(Integer.parseInt(value));
                                case "y1" -> fh.setY1(Integer.parseInt(value));
                                case "x2" -> fh.setX2(Integer.parseInt(value));
                                case "y2" -> fh.setY2(Integer.parseInt(value));
                                case "next" -> fh.setNext(Integer.parseInt(value));
                                case "prev" -> fh.setPrev(Integer.parseInt(value));
                                default -> {
                                    if (PRINT_WZ_UNK) {
                                        logger.warning("unknown Foothold property - " + name + " with value: " + value);
                                    }
                                }
                            }
                        }
                        field.addFoothold(fh);
                    }
                }
            }
        }
    }

    private static void loadPortalsNodeDataToField(Node portalNode, Field field) {
        if (portalNode != null) {
            for (Node idNode : XMLApi.getAllChildren(portalNode)) {
                int portalId = Integer.parseInt(XMLApi.getNamedAttribute(idNode, "name"));
                Portal portal = new Portal(portalId);
                for (Node n : XMLApi.getAllChildren(idNode)) {
                    String name = XMLApi.getNamedAttribute(n, "name");
                    String value = XMLApi.getNamedAttribute(n, "value");
                    switch (name) {
                        case "pt" -> portal.setType(PortalType.getTypeByInt(Integer.parseInt(value)));
                        case "pn" -> portal.setName(value);
                        case "tm" -> portal.setTargetMapId(Integer.parseInt(value));
                        case "tn" -> portal.setTargetPortalName(value);
                        case "x" -> portal.getPosition().setX(Integer.parseInt(value));
                        case "y" -> portal.getPosition().setY(Integer.parseInt(value));
                        case "horizontalImpact" -> portal.setHorizontalImpact(Integer.parseInt(value));
                        case "verticalImpact" -> portal.setVerticalImpact(Integer.parseInt(value));
                        case "script" -> portal.setScript(value);
                        case "onlyOnce" -> portal.setOnlyOnce(Integer.parseInt(value) != 0);
                        case "delay" -> portal.setDelay(Integer.parseInt(value));
                        default -> {
                            if (PRINT_WZ_UNK) {
                                logger.warning("unknown portal property - " + name + " with value: " + value);
                            }
                        }
                    }
                }
                field.addPortal(portal);
            }
        }
    }

    private static void loadLifeNodeDataToField(Node lifeNode, Field field) {
        if (lifeNode != null) {
            List<Node> idNodes = new ArrayList<>();
            if (XMLApi.getFirstChildByNameBF(lifeNode, "isCategory") != null) {
                for (Node catNode : XMLApi.getAllChildren(lifeNode)) {
                    if (!XMLApi.getNamedAttribute(catNode, "name").equals("isCategory")) {
                        idNodes.addAll(XMLApi.getAllChildren(catNode));
                    }
                }
            } else {
                idNodes = XMLApi.getAllChildren(lifeNode);
            }
            for (Node idNode : idNodes) {
                Life life = new Life(0);
                for (Node n : XMLApi.getAllChildren(idNode)) {
                    String name = XMLApi.getNamedAttribute(n, "name");
                    String value = XMLApi.getNamedAttribute(n, "value");
                    switch (name) {
                        case "id" -> life.setTemplateId(Integer.parseInt(value));
                        case "type" -> life.setLifeType(value);
                        case "limitedname" -> life.setLimitedName(value);
                        case "x" -> life.getPosition().setX(Integer.parseInt(value));
                        case "y" -> life.getPosition().setY(Integer.parseInt(value));
                        case "mobTime" -> life.setMobTime(Integer.parseInt(value));
                        case "f" -> life.setFlip(Integer.parseInt(value) != 0);
                        case "hide" -> life.setHide(Integer.parseInt(value) != 0);
                        case "fh" -> life.setFh((short) Integer.parseInt(value));
                        case "cy" -> life.setCy(Integer.parseInt(value));
                        case "rx0" -> life.setRx0(Integer.parseInt(value));
                        case "rx1" -> life.setRx1(Integer.parseInt(value));
                        case "team" -> life.setTeam((byte) Integer.parseInt(value));
                        default -> {
                            if (PRINT_WZ_UNK) {
                                logger.warning("unknown life property - " + name + " with value: " + value);
                            }
                        }
                    }
                }
                field.addLife(life);
            }
        }
    }

    private static void loadReactorNodeDataToField(Node reactorNode, Field field) {
        if (reactorNode != null) {
            for (Node reactorIdNode : XMLApi.getAllChildren(reactorNode)) {
                Reactor reactor = new Reactor(0);
                reactor.setLifeType("r");
                for (Node valNode : XMLApi.getAllChildren(reactorIdNode)) {
                    String name = XMLApi.getNamedAttribute(valNode, "name");
                    String value = XMLApi.getNamedAttribute(valNode, "value");
                    int iVal = MapleUtils.isNumber(value) ? Integer.parseInt(value) : 0;
                    switch (name) {
                        case "id" -> reactor.setTemplateId(iVal);
                        case "x" -> {
                            Position curPos = reactor.getHomePosition();
                            if (curPos == null) {
                                curPos = new Position();
                            }
                            curPos.setX(iVal);
                            reactor.getPosition().setX(iVal);
                            reactor.setHomePosition(curPos);
                        }
                        case "y" -> {
                            Position curPos = reactor.getHomePosition();
                            if (curPos == null) {
                                curPos = new Position();
                            }
                            curPos.setY(iVal);
                            reactor.getPosition().setY(iVal);
                            reactor.setHomePosition(curPos);
                        }
                        case "reactorTime" -> reactor.setMobTime(iVal); //They multiplied by 1000 ?
                        case "f" -> reactor.setFlip(iVal != 0);
                        case "name" -> reactor.setLimitedName(value);
                        default -> {
                            if (PRINT_WZ_UNK) {
                                logger.warning(String.format("Unknown reactor property %s with value %s", name, value));
                            }
                        }
                    }
                }
                field.addLife(reactor);
            }
        }
    }

    private static void loadDirectionNodeDataToField(Node directionInfoNode, Field field) {
        if (directionInfoNode != null) {
            for (Node idNode : XMLApi.getAllChildren(directionInfoNode)) {
                String name = XMLApi.getNamedAttribute(idNode, "name");
                for (Node n : XMLApi.getAllChildren(idNode)) {
                    // there are more values but only the client use it we need only eventQ
                    if (XMLApi.getNamedAttribute(n, "name").equals("EventQ")) {
                        List<String> directionInfo = new ArrayList<>();
                        for (Node event : XMLApi.getAllChildren(n)) {
                            directionInfo.add(XMLApi.getNamedAttribute(event, "value"));
                        }
                        field.addDirectionInfo(Integer.parseInt(name), directionInfo);
                    }
                }
            }
        }
    }

    public static void loadMapsFromWZ() {
        File dir = new File(ServerConstants.MAP_WZ_DIR);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.listFiles() == null) {
                continue;
            }
            for (File mapFile : file.listFiles()) {
                Document doc = XMLApi.getRoot(mapFile);
                Node node = XMLApi.getAllChildren(doc).get(0);
                if (node == null) {
                    continue;
                }
                int id = Integer.parseInt(XMLApi.getAttributes(node).get("name").replace(".img", ""));
                Field field = new Field(id);
                // Load info node data into field instance -
                Node infoNode = XMLApi.getFirstChildByNameBF(node, "info");
                loadInfoNodeDataToField(infoNode, field);
                // Verify field type -
                if (field.getFieldType() == null) {
                    field.setFieldType(FieldType.DEAFULT);
                }
                // Load foothold node data into field instance -
                Node fhNode = XMLApi.getFirstChildByNameBF(node, "foothold");
                loadFootholdNodeDataToField(fhNode, field);
                // Load portal node data into field instance -
                Node portalNode = XMLApi.getFirstChildByNameBF(node, "portal");
                loadPortalsNodeDataToField(portalNode, field);
                // Load life node data into field instance -
                Node lifeNode = XMLApi.getFirstChildByNameBF(node, "life");
                loadLifeNodeDataToField(lifeNode, field);
                // Load reactor node data into field instance -
                Node reactorNode = XMLApi.getFirstChildByNameBF(node, "reactor");
                loadReactorNodeDataToField(reactorNode, field);
                // Load direction info node data into field instance -
                Node directionInfoNode = XMLApi.getFirstChildByNameBF(node, "directionInfo");
                loadDirectionNodeDataToField(directionInfoNode, field);

                // Add the new Field instance to the map of Fields -
                fields.putIfAbsent(field.getId(), field);
            }
        }
    }

    public static void loadWorldMapFromWz() {
        File dir = new File(ServerConstants.WORLD_MAP_WZ_DIR);
        File[] files = dir.listFiles();
        for (File file : files) {
            Document doc = XMLApi.getRoot(file);
            Node node = XMLApi.getAllChildren(doc).get(0);
            if (node == null) {
                continue;
            }
            Node mapList = XMLApi.getFirstChildByNameBF(node, "MapList");
            for (Node n : XMLApi.getAllChildren(Objects.requireNonNull(mapList))) {
                Node infoNode = XMLApi.getFirstChildByNameBF(n, "mapNo");
                for (Node info : XMLApi.getAllChildren(Objects.requireNonNull(infoNode))) {
                    Map<String, String> attr = XMLApi.getAttributes(info);
                    int fieldId = Integer.parseInt(attr.get("value"));
                    if (!worldMapFields.contains(fieldId)) {
                        worldMapFields.add(fieldId);
                    }
                }
            }
        }
    }

    public static void loadMapData() {
        logger.serverNotice("Start loading Map data...");
        long startTime = System.currentTimeMillis();
        //TODO: in the future to add dat files reading and loading which will be called here -
        loadMapsFromWZ();
        logger.serverNotice("~ Finished loading " + fields.size() + " Map data in : " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
    }

    public static void loadWorldMapData() {
        logger.serverNotice("Start Loading WorldMap...");
        long startTime = System.currentTimeMillis();
        //TODO: in the future to add dat files reading and loading which will be called here -
        loadWorldMapFromWz();
        logger.serverNotice("~ Finished loading " + worldMapFields.size() + " WorldMaps in : " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
    }
}
