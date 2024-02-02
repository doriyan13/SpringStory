package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.commands.AdminCommands;
import com.dori.SpringStory.client.commands.Command;
import com.dori.SpringStory.client.commands.PlayerCommands;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CUser;
import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.enums.AccountType;
import com.dori.SpringStory.enums.CommandType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.MapleUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.dori.SpringStory.connection.packet.headers.InHeader.UserChat;

public class ChatHandler {
    // Logger -
    private static final Logger logger = new Logger(ChatHandler.class);
    // Commands maps data -
    private static final Map<CommandType, Map<String, Method>> commands = new HashMap<>();

    public static void initCmdMap() {
        long start = System.currentTimeMillis();
        int amountOfCommands = 0;
        String handlersDir = ServerConstants.COMMANDS_DIR;
        HashSet<File> filesList = new HashSet<>();
        MapleUtils.findAllFilesInDirectory(filesList, new File(handlersDir));

        for (File currFile : filesList) {
            try {
                // Grab all files in the commands' dir, strip them to their package name, and remove .java extension
                String className = currFile
                        .getPath()
                        .replaceAll("[\\\\|/]", ".")
                        .split("src\\.main\\.java\\.")[1]
                        .replaceAll("\\.java", "");
                Class<?> listOfClasses = Class.forName(className);
                for (Method currMethod : listOfClasses.getMethods()) {
                    Command commandClass = currMethod.getAnnotation(Command.class);
                    if (commandClass != null) {
                        // Fill The list with commands -
                        for (String cmdName : commandClass.names()) {
                            CommandType commandType = CommandType.NONE;
                            switch (commandClass.requiredPermission()) {
                                case Player -> commandType = CommandType.PLAYER_COMMAND;
                                case GameMaster -> commandType = CommandType.GM_COMMAND;
                            }
                            if(!commands.containsKey(commandType)){
                                commands.put(commandType,new HashMap<>());
                            }
                            commands.get(commandType).put(cmdName.toLowerCase(), currMethod);
                            amountOfCommands++;
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                logger.error("Error while reading the Commands files", e);
            }
        }
        logger.serverNotice("Initialized " + amountOfCommands + " commands in " + (System.currentTimeMillis() - start) + "ms.");
    }

    private static void handleCommand(String command, List<String> args, MapleChar currChar, CommandType commandType) {
        // Try to get the relevant command method -
        Method method = commands.get(commandType).get(command.toLowerCase());
        if (method != null) {
            try {
                method.invoke(commandType.getCommandInstance(), currChar, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Error while invoking Player command method");
                e.fillInStackTrace();
            }
        } else {
            // Command not found!
            logger.warning("The command you wrote not exist, look at @help for full list :D");
        }
    }

    @Handler(op = UserChat)
    public static void handleUserChat(MapleClient c, InPacket inPacket) {
        // CField::SendChatMsg
        inPacket.decodeInt(); // updateTime
        String msg = inPacket.decodeString();
        boolean isOnlyBalloon = !inPacket.decodeBool();

        MapleChar chr = c.getChr();
        boolean isCharAdmin = c.getAccount().getAccountType().getLvl() >= AccountType.GameMaster.getLvl();
        // Get the possible command modifier -
        char startChar = msg.charAt(0);
        List<String> args = Arrays.asList(msg.split(" "));
        // Remove the command from the list -
        String command = args.get(0);
        command = command.replace(String.valueOf(command.charAt(0)),"");
        // Handle commands/msg -
        CommandType commandType = CommandType.getCommandTypeByChar(startChar);
        if (commandType != CommandType.NONE) {
            handleCommand(command, args.subList(1,args.size()), chr, commandType);
        } else {
            // Broadcast the packet to all the char in the field -
            chr.getField().broadcastPacket(CUser.chat(chr.getId(), isCharAdmin, msg, isOnlyBalloon));
        }

    }
}
