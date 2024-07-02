package com.dori.SpringStory.client.commands;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.constants.GameConstants;
import com.dori.SpringStory.enums.*;
import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.inventory.Inventory;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.scripts.handlers.ScriptHandler;
import com.dori.SpringStory.services.StringDataService;
import com.dori.SpringStory.temporaryStats.characters.BuffDataHandler;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.Portal;
import com.dori.SpringStory.dataHandlers.ItemDataHandler;
import com.dori.SpringStory.dataHandlers.MapDataHandler;
import com.dori.SpringStory.dataHandlers.MobDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.MobData;
import com.dori.SpringStory.dataHandlers.dataEntities.StringData;
import lombok.NoArgsConstructor;

import java.util.*;

import static com.dori.SpringStory.constants.GameConstants.MAX_MESO;
import static com.dori.SpringStory.enums.InventoryOperation.Remove;

@NoArgsConstructor
public class AdminCommands {
    // Logger -
    private static final Logger logger = new Logger(AdminCommands.class);

    private static AdminCommands instance;

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static AdminCommands getInstance() {
        if (instance == null) {
            instance = new AdminCommands();
        }
        return instance;
    }

    @Command(names = {"help"}, requiredPermission = AccountType.GameMaster)
    public static void help(MapleChar chr, List<String> args) {
        logger.debug("ADMIN Testing!");
        logger.debug("chr: " + chr.getId());
        logger.debug("args: " + args.toString());
    }

    @Command(names = {"lvl", "level", "setlvl"}, requiredPermission = AccountType.GameMaster)
    public static void levelUp(MapleChar chr, List<String> args) {
        int lvl = Integer.parseInt(args.getFirst());
        int amountOfLevels = lvl - chr.getLevel();
        if (amountOfLevels > 0) {
            chr.lvlUp(amountOfLevels);
        } else {
            chr.setLevel(lvl);
            chr.updateStat(Stat.Level, lvl);
            chr.fullHeal();
        }

        chr.setExp(0);
        chr.updateStat(Stat.Exp, 0);
    }

    @Command(names = {"goto"}, requiredPermission = AccountType.GameMaster)
    public static void goToMap(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            Field toField = chr.getMapleClient().getMapleChannelInstance().getField(args.getFirst());
            if (toField != null) {
                Portal targetPortal = toField.findDefaultPortal();
                chr.warp(toField, targetPortal);
            } else {
                chr.message("Un-valid field name!", ChatType.SpeakerChannel);
            }
        } else {
            chr.message("Need to choose map from the list -", ChatType.Notice);
            chr.message(MapDataHandler.getGoToMaps().keySet().toString(), ChatType.Notice);
        }
    }

    @Command(names = {"warp"}, requiredPermission = AccountType.GameMaster)
    public static void warp(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            Field toField = chr.getMapleClient().getMapleChannelInstance().getField(Integer.parseInt(args.getFirst()));
            if (toField != null) {
                Portal targetPortal = toField.findDefaultPortal();
                chr.warp(toField, targetPortal);
            } else {
                chr.message("Un-valid Map ID!", ChatType.SpeakerChannel);
            }
        } else {
            chr.message("Need to choose Map ID", ChatType.Notice);
        }
    }

    @Command(names = {"job", "setJob"}, requiredPermission = AccountType.GameMaster)
    public static void job(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            int id = Short.parseShort(args.getFirst());
            chr.setJob(id);
        }
    }

    @Command(names = {"find", "search"}, requiredPermission = AccountType.GameMaster)
    public static void find(MapleChar chr, List<String> args) {
        if (args.size() >= 2) {
            StringDataType type = StringDataType.findTypeByName(args.getFirst());
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= args.size() - 1; i++) {
                sb.append(args.get(i)).append(" ");
            }
            sb.deleteCharAt(sb.length() - 1);
            String name = sb.toString();

            if (type != StringDataType.None) {
                Optional<List<StringData>> results = StringDataService.getInstance().findStringByNameAndType(name, type);
                chr.message("Query Result: ", ChatType.SpeakerWorld);
                results.ifPresent(resultsData ->
                        resultsData.forEach(entity ->
                                chr.message(entity.toString(), ChatType.SpeakerWorld)));
            } else {
                chr.message("Un-valid Search type! only can choose: Mob | Map | Item | Skill | NPC <name> ", ChatType.SpeakerChannel);
            }
        } else {
            chr.message("Un-valid Search type! only can choose: Mob | Map | Item | Skill | NPC <name> ", ChatType.SpeakerChannel);
        }
    }

    @Command(names = {"query"}, requiredPermission = AccountType.GameMaster)
    public static void query(MapleChar chr, List<String> args) {
        ScriptHandler.getInstance().handleNpcScript(chr, 9401771);
    }

    @Command(names = {"say", "speak"}, requiredPermission = AccountType.GameMaster)
    public static void say(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            args.forEach(word -> sb.append(word).append(" "));

            chr.noticeMsg(sb.toString());
        }
    }

    @Command(names = {"spawn"}, requiredPermission = AccountType.GameMaster)
    public static void spawn(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            int id = Integer.parseInt(args.get(0));
            int count = 1;
            if (args.size() >= 2) {
                count = Integer.parseInt(args.get(1));
            }
            MobData mobData = MobDataHandler.getMobDataByID(id);
            if (mobData != null) {
                for (int i = 0; i < count; i++) {
                    chr.getField().spawnMobById(id, chr);
                }
            }
        }
    }

    @Command(names = {"setstr", "setStr", "str"}, requiredPermission = AccountType.GameMaster)
    public static void setStrength(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            String amountToSet = args.getFirst();
            if (MapleUtils.isNumber(amountToSet)) {
                int desiredAmount = Integer.parseInt(amountToSet);
                int finalAmount = desiredAmount > Short.MAX_VALUE ? Short.MAX_VALUE : desiredAmount;
                chr.setNStr(finalAmount);
                chr.updateStat(Stat.Str, finalAmount);
                chr.message("Update Str to: " + finalAmount, ChatType.GameDesc);
            }
        }
    }

    @Command(names = {"setdex", "setDex", "dex"}, requiredPermission = AccountType.GameMaster)
    public static void setDexterity(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            String amountToSet = args.getFirst();
            if (MapleUtils.isNumber(amountToSet)) {
                int desiredAmount = Integer.parseInt(amountToSet);
                int finalAmount = desiredAmount > Short.MAX_VALUE ? Short.MAX_VALUE : desiredAmount;
                chr.setNDex(finalAmount);
                chr.updateStat(Stat.Dex, finalAmount);
                chr.message("Update Dex to: " + finalAmount, ChatType.GameDesc);
            }
        }
    }

    @Command(names = {"setint", "setint", "int"}, requiredPermission = AccountType.GameMaster)
    public static void setIntelligence(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            String amountToSet = args.getFirst();
            if (MapleUtils.isNumber(amountToSet)) {
                int desiredAmount = Integer.parseInt(amountToSet);
                int finalAmount = desiredAmount > Short.MAX_VALUE ? Short.MAX_VALUE : desiredAmount;
                chr.setNInt(finalAmount);
                chr.updateStat(Stat.Inte, finalAmount);
                chr.message("Update int to: " + finalAmount, ChatType.GameDesc);
            }
        }
    }

    @Command(names = {"setluk", "setLuk", "luk"}, requiredPermission = AccountType.GameMaster)
    public static void setLuk(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            String amountToSet = args.getFirst();
            if (MapleUtils.isNumber(amountToSet)) {
                int desiredAmount = Integer.parseInt(amountToSet);
                int finalAmount = desiredAmount > Short.MAX_VALUE ? Short.MAX_VALUE : desiredAmount;
                chr.setNLuk(finalAmount);
                chr.updateStat(Stat.Luk, finalAmount);
                chr.message("Update luk to: " + finalAmount, ChatType.GameDesc);
            }
        }
    }

    @Command(names = {"sethp", "setHp", "hp"}, requiredPermission = AccountType.GameMaster)
    public static void setHp(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            String amountToSet = args.getFirst();
            if (MapleUtils.isNumber(amountToSet)) {
                int desiredAmount = Integer.parseInt(amountToSet);
                int finalAmount = Math.min(desiredAmount, GameConstants.MAX_HP);
                chr.setMaxHp(finalAmount);
                chr.updateStat(Stat.MaxHp, finalAmount);
                chr.message("Update Max HP to: " + finalAmount, ChatType.GameDesc);
            }
        }
    }

    @Command(names = {"setmp", "setMp", "mp"}, requiredPermission = AccountType.GameMaster)
    public static void setMp(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            String amountToSet = args.getFirst();
            if (MapleUtils.isNumber(amountToSet)) {
                int desiredAmount = Integer.parseInt(amountToSet);
                int finalAmount = Math.min(desiredAmount, GameConstants.MAX_MP);
                chr.setMaxMp(finalAmount);
                chr.updateStat(Stat.MaxMp, finalAmount);
                chr.message("Update Max MP to: " + finalAmount, ChatType.GameDesc);
            }
        }
    }

    @Command(names = {"setap", "setAp", "ap"}, requiredPermission = AccountType.GameMaster)
    public static void setAp(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            String amountToSet = args.getFirst();
            if (MapleUtils.isNumber(amountToSet)) {
                int desiredAmount = Integer.parseInt(amountToSet);
                int finalAmount = Math.min(desiredAmount, Short.MAX_VALUE);
                chr.setAp(finalAmount);
                chr.updateStat(Stat.AbilityPoint, finalAmount);
                chr.message("Update AP to: " + finalAmount, ChatType.GameDesc);
            }
        }
    }

    @Command(names = {"setsp", "setSp", "sp"}, requiredPermission = AccountType.GameMaster)
    public static void setSp(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            String amountToSet = args.getFirst();
            if (MapleUtils.isNumber(amountToSet)) {
                int desiredAmount = Integer.parseInt(amountToSet);
                int finalAmount = Math.min(desiredAmount, Short.MAX_VALUE);
                chr.setSp(finalAmount);
                chr.updateStat(Stat.SkillPoint, finalAmount);
                chr.message("Update SP to: " + finalAmount, ChatType.GameDesc);
            }
        }
    }

    @Command(names = {"maxStats", "maxstat", "max"}, requiredPermission = AccountType.GameMaster)
    public static void MaxStats(MapleChar chr, List<String> args) {
        Map<Stat, Object> stats = new HashMap<>();
        stats.put(Stat.Str, Short.MAX_VALUE);
        chr.setNStr(Short.MAX_VALUE);
        stats.put(Stat.Dex, Short.MAX_VALUE);
        chr.setNDex(Short.MAX_VALUE);
        stats.put(Stat.Inte, Short.MAX_VALUE);
        chr.setNInt(Short.MAX_VALUE);
        stats.put(Stat.Luk, Short.MAX_VALUE);
        chr.setNLuk(Short.MAX_VALUE);
        //stats.put(Stat.Level, MAX_LVL);
        chr.setMeso(MAX_MESO);
        stats.put(Stat.Money, MAX_MESO);
        chr.changeStats(stats);
        chr.message("Update stats to max!", ChatType.GameDesc);
    }

    @Command(names = {"mobStats", "mobs", ",mobStatus"}, requiredPermission = AccountType.GameMaster)
    public static void MobStatus(MapleChar chr, List<String> args) {
        Field field = chr.getField();
        chr.message("Total mobs: " + field.getMobs().size(), ChatType.SpeakerWorld);
        field.getMobs().values().forEach(mob -> chr.message(mob.toString(), ChatType.SpeakerWorld));
    }

    @Command(names = {"heal", "fullheal"}, requiredPermission = AccountType.GameMaster)
    public static void heal(MapleChar chr, List<String> args) {
        chr.fullHeal();
    }

    @Command(names = {"item", "getitem"}, requiredPermission = AccountType.GameMaster)
    public static void item(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            int itemID = Integer.parseInt(args.getFirst());
            Equip equip = ItemDataHandler.getEquipByID(itemID);
            if (equip != null) {
                chr.addEquip(equip);
            } else {
                Item item = ItemDataHandler.getItemByID(itemID);
                if (item != null) {
                    if (args.size() > 1) {
                        int amount = Integer.parseInt(args.get(1));
                        item.setQuantity(amount > 0 ? amount : 1);
                    }
                    chr.addItem(item);
                }
            }
        }
    }

    @Command(names = {"reloadbuffdata", "reloadBuffsData"}, requiredPermission = AccountType.GameMaster)
    public static void reloadBuffsData(MapleChar chr, List<String> args) {
        BuffDataHandler.reloadBuffData();
        chr.message("Reloaded Buffs!", ChatType.GameDesc);
    }

    @Command(names = {"weapon", "armiger"}, requiredPermission = AccountType.GameMaster)
    public static void armiger(MapleChar chr, List<String> args) {
        // One-Handed Sword | Maple Sword - 1302020
        // Two-Handed Sword | Wooden Baseball Bat - 1402009
        // One-Handed Axe | Double Axe - 1312000
        // Two-Handed Axe | Maple Dragon Axe - 1412011
        // One-Handed Blunt | Wizet Secret Agent Suitcase - 1322013
        // Two-Handed Blunt | Wooden Mallet - 1422000
        // Bow | Maple Bow - 1452016
        // Crossbow | Maple Crow - 1462014
        // Claw | Maple Claw - 1472030
        // Dagger | Dragon's Tail - 1332023
        // Spear | Maple Impaler - 1432012
        // Polearm | Yellow Valentine Rose - 1442047
        // Wand | Wooden Wand - 1372005
        // Staff | Wooden Staff - 1382000
        // Knuckle | Steel Knuckler - 1482000
        // Gun | Pistol - 1492000
        // Katara | Snowy Earth Katara - 1342023
        // Crystal Ilbi throwing star - 2070016
        int[] weaponListToAdd = {1302020, 1402009, 1312000, 1412011, 1322013, 1422000, 1452016, 1462014, 1472030, 1332023, 1432012, 1442047, 1372005, 1382000, 1482000, 1492000, 1342023};
        for (int weaponID : weaponListToAdd) {
            Equip equip = ItemDataHandler.getEquipByID(weaponID);
            if (equip != null) {
                chr.addEquip(equip);
            }
        }
        chr.message("~Armiger unleashed~", ChatType.SpeakerWorld);
    }

    @Command(names = {"test"}, requiredPermission = AccountType.GameMaster)
    public static void test(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
//            String cmdType = args.getFirst();
//            if (MapleUtils.isNumber(cmdType)) {
//                int cmdTypeFlag = Integer.parseInt(cmdType);
//                chr.write(CField.adminResult(cmdTypeFlag, false));
//            }
        }
    }

    @Command(names = {"invdata"}, requiredPermission = AccountType.GameMaster)
    public static void inventoryData(MapleChar chr, List<String> args) {
        chr.getInventoryByType(InventoryType.ETC)
                .getItems()
                .forEach(item -> chr.message(item.getItemId() + " : bagIndex - " + item.getBagIndex() + " | quantity: " + item.getQuantity(), ChatType.SpeakerWorld));
    }

    @Command(names = {"stars"}, requiredPermission = AccountType.GameMaster)
    public static void getStars(MapleChar chr, List<String> args) {
        Item crystalIlbi = ItemDataHandler.getItemByID(2070016);
        if (crystalIlbi != null) {
            crystalIlbi.setQuantity(200);
            chr.addItem(crystalIlbi);
        }
        Item balancedFury = ItemDataHandler.getItemByID(2070018);
        if (balancedFury != null) {
            balancedFury.setQuantity(200);
            chr.addItem(balancedFury);
        }
    }

    @Command(names = {"clearinv", "clearInv", "clearInventory"}, requiredPermission = AccountType.GameMaster)
    public static void clearInventory(MapleChar chr, List<String> args) {
        if (!args.isEmpty() && args.getFirst() instanceof String inventoryType && !inventoryType.isEmpty()) {
            Inventory chosenInventory = chr.getInventoryByType(InventoryType.getInvTypeByString(inventoryType));
            if (chosenInventory != null && !chosenInventory.getItems().isEmpty()) {
                List<Item> itemsToRemove = new ArrayList<>(chosenInventory.getItems());
                // First clear server side the items -
                chosenInventory.getItems().clear();
                // Update the remove in the client -
                itemsToRemove.forEach(item -> chr.write(CWvsContext.inventoryOperation(true, Remove, (short) item.getBagIndex(), (short) 0, item)));
                // Clear the ref anyway -
                itemsToRemove.clear();
            }
        }
    }

    @Command(names = {"vac", "cleardrops", "clearDrops"}, requiredPermission = AccountType.GameMaster)
    public static void vac(MapleChar chr, List<String> args) {
        Field field = chr.getField();
        field.getDrops().values().removeIf(drop -> {
            field.removeDrop(drop.getId(), chr.getId(), 0);
            chr.pickupItem(drop);
            return true;
        });
        field.getDrops().clear();
    }

    @Command(names = {"style"}, requiredPermission = AccountType.GameMaster)
    public static void style(MapleChar chr, List<String> args) {
        ScriptHandler.getInstance().handleNpcScript(chr, 9401769);
    }

    @Command(names = {"potential"}, requiredPermission = AccountType.GameMaster)
    public static void cube(MapleChar chr, List<String> args) {
        Item potentialScroll = ItemDataHandler.getItemByID(2460003);
        if (potentialScroll != null) {
            potentialScroll.setQuantity(100);
            chr.addItem(potentialScroll);
        }
        Item magnifyGlass = ItemDataHandler.getItemByID(2049400);
        if (magnifyGlass != null) {
            magnifyGlass.setQuantity(100);
            chr.addItem(magnifyGlass);
        }
    }

    @Command(names = {"kill", "killall", "nuke"}, requiredPermission = AccountType.GameMaster)
    public static void killALl(MapleChar chr, List<String> args) {
        int amount = chr.getField().getMobs().size();
        chr.getField().getMobs().forEach((id, mob) -> mob.die(false));
        chr.message("Killed " + amount + " mobs!", ChatType.GameDesc);
    }

    @Command(names = {"proitem", "hackitem", "msi"}, requiredPermission = AccountType.GameMaster)
    public static void proItem(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            int itemID = Integer.parseInt(args.getFirst());
            Equip equip = ItemDataHandler.getEquipByID(itemID);
            if (equip != null) {
                equip.setIStr(Short.MAX_VALUE);
                equip.setIDex(Short.MAX_VALUE);
                equip.setIInt(Short.MAX_VALUE);
                equip.setILuk(Short.MAX_VALUE);
                equip.setIMad(Short.MAX_VALUE);
                equip.setIMDD(Short.MAX_VALUE);
                equip.setIMaxHp(Short.MAX_VALUE);
                equip.setIMaxMp(Short.MAX_VALUE);
                equip.setIPad(Short.MAX_VALUE);
                equip.setIPDD(Short.MAX_VALUE);
                equip.setImdr(Short.MAX_VALUE);
                chr.addEquip(equip);
            }
        }
    }

    @Command(names = {"resetap", "resetAp"}, requiredPermission = AccountType.GameMaster)
    public static void resetAp(MapleChar chr, List<String> args) {
        chr.setAp(4);
        chr.updateStat(Stat.AbilityPoint, 4);
        chr.message("Reset AP back to: " + 4, ChatType.GameDesc);
    }

}
