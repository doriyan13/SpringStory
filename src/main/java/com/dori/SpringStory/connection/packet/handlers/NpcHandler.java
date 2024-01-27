package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CScriptMan;
import com.dori.SpringStory.enums.NpcMessageType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.scripts.api.NpcMessage;
import com.dori.SpringStory.scripts.api.ScriptApi;
import com.dori.SpringStory.scripts.message.NpcMessageData;
import com.dori.SpringStory.world.fieldEntities.Npc;

import static com.dori.SpringStory.connection.packet.headers.InHeader.*;

public class NpcHandler {
    private static final Logger logger = new Logger(NpcHandler.class);

    @Handler(op = UserSelectNpc)
    public static void handleUserMove(MapleClient c, InPacket inPacket) {
        int npcOid = inPacket.decodeInt();
        inPacket.decodePosition(); // playerPos
        Npc npc = c.getChr().getField().getNpcs().get(npcOid);
        // TODO: k now move to reflection handling and we are DONE!
        ScriptApi test = new ScriptApi();
        test.setNpcID(npc.getTemplateId());
        test.sayNext("Hey")
                .sayNext("This is a test")
                .askYesNo("are you ready?", result -> {
                    if (result) {
                        test.askMenu(
                                test.addMenuOption("first option",
                                        () -> test.askNumber("choose a num between 1 - 10", 1, 10,
                                                (answer) -> test.sayOK("DONE"))
                                ),
                                test.addMenuOption("second",
                                        () -> test.sayNext("didn't want to end")
                                        .sayOK("Bye again!"))
                        );
                    } else {
                        test.sayOK("Bye");
                    }
                });
        c.getChr().boundScript(test);
        NpcMessage msg = test.getCurrentMsg();
        c.getChr().write(CScriptMan.scriptMessage((byte) 0, npc.getTemplateId(), msg.getType(), msg.getData()));
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

    @Handler(op = UserScriptMessageAnswer)
    public static void handleUserScriptMessageAnswer(MapleClient c, InPacket inPacket) {
        MapleChar chr = c.getChr();
        byte type = inPacket.decodeByte();
        byte action = inPacket.decodeByte();
        NpcMessageType msgType = NpcMessageType.getNpcMsgTypeByVal(type);
        NpcMessageData messageData = null;
        //TODO: move to small methods~!!
        switch (msgType) {
            case Say, SayNext, SayPrev, SayOk -> {
                NpcMessage message = null;
                if (action == 0) {
                    // Prev action?
                    message = chr.getScript().getPrevMsg();
                } else if (action == 1) {
                    message = chr.getScript().getNextMsg();
                } else {
                    // close dialog!
                    chr.clearScript();
                }
                if (message != null) {
                    messageData = message.getData();
                }
            }
            case AskYesNo -> {
                chr.getScript().applyAskResponseAction(action == 1);
                NpcMessage message = chr.getScript().getNextMsg();
                if (message != null) {
                    messageData = message.getData();
                }
            }
            case AskMenu -> {
                int selection = inPacket.decodeInt();
                NpcMessage message = null;
                if (action == 0) {
                    // Prev action?
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
                    messageData = message.getData();
                }
            }
            case AskNumber -> {
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
                    messageData = message.getData();
                }
            }
            case AskText, AskBoxText -> {
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
                    messageData = message.getData();
                }
            }
            default -> {
                logger.warning("Unsupported Npc message type!!! - " + msgType);
            }
        }
        if (messageData != null) {
            chr.write(CScriptMan.scriptMessage((byte) 0, chr.getScript().getNpcID(), chr.getScript().getCurrMsgType(), messageData));
        } else {
            chr.clearScript();
            logger.error("No message to handle in the ScriptApi for the player - " + chr.getId());
        }
    }
}
