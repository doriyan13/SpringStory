package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CUser;
import com.dori.SpringStory.enums.AccountType;
import com.dori.SpringStory.logger.Logger;

import static com.dori.SpringStory.connection.packet.headers.InHeader.UserChat;

public class ChatHandler {

    // Logger -
    private static final Logger logger = new Logger(ChatHandler.class);

    @Handler(op = UserChat)
    public static void handleUserChat(MapleClient c, InPacket inPacket) {
        // CField::SendChatMsg
        int updateTime = inPacket.decodeInt();
        String msg = inPacket.decodeString();
        boolean isOnlyBalloon = !inPacket.decodeBool();

        MapleChar chr = c.getChr();
        boolean isCharAdmin = c.getAccount().getAccountType().getLvl() >= AccountType.GameMaster.getLvl();
        // Broadcast the packet to all the char in the field -
        chr.getField().broadcastPacket(CUser.chat(chr.getId(), isCharAdmin, msg, isOnlyBalloon));
    }
}
