package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.enums.ChatType;
import com.dori.SpringStory.logger.Logger;

public interface CUserLocal {
    // Logger -
    Logger logger = new Logger(CUserLocal.class);

    static OutPacket noticeMsg(String msg){
        OutPacket outPacket = new OutPacket(OutHeader.CUserLocalNoticeMsg);
        outPacket.encodeString(msg);

        return outPacket;
    }

    static OutPacket chatMsg( String msg, ChatType colour) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserLocalChatMsg);

        outPacket.encodeShort(colour.getVal());
        outPacket.encodeString(msg);

        return outPacket;
    }

    static OutPacket teleport(byte portalNum) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserLocalTeleport);
        outPacket.encodeBool(false); // I don't know what this check does?
        outPacket.encodeByte(portalNum); // Portal number (in array i guess?)

        return outPacket;
    }
}
