package com.dori.SpringStory.dataHandlers;

import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.dataHandlers.dataEntities.ItemOptionData;
import com.dori.SpringStory.enums.BaseStat;
import com.dori.SpringStory.enums.InventoryType;
import com.dori.SpringStory.enums.ItemOptionType;
import com.dori.SpringStory.enums.SpecStat;
import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.inventory.ItemRewardInfo;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.JsonUtils;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.XMLApi;
import com.dori.SpringStory.dataHandlers.dataEntities.EquipData;
import com.dori.SpringStory.dataHandlers.dataEntities.ItemData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;

import static com.dori.SpringStory.constants.ServerConstants.*;
import static com.dori.SpringStory.enums.ScrollStat.*;

@Service
public class ItemDataHandler {
    // Logger -
    private static final Logger logger = new Logger(MapDataHandler.class);
    // Map Cache of all the items -
    private static final Map<Integer, ItemData> items = new HashMap<>();
    private static final Map<Integer, EquipData> equips = new HashMap<>();
    private static final Map<Integer, ItemOptionData> itemOptions = new HashMap<>();


    public static ItemData getItemDataByID(int itemID) {
        return items.getOrDefault(itemID, null);
    }

    public static EquipData getEquipDataByID(int equipID) {
        return equips.getOrDefault(equipID, null);
    }

    public static Item getItemByID(int itemID) {
        ItemData itemData = items.getOrDefault(itemID, null);
        return itemData != null ? new Item(itemData) : null;
    }

    public static Equip getEquipByID(int equipID) {
        EquipData equipData = equips.getOrDefault(equipID, null);
        return equipData != null ? new Equip(equipData) : null;
    }

    public static List<ItemOptionData> getAllItemOptions() {
        return new ArrayList<>(itemOptions.values());
    }

    private static void loadInfoNode(Node infoNode, ItemData item) {
        if (infoNode != null) {
            for (Node info : XMLApi.getAllChildren(infoNode)) {
                String name = XMLApi.getNamedAttribute(info, "name");
                String value = XMLApi.getNamedAttribute(info, "value");
                int intValue = 0;
                if (MapleUtils.isInteger(value)) {
                    intValue = Integer.parseInt(value);
                }
                switch (name) {
                    case "cash" -> item.setCash(intValue != 0);
                    case "price" -> item.setPrice(intValue);
                    case "slotMax" -> item.setSlotMax(intValue);
                    case "skill" -> {
                        for (Node masteryBookSkillIdNode : XMLApi.getAllChildren(info)) {
                            item.addSkill(Integer.parseInt((XMLApi.getNamedAttribute(masteryBookSkillIdNode, "value"))));
                        }
                    }
                    case "reqSkillLevel" -> item.setReqSkillLv(intValue);
                    case "masterLevel" -> item.setMasterLv(intValue);
                    case "tradeBlock" -> item.setTradeBlock(intValue != 0);
                    case "notSale" -> item.setNotSale(intValue != 0);
                    case "path" -> item.setPath(value);
                    case "noCursed" -> item.setNoCursed(intValue != 0);
                    case "noNegative" -> item.putScrollStat(noNegative, intValue);
                    case "incRandVol" -> item.putScrollStat(incRandVol, intValue);
                    case "success" -> item.putScrollStat(success, intValue);
                    case "incSTR" -> item.putScrollStat(incSTR, intValue);
                    case "incDEX" -> item.putScrollStat(incDEX, intValue);
                    case "incINT" -> item.putScrollStat(incINT, intValue);
                    case "incLUK" -> item.putScrollStat(incLUK, intValue);
                    case "incPAD" -> item.putScrollStat(incPAD, intValue);
                    case "incMAD" -> item.putScrollStat(incMAD, intValue);
                    case "incPDD" -> item.putScrollStat(incPDD, intValue);
                    case "incMDD" -> item.putScrollStat(incMDD, intValue);
                    case "incEVA" -> item.putScrollStat(incEVA, intValue);
                    case "incACC" -> item.putScrollStat(incACC, intValue);
                    case "incPERIOD" -> item.putScrollStat(incPERIOD, intValue);
                    case "incMHP", "incMaxHP" -> item.putScrollStat(incMHP, intValue);
                    case "incMMP", "incMaxMP" -> item.putScrollStat(incMMP, intValue);
                    case "incSpeed" -> item.putScrollStat(incSpeed, intValue);
                    case "incJump" -> item.putScrollStat(incJump, intValue);
                    case "incReqLevel" -> item.putScrollStat(incReqLevel, intValue);
                    case "randOption" -> item.putScrollStat(randOption, intValue);
                    case "randstat", "randStat" -> item.putScrollStat(randStat, intValue);
                    case "tuc" -> item.putScrollStat(tuc, intValue);
                    case "incIUC" -> item.putScrollStat(incIUC, intValue);
                    case "speed" -> item.putScrollStat(speed, intValue);
                    case "forceUpgrade" -> item.putScrollStat(forceUpgrade, intValue);
                    case "cursed" -> item.putScrollStat(cursed, intValue);
                    case "maxSuperiorEqp" -> item.putScrollStat(maxSuperiorEqp, intValue);
                    case "reqRUC" -> item.putScrollStat(reqRUC, intValue);
                    case "bagType" -> item.setBagType(intValue);
                    case "quest" -> item.setQuest(intValue != 0);
                    case "reqQuestOnProgress" -> item.setReqQuestOnProgress(intValue);
                    case "qid", "questId" -> {
                        if (value.contains(".") && value.split("[.]").length > 0) {
                            item.addQuest(Integer.parseInt(value.split("[.]")[0]));
                        } else {
                            item.addQuest(intValue);
                        }
                    }
                    case "notConsume" -> item.setNotConsume(intValue != 0);
                    case "monsterBook" -> item.setMonsterBook(intValue != 0);
                    case "mob" -> item.setMobID(intValue);
                    case "npc" -> item.setNpcID(intValue);
                    case "linkedID" -> item.setLinkedID(intValue);
                    case "reqEquipLevelMax" -> item.putScrollStat(reqEquipLevelMax, intValue);
                    case "createType" -> item.putScrollStat(createType, intValue);
                    case "optionType" -> item.putScrollStat(optionType, intValue);
                    case "grade" -> item.setGrade(intValue);
                    case "recover" -> item.putScrollStat(recover, intValue);
                    case "setItemCategory" -> item.putScrollStat(setItemCategory, intValue);
                    case "create" -> item.setCreateID(intValue);
                    case "mobHP" -> item.setMobHP(intValue);
                    case "only" -> item.setOnly(Integer.parseInt(value) != 0); //TODO: need to test with ID - 3994126
                    case "recoveryHP" -> item.setRecoveryHP(intValue);
                    case "recoveryMP" -> item.setRecoveryMP(intValue);
                    // info not currently interesting. May be interested in the future.
                    /*case "icon", "iconRaw", "iconD", "iconReward", "iconShop", "recoveryHP", "recoveryMP", "sitAction",
                            "bodyRelMove", "only", "noDrop", "timeLimited", "accountSharable", "nickTag", "nickSkill", "endLotteryDate", "noFlip",
                            "noMoveToLocker", "soldInform", "purchaseShop", "flatRate", "limitMin", "protectTime", "maxDays", "reset", "replace",
                            "expireOnLogout", "max", "lvOptimum", "lvRange", "limitedLv", "tradeReward", "type", "floatType", "message", "pquest",
                            "bonusEXPRate", "notExtend", "stateChangeItem", "direction", "exGrade", "exGradeWeight", "effect", "bigSize", "nickSkillTimeLimited",
                            "useTradeBlock", "invisibleWeapon", "sitEmotion", "sitLeft", "tamingMob", "textInfo", "lv", "tradeAvailable", "pickUpBlock", "rewardItemID",
                            "autoPrice", "selectedSlot", "minusLevel", "addTime", "reqLevel", "waittime", "buffchair", "cooltime", "consumeitem",
                            "distanceX", "distanceY", "maxDiff", "maxDX", "levelDX", "maxLevel", "exp", "dropBlock", "dropExpireTime", "animation_create",
                            "animation_dropped", "noCancelMouse", "Rate", "unitPrice", "delayMsg", "bridlePropZeroMsg", "nomobMsg", "bridleProp", "bridlePropChg",
                            "bridleMsgType", "left", "right", "top", "bottom", "useDelay", "name", "uiData", "UI", "recoveryRate", "itemMsg",
                            "noRotateIcon", "endUseDate", "noSound", "slotMat", "isBgmOrEffect", "bgmPath", "repeat", "NoCancel", "rotateSpeed",
                            "gender", "life", "pickupItem", "add", "consumeHP", "longRange", "dropSweep", "pickupAll", "ignorePickup", "consumeMP",
                            "autoBuff", "smartPet", "giantPet", "shop", "recall", "autoSpeaking", "consumeCure", "meso", "maplepoint", "rate",
                            "overlap", "lt", "rb", "path4Top", "jumplevel", "slotIndex", "addDay", "incLEV", "cashTradeBlock", "dressUpgrade",
                            "skillEffectID", "emotion", "tradBlock", "tragetBlock", "scanTradeBlock", "mobPotion", "ignoreTendencyStatLimit",
                            "effectByItemID", "pachinko", "iconEnter", "iconLeave", "noMoveIcon", "noShadow", "preventslip", "warmsupport",
                            "reqCUC", "incCraft", "reqEquipLevelMin", "incPVPDamage", "successRates", "enchantCategory", "additionalSuccess", "level", "specialItem",
                            "exNew", "cuttable", "perfectReset", "resetRUC", "incMax", "noSuperior", "noRecovery", "reqMap", "random", "limit", "cantAccountSharable",
                            "LvUpWarning", "canAccountSharable", "canUseJob", "createPeriod", "iconLarge", "morphItem", "consumableFrom", "noExpend", "sample", "notPickUpByPet",
                            "sharableOnce", "bonusStageItem", "sampleOffsetY", "runOnPickup", "noSale", "skillCast", "activateCardSetID", "summonSoulMobID",
                            "cursor", "karma", "pointCost", "itemPoint", "sharedStatCostGrade", "levelVariation", "accountShareable", "extendLimit", "showMessage", "mcType",
                            "consumeItem", "hybrid", "mobId", "lvMin", "lvMax", "picture", "ratef", "time", "reqGuildLevel", "guild", "randEffect",
                            "accountShareTag", "removeEffect", "forcingItem", "fixFrameIdx", "buffItemID", "removeCharacterInfo", "nameInfo", "bgmInfo", "flip", "pos", "randomChair",
                            "maxLength", "continuity", "specificDX", "groupTWInfo", "face", "removeBody", "mesoChair", "towerBottom", "towerTop", "topOffset", "craftEXP", "willEXP",
                            "spec", "0", "mesomin", "mesomax", "mesostdev" -> {}*/
                }
            }
        }
    }

    private static void loadReqNode(Node reqNode, ItemData item) {
        if (reqNode != null) {
            for (Node req : XMLApi.getAllChildren(reqNode)) {
                String value = XMLApi.getNamedAttribute(req, "value");
                if (value != null) {
                    item.getReqItemIds().add(Integer.parseInt(value));
                }
            }
        }
    }

    private static void loadSpecNode(Node spec, ItemData item, Integer id) {
        if (spec != null) {
            for (Node specNode : XMLApi.getAllChildren(spec)) {
                String name = XMLApi.getNamedAttribute(specNode, "name");
                String value = XMLApi.getNamedAttribute(specNode, "value");
                switch (name) {
                    case "script" -> item.setScript(value);
                    case "npc" -> item.setScriptNPC(Integer.parseInt(value));
                    case "moveTo" -> item.setMoveTo(Integer.parseInt(value));
                    default -> {
                        SpecStat ss = SpecStat.getSpecStatByName(name);
                        if (ss != null && value != null) {
                            item.putSpecStat(ss, Integer.parseInt(value));
                        } else if (PRINT_WZ_UNK) {
                            logger.warning(String.format("Unhandled spec for id %d, name %s, value %s", id, name, value));
                        }
                    }
                }
            }
        }
    }

    private static void loadRewardNode(Node reward, ItemData item) {
        if (reward != null) {
            for (Node rewardNode : XMLApi.getAllChildren(reward)) {
                ItemRewardInfo iri = new ItemRewardInfo();
                for (Node rewardInfoNode : XMLApi.getAllChildren(rewardNode)) {
                    String name = XMLApi.getNamedAttribute(rewardInfoNode, "name");
                    String value = XMLApi.getNamedAttribute(rewardInfoNode, "value");
                    if (value == null) {
                        continue;
                    }
                    value = value.replace("\n", "").replace("\r", "")
                            .replace("\\n", "").replace("\\r", "") // unlucky
                            .replace("[R8]", "");
                    switch (name) {
                        case "count" -> iri.setCount(Integer.parseInt(value));
                        case "item" -> iri.setItemID(Integer.parseInt(value));
                        case "prob" -> iri.setProb(Double.parseDouble(value));
                        case "period" -> iri.setPeriod(Integer.parseInt(value));
                        case "effect", "Effect" -> iri.setEffect(value);
                    }
                }
                item.addItemReward(iri);
            }
        }
    }

    private static void loadMainNode(Node mainNode, EquipData equip) {
        for (Node n : XMLApi.getAllChildren(mainNode)) {
            String name = XMLApi.getNamedAttribute(n, "name");
            String value = XMLApi.getNamedAttribute(n, "value");
            List<Integer> options = new ArrayList<>(List.of(0, 0, 0));
            switch (name) {
                case "islot" -> equip.setISlot(value);
                case "vslot" -> equip.setVSlot(value);
                case "reqJob" -> equip.setRJob(Short.parseShort(value));
                case "reqLevel" -> equip.setRLevel(Short.parseShort(value));
                case "reqSTR" -> equip.setRStr(Short.parseShort(value));
                case "reqDEX" -> equip.setRDex(Short.parseShort(value));
                case "reqINT" -> equip.setRInt(Short.parseShort(value));
                case "reqLUK" -> equip.setRLuk(Short.parseShort(value));
                case "reqPOP" -> equip.setRPop(Short.parseShort(value));
                case "incSTR" -> equip.setIStr(Short.parseShort(value));
                case "incDEX" -> equip.setIDex(Short.parseShort(value));
                case "incINT" -> equip.setIInt(Short.parseShort(value));
                case "incLUK" -> equip.setILuk(Short.parseShort(value));
                case "incPDD" -> equip.setIPDD(Short.parseShort(value));
                case "incMDD" -> equip.setIMDD(Short.parseShort(value));
                case "incMHP" -> equip.setIMaxHp(Short.parseShort(value));
                case "incMMP" -> equip.setIMaxMp(Short.parseShort(value));
                case "incPAD" -> equip.setIPad(Short.parseShort(value));
                case "incMAD" -> equip.setIMad(Short.parseShort(value));
                case "incEVA" -> equip.setIEva(Short.parseShort(value));
                case "incACC" -> equip.setIAcc(Short.parseShort(value));
                case "incSpeed" -> equip.setISpeed(Short.parseShort(value));
                case "incJump" -> equip.setIJump(Short.parseShort(value));
                case "damR" -> equip.setDamR(Short.parseShort(value));
                case "statR" -> equip.setStatR(Short.parseShort(value));
                case "imdR" -> equip.setImdr(Short.parseShort(value));
                case "bdR" -> equip.setBdr(Short.parseShort(value));
                case "tuc" -> equip.setTuc(Short.parseShort(value));
                case "IUCMax" -> {
                    equip.setHasIUCMax(true);
                    equip.setIucMax(Short.parseShort(value));
                }
                case "setItemID" -> equip.setItemID(Integer.parseInt(value));
                case "price" -> equip.setPrice(Integer.parseInt(value));
                case "attackSpeed" -> equip.setAttackSpeed(Integer.parseInt(value));
                case "cash" -> equip.setCash(Integer.parseInt(value) != 0);
                case "expireOnLogout" -> equip.setExpireOnLogout(Integer.parseInt(value) != 0);
                case "exItem" -> equip.setExItem(Integer.parseInt(value) != 0);
                case "notSale" -> equip.setNotSale(Integer.parseInt(value) != 0);
                case "only" -> equip.setOnly(Integer.parseInt(value) != 0);
                case "tradeBlock" -> equip.setTradeBlock(Integer.parseInt(value) != 0);
                case "fixedPotential" -> equip.setFixedPotential(Integer.parseInt(value) != 0);
                case "noPotential" -> equip.setNoPotential(Integer.parseInt(value) != 0);
                case "bossReward" -> equip.setBossReward(Integer.parseInt(value) != 0);
                case "superiorEqp" -> equip.setSuperiorEqp(Integer.parseInt(value) != 0);
                case "reduceReq" -> equip.setIReduceReq((byte) Short.parseShort(value));
                case "fixedGrade" -> equip.setFixedGrade(Integer.parseInt(value));
                case "specialGrade" -> equip.setSpecialGrade(Integer.parseInt(value));
                case "level" -> {
                    // TODO: proper parsing, actual stats and skills for each level the equip gets
                    //logger.warning("Need to handle level parsing properly and such.. ");
                }
                case "option" -> {
                    for (Node whichOptionNode : XMLApi.getAllChildren(n)) {
                        Map<String, String> attributes = XMLApi.getAttributes(whichOptionNode);
                        int index = Integer.parseInt(attributes.get("name"));
                        Node optionNode = XMLApi.getFirstChildByNameBF(whichOptionNode, "option");
                        Map<String, String> optionAttr = XMLApi.getAttributes(optionNode);
                        options.set(index, Integer.parseInt(optionAttr.get("value")));
                    }
                }
            }
            equip.setOptions(options);
        }
    }

    public static void loadItemsFromWZ() {
        for (String subDir : ServerConstants.ITEM_SUB_WZ_DIRS) {
            File subFolderFile = new File(String.format("%s/%s", ServerConstants.ITEM_BASE_WZ_DIR, subDir));
            File[] files = subFolderFile.listFiles();
            for (File file : files) {
                Node node = XMLApi.getRoot(file);
                List<Node> nodes = XMLApi.getAllChildren(node);
                for (Node mainNode : XMLApi.getAllChildren(nodes.get(0))) {
                    String nodeName = XMLApi.getNamedAttribute(mainNode, "name");
                    if (!MapleUtils.isNumber(nodeName)) {
                        logger.error(String.format("%s is not a number.", nodeName));
                        continue;
                    }
                    int id = Integer.parseInt(nodeName);
                    ItemData item = new ItemData();
                    item.setItemId(id);
                    item.setInvType(InventoryType.getInvTypeByString(subDir));
                    Node infoNode = XMLApi.getFirstChildByNameBF(mainNode, "info");
                    // Loading info node data -
                    loadInfoNode(infoNode, item);
                    Node reqNode = XMLApi.getFirstChildByNameBF(mainNode, "req");
                    // Loading Req node data -
                    loadReqNode(reqNode, item);
                    Node spec = XMLApi.getFirstChildByNameBF(mainNode, "spec");
                    loadSpecNode(spec, item, id);
                    Node reward = XMLApi.getFirstChildByNameBF(mainNode, "reward");
                    // Load Reward node data -
                    loadRewardNode(reward, item);

                    // TODO: currently i'm not implementing this part, not sure it's necessary
                    //item.setSkillId(getSkillIdByItemId(id));

                    // Add the item into the Map -
                    items.put(item.getItemId(), item);
                }
            }
        }
    }

    public static void loadEquipsFromWZ() {
        //TODO: need to refactor heavily !
        for (String subDir : ServerConstants.EQUIP_SUB_WZ_DIR) {
            File subFolderFile = new File(String.format("%s/%s", ServerConstants.EQUIP_BASE_WZ_DIR, subDir));
            File[] files = subFolderFile.listFiles();
            if (files == null) {
                if (PRINT_WZ_UNK) {
                    logger.warning("Null subfolder: " + subDir);
                }
                break;
            }
            for (File file : files) {
                Node node = XMLApi.getRoot(file);
                List<Node> nodes = XMLApi.getAllChildren(node);
                for (Node mainNode : XMLApi.getAllChildren(nodes.getFirst())) {
                    Map<String, String> attributes = XMLApi.getAttributes(mainNode);
                    String mainName = attributes.get("name");
                    if (mainName != null) {
                        int itemId = Integer.parseInt(XMLApi.getAttributes(nodes.getFirst()).get("name").replace(".img", ""));
                        EquipData equip = new EquipData();
                        equip.setItemID(itemId);
                        // Load Main node data into equip data -
                        loadMainNode(mainNode, equip);
                        // Add to the cache of equips -
                        equips.putIfAbsent(equip.getItemID(), equip);
                    }
                }
            }
        }
    }

    private static void loadItemOptionInfoNode(@NotNull Node infoNode,
                                               @NotNull ItemOptionData itemOption) {
        for (Node infoChild : XMLApi.getAllChildren(infoNode)) {
            String name = XMLApi.getNamedAttribute(infoChild, "name");
            String value = XMLApi.getNamedAttribute(infoChild, "value");
            switch (name) {
                case "optionType" -> itemOption.setOptionType(Integer.parseInt(value));
                case "reqLevel" -> itemOption.setReqLevel(Integer.parseInt(value));
            }
        }
    }

    private static void addBossStatValuesToItemOption(@NotNull Node levelChild,
                                                      int level,
                                                      int value,
                                                      @NotNull ItemOptionData itemOption) {
        Node incDamgNode = XMLApi.getFirstChildByNameDF(levelChild, "incDAMr");
        if (incDamgNode != null) {
            value = Integer.parseInt(XMLApi.getNamedAttribute(incDamgNode, "value"));
        }
        itemOption.addStatValue(level, BaseStat.bd, value);
    }

    private static void loadItemOptionLevelNode(@NotNull Node levelNode,
                                                @NotNull ItemOptionData itemOption) {
        for (Node levelChild : XMLApi.getAllChildren(levelNode)) {
            int level = Integer.parseInt(XMLApi.getNamedAttribute(levelChild, "name"));
            for (Node levelInfoNode : XMLApi.getAllChildren(levelChild)) {
                String name = XMLApi.getNamedAttribute(levelInfoNode, "name");
                String stringValue = XMLApi.getNamedAttribute(levelInfoNode, "value");
                int value = 0;
                if (MapleUtils.isNumber(stringValue)) {
                    value = Integer.parseInt(stringValue);
                }
                switch (name) {
                    case "incSTR" -> itemOption.addStatValue(level, BaseStat.str, value);
                    case "incDEX" -> itemOption.addStatValue(level, BaseStat.dex, value);
                    case "incINT" -> itemOption.addStatValue(level, BaseStat.inte, value);
                    case "incLUK" -> itemOption.addStatValue(level, BaseStat.luk, value);
                    case "incMHP" -> itemOption.addStatValue(level, BaseStat.mhp, value);
                    case "incMMP" -> itemOption.addStatValue(level, BaseStat.mmp, value);
                    case "incACC" -> itemOption.addStatValue(level, BaseStat.acc, value);
                    case "incEVA" -> itemOption.addStatValue(level, BaseStat.eva, value);
                    case "incSpeed" -> itemOption.addStatValue(level, BaseStat.speed, value);
                    case "incJump" -> itemOption.addStatValue(level, BaseStat.jump, value);
                    case "incPAD" -> itemOption.addStatValue(level, BaseStat.pad, value);
                    case "incMAD" -> itemOption.addStatValue(level, BaseStat.mad, value);
                    case "incPDD" -> itemOption.addStatValue(level, BaseStat.pdd, value);
                    case "incMDD" -> itemOption.addStatValue(level, BaseStat.mdd, value);
                    case "incCr" -> itemOption.addStatValue(level, BaseStat.cr, value);
                    case "incPADr" -> itemOption.addStatValue(level, BaseStat.padR, value);
                    case "incMADr" -> itemOption.addStatValue(level, BaseStat.madR, value);
                    case "incSTRr" -> itemOption.addStatValue(level, BaseStat.strR, value);
                    case "incDEXr" -> itemOption.addStatValue(level, BaseStat.dexR, value);
                    case "incINTr" -> itemOption.addStatValue(level, BaseStat.intR, value);
                    case "incLUKr" -> itemOption.addStatValue(level, BaseStat.lukR, value);
                    case "ignoreTargetDEF" -> itemOption.addStatValue(level, BaseStat.ied, value);
                    case "incDAMr" -> itemOption.addStatValue(level, BaseStat.fd, value);
                    case "boss" -> addBossStatValuesToItemOption(levelChild, level, value, itemOption);
                    case "incAllskill" -> itemOption.addStatValue(level, BaseStat.incAllSkill, value);
                    case "incMHPr" -> itemOption.addStatValue(level, BaseStat.mhpR, value);
                    case "incMMPr" -> itemOption.addStatValue(level, BaseStat.mmpR, value);
                    case "incACCr" -> itemOption.addStatValue(level, BaseStat.accR, value);
                    case "incEVAr" -> itemOption.addStatValue(level, BaseStat.evaR, value);
                    case "incPDDr" -> itemOption.addStatValue(level, BaseStat.pddR, value);
                    case "incMDDr" -> itemOption.addStatValue(level, BaseStat.mddR, value);
                    case "RecoveryHP" -> itemOption.addStatValue(level, BaseStat.hpRecovery, value);
                    case "RecoveryMP" -> itemOption.addStatValue(level, BaseStat.mpRecovery, value);
                    case "incMaxDamage" -> itemOption.addStatValue(level, BaseStat.damageOver, value);
                    case "incSTRlv" -> itemOption.addStatValue(level, BaseStat.strLv, value);
                    case "incDEXlv" -> itemOption.addStatValue(level, BaseStat.dexLv, value);
                    case "incINTlv" -> itemOption.addStatValue(level, BaseStat.intLv, value);
                    case "incLUKlv" -> itemOption.addStatValue(level, BaseStat.lukLv, value);
                    case "RecoveryUP" -> itemOption.addStatValue(level, BaseStat.recoveryUp, value);
                    case "incTerR" -> itemOption.addStatValue(level, BaseStat.ter, value);
                    case "incAsrR" -> itemOption.addStatValue(level, BaseStat.asr, value);
                    case "incEXPr" -> itemOption.addStatValue(level, BaseStat.expR, value);
                    case "mpconReduce" -> itemOption.addStatValue(level, BaseStat.mpconReduce, value);
                    case "reduceCooltime" -> itemOption.addStatValue(level, BaseStat.reduceCooltime, value);
                    case "incMesoProp" -> itemOption.addStatValue(level, BaseStat.mesoR, value);
                    case "incRewardProp" -> itemOption.addStatValue(level, BaseStat.dropR, value);
                    case "incCriticaldamageMin" -> itemOption.addStatValue(level, BaseStat.minCd, value);
                    case "incCriticaldamageMax" -> itemOption.addStatValue(level, BaseStat.maxCd, value);
                    case "incPADlv" -> itemOption.addStatValue(level, BaseStat.padLv, value);
                    case "incMADlv" -> itemOption.addStatValue(level, BaseStat.madLv, value);
                    case "incMHPlv" -> itemOption.addStatValue(level, BaseStat.mhpLv, value);
                    case "incMMPlv" -> itemOption.addStatValue(level, BaseStat.mmpLv, value);
                    case "prop" -> itemOption.addMiscValue(level, ItemOptionType.prop, value);
                    case "face" -> itemOption.addMiscValue(level, ItemOptionType.face, value);
                    case "time" -> itemOption.addMiscValue(level, ItemOptionType.time, value);
                    case "HP" -> itemOption.addMiscValue(level, ItemOptionType.HP, value);
                    case "MP" -> itemOption.addMiscValue(level, ItemOptionType.MP, value);
                    case "attackType" -> itemOption.addMiscValue(level, ItemOptionType.attackType, value);
                    case "level" -> itemOption.addMiscValue(level, ItemOptionType.level, value);
                    case "ignoreDAM" -> itemOption.addMiscValue(level, ItemOptionType.ignoreDAM, value);
                    case "ignoreDAMr" -> itemOption.addMiscValue(level, ItemOptionType.ignoreDAMr, value);
                    case "DAMreflect" -> itemOption.addMiscValue(level, ItemOptionType.DAMreflect, value);
                }
            }
        }
    }

    public static void loadItemOptionsFromWZ() {
        File file = new File(ITEM_BASE_WZ_DIR + "/ItemOption.img.xml");
        List<Node> nodes = XMLApi.getAllChildren(XMLApi.getRoot(file));
        for (Node mainNode : XMLApi.getAllChildren(nodes.getFirst())) {
            ItemOptionData itemOption = new ItemOptionData();
            String nodeName = XMLApi.getNamedAttribute(mainNode, "name");
            itemOption.setId(Integer.parseInt(nodeName));
            Node infoNode = XMLApi.getFirstChildByNameBF(mainNode, "info");
            if (infoNode != null) {
                loadItemOptionInfoNode(infoNode, itemOption);
            }
            Node levelNode = XMLApi.getFirstChildByNameBF(mainNode, "level");
            if (levelNode != null) {
                loadItemOptionLevelNode(levelNode, itemOption);
            }
            itemOptions.put(itemOption.getId(), itemOption);
        }
    }

    public static void loadItemOptionData() {
        logger.startLoad("WZ", "Item Option Data");
        long startTime = System.currentTimeMillis();
        loadItemOptionsFromWZ();
        logger.finishLoad(itemOptions.size(), "WZ", "Items Option Data", ((System.currentTimeMillis() - startTime) / 1000.0));
    }

    public static void loadItemData() {
        logger.startLoad("WZ", "Item Data");
        long startTime = System.currentTimeMillis();
        loadItemsFromWZ();
        logger.finishLoad(items.size(), "WZ", "Items Data", ((System.currentTimeMillis() - startTime) / 1000.0));
    }

    public static void loadEquipData() {
        logger.serverNotice("Start loading Equip data...");
        logger.startLoad("WZ", "Equip Data");
        long startTime = System.currentTimeMillis();
        loadEquipsFromWZ();
        logger.finishLoad(equips.size(), "WZ", "Equip Data", ((System.currentTimeMillis() - startTime) / 1000.0));
    }

    private static void exportItemsToJson() {
        logger.serverNotice("Start creating the JSONs for items..");
        MapleUtils.makeDirIfAbsent(JSON_DIR);
        MapleUtils.makeDirIfAbsent(ITEM_JSON_DIR);
        items.values().forEach(item -> JsonUtils.createJsonFile(item, ITEM_JSON_DIR + item.getItemId() + ".json"));
        logger.serverNotice("~ Finished creating the items JSON files! ~");
    }

    private static void exportItemOptionsToJson() {
        logger.serverNotice("Start creating the JSONs for item options..");
        MapleUtils.makeDirIfAbsent(JSON_DIR);
        MapleUtils.makeDirIfAbsent(ITEM_OPTION_JSON_DIR);
        itemOptions.values().forEach(item -> JsonUtils.createJsonFile(item, ITEM_OPTION_JSON_DIR + item.getId() + ".json"));
        logger.serverNotice("~ Finished creating the item options JSON files! ~");
    }

    private static void exportEquipsToJson() {
        logger.serverNotice("Start creating the JSONs for equips..");
        MapleUtils.makeDirIfAbsent(JSON_DIR);
        MapleUtils.makeDirIfAbsent(EQUIP_JSON_DIR);
        equips.values().forEach(equip -> JsonUtils.createJsonFile(equip, EQUIP_JSON_DIR + equip.getItemID() + ".json"));
        logger.serverNotice("~ Finished creating the equips JSON files! ~");
    }

    public static void exportDataToJson() {
        exportItemsToJson();
        exportItemOptionsToJson();
        exportEquipsToJson();
    }

    public static void loadJsonItems() {
        long startTime = System.currentTimeMillis();
        File dir = new File(ITEM_JSON_DIR);
        File[] files = dir.listFiles();
        logger.startLoad("JSON", "Item Data");
        if (files != null) {
            for (File file : files) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    ItemData item = mapper.readValue(file, ItemData.class);
                    items.put(item.getItemId(), item);
                } catch (Exception e) {
                    logger.error("Error occurred while trying to load the file: " + file.getName());
                    e.printStackTrace();
                }
            }
            logger.finishLoad(files.length, "JSON", "Item Data", ((System.currentTimeMillis() - startTime) / 1000.0));
        } else {
            logger.error("Didn't found items JSONs to load!");
        }
    }

    public static void loadJsonEquips() {
        long startTime = System.currentTimeMillis();
        File dir = new File(EQUIP_JSON_DIR);
        File[] files = dir.listFiles();
        logger.startLoad("JSON", "Equip Data");
        if (files != null) {
            for (File file : files) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    EquipData item = mapper.readValue(file, EquipData.class);
                    equips.put(item.getItemID(), item);
                } catch (Exception e) {
                    logger.error("Error occurred while trying to load the file: " + file.getName());
                    e.printStackTrace();
                }
            }
            logger.finishLoad(files.length, "JSON", "Equip Data", ((System.currentTimeMillis() - startTime) / 1000.0));
        } else {
            logger.error("Didn't found equips JSONs to load!");
        }
    }

    public static void loadJsonItemOptions() {
        long startTime = System.currentTimeMillis();
        File dir = new File(ITEM_OPTION_JSON_DIR);
        File[] files = dir.listFiles();
        logger.startLoad("JSON", "Item Option Data");
        if (files != null) {
            for (File file : files) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    ItemOptionData itemOption = mapper.readValue(file, ItemOptionData.class);
                    itemOptions.put(itemOption.getId(), itemOption);
                } catch (Exception e) {
                    logger.error("Error occurred while trying to load the file: " + file.getName());
                    e.printStackTrace();
                }
            }
            logger.finishLoad(files.length, "JSON", "Item Option Data", ((System.currentTimeMillis() - startTime) / 1000.0));
        } else {
            logger.error("Didn't found items JSONs to load!");
        }
    }

    private static boolean isJsonDataExist() {
        File itemDir = new File(ITEM_JSON_DIR);
        File itemOptionDir = new File(ITEM_OPTION_JSON_DIR);
        File equipDir = new File(EQUIP_JSON_DIR);
        return itemDir.exists() && equipDir.exists() && itemOptionDir.exists();
    }

    public static void load() {
        if (isJsonDataExist()) {
            loadJsonItems();
            loadJsonItemOptions();
            loadJsonEquips();
        } else {
            loadItemData();
            loadItemOptionData();
            loadEquipData();
            exportDataToJson();
        }
    }
}
