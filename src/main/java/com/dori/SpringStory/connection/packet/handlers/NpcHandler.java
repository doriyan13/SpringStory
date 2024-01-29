package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CScriptMan;
import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.enums.NpcMessageType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.scripts.api.NpcMessage;
import com.dori.SpringStory.scripts.api.NpcScript;
import com.dori.SpringStory.scripts.api.ScriptApi;
import com.dori.SpringStory.scripts.message.NpcMessageData;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.world.fieldEntities.Npc;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.dori.SpringStory.connection.packet.headers.InHeader.*;

public class NpcHandler {
    private static final Logger logger = new Logger(NpcHandler.class);
    private static final Map<Integer, Method> npcScripts = new HashMap<>();

    public static void initHandlers() {
        long start = System.currentTimeMillis();
        String handlersDir = ServerConstants.NPC_SCRIPTS_DIR;
        Set<File> files = new HashSet<>();
        MapleUtils.findAllFilesInDirectory(files, new File(handlersDir));
        for (File file : files) {
            try {
                // grab all files in the NPCs scripts dir, strip them to their package name, and remove .java extension -
                String className = file.getPath()
                        .replaceAll("[\\\\|/]", ".")
                        .split("src\\.main\\.java\\.")[1]
                        .replaceAll("\\.java", "");
                Class<?> clazz = Class.forName(className);
                for (Method method : clazz.getMethods()) {
                    NpcScript npcScript = method.getAnnotation(NpcScript.class);
                    if (npcScript != null) {
                        int npcID = npcScript.id();
                        if (npcID == -1) {
                            logger.error("Found unMarked script! in the class - " + className);
                        } else {
                            npcScripts.put(npcID, method);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        logger.serverNotice("Initialized " + npcScripts.size() + " NPC scripts in " + (System.currentTimeMillis() - start) + "ms.");
    }

    @Handler(op = UserSelectNpc)
    public void handleUserMove(MapleClient c, InPacket inPacket) {
        MapleChar chr = c.getChr();
        int npcOid = inPacket.decodeInt();
        inPacket.decodePosition(); // playerPos
        Npc npc = c.getChr().getField().getNpcs().get(npcOid);

        Method method = npcScripts.get(npc.getTemplateId());
        ScriptApi script = null;
        if(method == null) {
            script = new ScriptApi();
            script.sayOK("The Npc ");
            script.addMsg(npc.getTemplateId())
                    .red()
                    .addMsg("wasn't handled!");
        } else {
            try {
                //TODO: need to think on solution to this (does making the method not static is good enough? | need testin!)
                script = (ScriptApi) method.invoke(this, chr);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (script != null) {
            chr.boundScript(script, npc.getTemplateId());
            NpcMessage msg = script.getCurrentMsg();
            c.getChr().write(CScriptMan.scriptMessage((byte) 0, npc.getTemplateId(), msg.getType(), msg.getData()));
        }

        //TODO: need to handle script invoking!
        // First try invoke the npc script
        // - OR -
        // second if it not a script it can be a shop!
        // Then Error!
        /**
         * Notice for my self to how to handle it -
         * maybe instead of stat weird stuff and secondary threads, then i can use consumeables -
         * like manage list of them that i build once when the script invoke and then have them sorted and invoked
         * depends on the action being called and this way it's on the main thread, don't need to manage the state myself
         * and each consumeable will have a invoked func behind the scene thus debug will be rlly easy
         * and loading the script obj will be when starting convo with npc and clear that when i get the end convo action!
         * the obj linked to the char will be managed as transient field -> not hurting anything
         * so basiclly letting me make the scripting being the building of the obj and then just have the debug option packet by packet
         * and have generic handling and not worry about threading things? and it sound not too resource heavy!
         */
    }

    private static NpcMessageData getSayMsgData(MapleChar chr,
                                                byte action) {
        NpcMessage message = null;
        if (action == 0) {
            // Prev action -
            message = chr.getScript().getPrevMsg();
        } else if (action == 1) {
            message = chr.getScript().getNextMsg();
        } else {
            // close dialog!
            chr.clearScript();
        }
        if (message != null) {
            return message.getData();
        }
        return null;
    }

    private static NpcMessageData getAskYesNoMsgData(MapleChar chr,
                                                     byte action) {
        chr.getScript().applyAskResponseAction(action == 1);
        NpcMessage message = chr.getScript().getNextMsg();
        if (message != null) {
            return message.getData();
        }
        return null;
    }

    private static NpcMessageData getAskMenuMsgData(MapleChar chr,
                                                    InPacket inPacket,
                                                    byte action) {
        int selection = inPacket.decodeInt();
        NpcMessage message = null;
        if (action == 0) {
            // Prev action -
            message = chr.getScript().getPrevMsg();
        } else if (action == 1) {
            // handle the API selection action!
            chr.getScript().applyResponseAction(selection);
            message = chr.getScript().getNextMsg();
        } else {
            // close dialog!
            chr.clearScript();
        }
        if (message != null) {
            return message.getData();
        }
        return null;
    }

    private static NpcMessageData getAskNumberMsgData(MapleChar chr,
                                                      InPacket inPacket,
                                                      byte action) {
        NpcMessage message = null;
        if (action == 0) {
            // Prev action?
            message = chr.getScript().getPrevMsg();
        } else if (action == 1) {
            int input = inPacket.decodeInt();
            // handle the API selection action!
            chr.getScript().applyAskResponseAction(input);
            message = chr.getScript().getNextMsg();
        }
        if (message != null) {
            return message.getData();
        }
        return null;
    }

    private static NpcMessageData getAskTextMsgData(MapleChar chr,
                                                    InPacket inPacket,
                                                    byte action) {
        NpcMessage message = null;
        if (action == 0) {
            // Prev action?
            message = chr.getScript().getPrevMsg();
        } else if (action == 1) {
            String text = inPacket.decodeString();
            // handle the API selection action!
            chr.getScript().applyAskResponseAction(text);
            message = chr.getScript().getNextMsg();
        }
        if (message != null) {
            return message.getData();
        }
        return null;
    }

    @Handler(op = UserScriptMessageAnswer)
    public static void handleUserScriptMessageAnswer(MapleClient c, InPacket inPacket) {
        MapleChar chr = c.getChr();
        byte type = inPacket.decodeByte();
        byte action = inPacket.decodeByte();
        NpcMessageType msgType = NpcMessageType.getNpcMsgTypeByVal(type);
        NpcMessageData messageData = null;
        switch (msgType) {
            case Say, SayNext, SayPrev, SayOk -> messageData = getSayMsgData(chr, action);
            case AskYesNo -> messageData = getAskYesNoMsgData(chr, action);
            case AskMenu -> messageData = getAskMenuMsgData(chr, inPacket, action);
            case AskNumber -> messageData = getAskNumberMsgData(chr, inPacket, action);
            case AskText, AskBoxText -> messageData = getAskTextMsgData(chr, inPacket, action);
            default -> logger.warning("Unsupported Npc message type!!! - " + msgType);
        }
        if (messageData != null) {
            chr.write(CScriptMan.scriptMessage((byte) 0, chr.getScript().getNpcID(), chr.getScript().getCurrMsgType(), messageData));
        } else {
            chr.clearScript();
            logger.error("No message to handle in the ScriptApi for the player - " + chr.getId());
        }
    }
}
