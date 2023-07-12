package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;

public interface CUser {

    static OutPacket chat(int charID, boolean isAdmin, String msg, boolean isOnlyBalloon){
        OutPacket outPacket = new OutPacket(OutHeader.UserChat);
        outPacket.encodeInt(charID);
        outPacket.encodeBool(isAdmin);
        outPacket.encodeString(msg);
        outPacket.encodeBool(!isOnlyBalloon);

        return outPacket;
    }
}
