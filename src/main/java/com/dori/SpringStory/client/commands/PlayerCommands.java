package com.dori.SpringStory.client.commands;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.AccountType;
import com.dori.SpringStory.enums.ChatType;
import com.dori.SpringStory.logger.Logger;

import java.util.List;

public class PlayerCommands {
    // Logger -
    private static final Logger logger = new Logger(PlayerCommands.class);

    private static PlayerCommands instance;

    private PlayerCommands(){}

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static PlayerCommands getInstance(){
        if(instance == null){
            instance = new PlayerCommands();
        }
        return instance;
    }

    @Command(names = {"help"}, requiredPermission = AccountType.Player)
    public static void help(MapleChar chr, List<String> args){
        logger.debug("Testing!");
        logger.debug("chr: " + chr.getId());
        logger.debug("args: " + args.toString());
    }

    @Command(names = {"dispose", "ea"}, requiredPermission = AccountType.Player)
    public static void dispose(MapleChar chr, List<String> args){
        chr.enableAction();
        chr.message("You've been disposed <3", ChatType.GameDesc);
    }

    @Command(names = {"info", "status"}, requiredPermission = AccountType.Player)
    public static void info(MapleChar chr, List<String> args){
        chr.message("Name: " + chr.getName(), ChatType.GameDesc);
        chr.message("Lvl: " + chr.getLevel() + " | Job: " + chr.getJob() +  " | Str: " + chr.getNStr() + " | Dex: " + chr.getNDex() +
                " | Int: " + chr.getNInt() + " | Luk: " + chr.getNLuk(), ChatType.GameDesc);
        chr.message("Field: " + chr.getField().getId() + " | Pos:" + chr.getPosition() + " | Fh: " + chr.getFoothold(), ChatType.GameDesc);
    }
}
