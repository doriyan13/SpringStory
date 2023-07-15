package com.dori.SpringStory.client.commands;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.AccountType;
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
}
