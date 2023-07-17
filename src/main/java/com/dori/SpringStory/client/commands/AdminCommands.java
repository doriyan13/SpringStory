package com.dori.SpringStory.client.commands;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.AccountType;
import com.dori.SpringStory.enums.Job;
import com.dori.SpringStory.enums.Stat;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.Portal;
import com.dori.SpringStory.wzHandlers.MapDataHandler;

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
        int num = Integer.parseInt(args.get(0));
        int amountOfLevels = num - chr.getLevel();
        if (amountOfLevels > 0) {
            chr.lvlUp(amountOfLevels);
            chr.fullHeal();
        }
    }

    @Command(names = {"goto"}, requiredPermission = AccountType.GameMaster)
    public static void goToMap(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            Field toField = MapDataHandler.getMapByName(args.get(0));
            if (toField != null) {
                Portal targetPortal = toField.getDefaultPortal();
                chr.warp(toField, targetPortal);
            }
            // TODO: need to handle nonExist portals!
        }
        //TODO: need to handle sending to the client list of possible maps!
    }

    @Command(names = {"job", "setJob"}, requiredPermission = AccountType.GameMaster)
    public static void job(MapleChar chr, List<String> args) {
        if (!args.isEmpty()) {
            int id = Short.parseShort(args.get(0));
            Job job = Job.getJobById(id);
            if (job != null) {
                chr.setJob(id);
                chr.updateStat(Stat.SubJob, id);
            }
        }
    }
}
