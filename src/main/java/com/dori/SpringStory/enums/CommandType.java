package com.dori.SpringStory.enums;

import com.dori.SpringStory.client.commands.AdminCommands;
import com.dori.SpringStory.client.commands.PlayerCommands;

import java.util.Arrays;

public enum CommandType {
    NONE(' ', null),
    PLAYER_COMMAND('@', PlayerCommands.getInstance()),
    GM_COMMAND('!', AdminCommands.getInstance())
    ;

    private final char prefix;
    private final Object commandInstance;

    CommandType(char prefix, Object commandInstance){
        this.prefix = prefix;
        this.commandInstance = commandInstance;
    }

    public char getPrefix(){
        return this.prefix;
    }

    public Object getCommandInstance(){
        return this.commandInstance;
    }

    public static CommandType getCommandTypeByChar(char prefix){
        return Arrays.stream(CommandType.values())
                .filter(commandType -> commandType.getPrefix() == prefix)
                .findFirst()
                .orElse(NONE);
    }
}
