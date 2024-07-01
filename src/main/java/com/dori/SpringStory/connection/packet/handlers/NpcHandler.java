package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CNpcPool;
import com.dori.SpringStory.connection.packet.packets.CScriptMan;
import com.dori.SpringStory.enums.NpcMessageType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.scripts.api.NpcMessage;
import com.dori.SpringStory.scripts.handlers.ScriptHandler;
import com.dori.SpringStory.scripts.message.AvatarMsg;
import com.dori.SpringStory.scripts.message.NpcMessageData;
import com.dori.SpringStory.scripts.message.SayMsg;
import com.dori.SpringStory.world.fieldEntities.Npc;
import com.dori.SpringStory.world.fieldEntities.movement.MovementData;

import static com.dori.SpringStory.connection.packet.headers.InHeader.*;

public class NpcHandler {
    private static final Logger logger = new Logger(NpcHandler.class);

    @Handler(op = UserSelectNpc)
    public static void handleUserMove(MapleClient c, InPacket inPacket) {
        MapleChar chr = c.getChr();
        int npcOid = inPacket.decodeInt();
        inPacket.decodePosition(); // playerPos
        Npc npc = chr.getField().getNpcs().get(npcOid);
        // Invoke the script by the handler -
        ScriptHandler.getInstance().handleNpcScript(chr, npc);

        //TODO: need to handle script invoking!
        // First try invoke the npc script
        // - OR -
        // second if it not a script it can be a shop!

    }

    @Handler(op = NpcMove)
    public static void handleNpcMove(MapleClient c, InPacket inPacket) {
        MapleChar chr = c.getChr();
        int objectID = inPacket.decodeInt();
        byte oneTimeAction = inPacket.decodeByte();
        byte chatIdx = inPacket.decodeByte();
        Npc npc = chr.getField().getNpcs().get(objectID);
        chr.getField().broadcastPacket(CNpcPool.npcMove(objectID, oneTimeAction, chatIdx, npc.isMove(), npc.isMove() ? new MovementData(inPacket) : null));
    }

    private static NpcMessageData getSayMsgData(MapleChar chr,
                                                byte action) {
        NpcMessage message = null;
        ((SayMsg) chr.getScript().getCurrentMsg().getData()).applyAction();
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
        if (inPacket.getUnreadAmount() == 0) {
            return null;
        }
        int selection = inPacket.decodeInt();
        NpcMessage message = null;
        if (action == 0) {
            // Prev action -
            message = chr.getScript().getPrevMsg();
        } else if (action == 1) {
            // handle the API selection action!
            chr.getScript().applyResponseAction(selection);
            message = chr.getScript().getNextMsg();
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

    private static NpcMessageData getAvatarMsgData(MapleChar chr,
                                                   InPacket inPacket,
                                                   byte action) {
        NpcMessage message = null;
        if (action == 0) {
            // Prev action -
            message = chr.getScript().getPrevMsg();
        } else if (action == 1) {
            if (inPacket.getUnreadAmount() > 0) {
                int chosenCosmeticIndex = inPacket.decodeByte();
                if (chosenCosmeticIndex < 0) {
                    chosenCosmeticIndex = chosenCosmeticIndex + 256;
                }
                AvatarMsg msg = (AvatarMsg) chr.getScript().getCurrentMsg().getData();
                int chosenCosmeticId = msg.getOptions().get(chosenCosmeticIndex);
                switch (msg.getType()) {
                    case Hair -> chr.modifyHair(chosenCosmeticId);
                    case Face -> chr.modifyFace(chosenCosmeticId);
                    case Skin -> chr.modifySkin(chosenCosmeticId);
                }
            }
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
            case AskAvatar -> messageData = getAvatarMsgData(chr, inPacket, action);
            default -> logger.warning("Unsupported Npc message type!!! - " + msgType);
        }
        if (messageData != null) {
            chr.write(CScriptMan.scriptMessage((byte) 0, chr.getScript().getNpcID(), chr.getScript().getCurrMsgType(), messageData));
        } else {
            chr.clearScript();
        }
    }
}
