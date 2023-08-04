package com.dori.SpringStory.client.commands;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.*;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.services.StringDataService;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.mob.Mob;
import com.dori.SpringStory.world.fieldEntities.Portal;
import com.dori.SpringStory.wzHandlers.MapDataHandler;
import com.dori.SpringStory.wzHandlers.MobDataHandler;
import com.dori.SpringStory.wzHandlers.wzEntities.MobData;
import com.dori.SpringStory.wzHandlers.wzEntities.StringData;

import java.util.*;

public class AdminCommands {
    // Logger -
    private static final Logger logger = new Logger(AdminCommands.class);

    private static AdminCommands instance;

    private AdminCommands() {
    }

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
        int lvl = Integer.parseInt(args.get(0));
        int amountOfLevels = lvl - chr.getLevel();
        if (amountOfLevels > 0) {
            chr.lvlUp(amountOfLevels);
        } else {
            chr.setLevel(lvl);
            chr.updateStat(Stat.Level, lvl);
            chr.fullHeal();
        }
    }

    @Command(names = {"goto"}, requiredPermission = AccountType.GameMaster)
    public static void goToMap(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            Field toField = MapDataHandler.getMapByName(args.get(0));
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

    @Command(names = {"job", "setJob"}, requiredPermission = AccountType.GameMaster)
    public static void job(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            int id = Short.parseShort(args.get(0));
            Job job = Job.getJobById(id);
            if (job != null) {
                chr.message("Change " + chr.getName() + " to the Job: " + job.name(), ChatType.GameDesc);
                chr.setJob(id);
                chr.updateStat(Stat.SubJob, id);
            } else {
                chr.message("Didn't receive a valid Job id!", ChatType.SpeakerChannel);
            }
        }
    }

    @Command(names = {"find", "search"}, requiredPermission = AccountType.GameMaster)
    public static void find(MapleChar chr, List<String> args) {
        if (args.size() >= 2) {
            StringDataType type = StringDataType.findTypeByName(args.get(0));
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
                chr.message("Un-valid Search type! only can choose: Mob | Map | Item | Skill | NPC _name_ ", ChatType.SpeakerChannel);
            }
        } else {
            chr.message("Un-valid Search type! only can choose: Mob | Map | Item | Skill | NPC _name_ ", ChatType.SpeakerChannel);
        }
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
        if (args.size() >= 1) {
            int id = Integer.parseInt(args.get(0));
            int count = 1;
            if (args.size() >= 2) {
                count = Integer.parseInt(args.get(1));
            }
            MobData mobData = MobDataHandler.getMobDataByID(id);
            if (mobData != null) {
                for (int i = 0; i < count; i++) {
                    Field field = chr.getField();
                    Mob mob = MobDataHandler.getMobByID(id);
                    if (mob != null) {
                        Position pos = chr.getPosition();
                        mob.setPosition(pos.deepCopy());
                        mob.setVPosition(pos.deepCopy());
                        mob.setHomePosition(pos.deepCopy());
                        mob.setFh(chr.getFoothold());
                        mob.setHomeFh(chr.getFoothold());
                        mob.setRespawnable(false);
                        mob.setField(field);
                        field.spawnMob(mob, chr);
                    }
                }
            }
        }
    }

    @Command(names = {"setstr", "setStr", "str"}, requiredPermission = AccountType.GameMaster)
    public static void setStrength(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            String amountToSet = args.get(0);
            if (MapleUtils.isNumber(amountToSet)) {
                int desiredAmount = Integer.parseInt(amountToSet);
                int finalAmount = desiredAmount > Short.MAX_VALUE ? Short.MAX_VALUE : desiredAmount;
                chr.updateStat(Stat.Str, finalAmount);
                chr.message("Update Str to: " + finalAmount, ChatType.GameDesc);
            }
        }
    }

    @Command(names = {"setdex", "setDex", "dex"}, requiredPermission = AccountType.GameMaster)
    public static void setDexterity(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            String amountToSet = args.get(0);
            if (MapleUtils.isNumber(amountToSet)) {
                int desiredAmount = Integer.parseInt(amountToSet);
                int finalAmount = desiredAmount > Short.MAX_VALUE ? Short.MAX_VALUE : desiredAmount;
                chr.updateStat(Stat.Dex, finalAmount);
                chr.message("Update Dex to: " + finalAmount, ChatType.GameDesc);
            }
        }
    }

    @Command(names = {"setint", "setint", "int"}, requiredPermission = AccountType.GameMaster)
    public static void setIntelligence(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            String amountToSet = args.get(0);
            if (MapleUtils.isNumber(amountToSet)) {
                int desiredAmount = Integer.parseInt(amountToSet);
                int finalAmount = desiredAmount > Short.MAX_VALUE ? Short.MAX_VALUE : desiredAmount;
                chr.updateStat(Stat.Inte, finalAmount);
                chr.message("Update int to: " + finalAmount, ChatType.GameDesc);
            }
        }
    }

    @Command(names = {"setluk", "setLuk", "luk"}, requiredPermission = AccountType.GameMaster)
    public static void setLuk(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            String amountToSet = args.get(0);
            if (MapleUtils.isNumber(amountToSet)) {
                int desiredAmount = Integer.parseInt(amountToSet);
                int finalAmount = desiredAmount > Short.MAX_VALUE ? Short.MAX_VALUE : desiredAmount;
                chr.updateStat(Stat.Luk, finalAmount);
                chr.message("Update luk to: " + finalAmount, ChatType.GameDesc);
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
        //stats.put(Stat.Money, MAX_MESO);
        chr.changeStats(stats);
        chr.message("Update stats to max!", ChatType.GameDesc);
    }

    @Command(names = {"mobStats", "mobs", ",mobStatus"}, requiredPermission = AccountType.GameMaster)
    public static void MobStatus(MapleChar chr, List<String> args) {
        Field field = chr.getField();
        chr.message("Total mobs: " + field.getMobs().size(), ChatType.SpeakerWorld);
        field.getMobs().values().forEach(mob -> chr.message(mob.toString(), ChatType.SpeakerWorld));
    }
}
