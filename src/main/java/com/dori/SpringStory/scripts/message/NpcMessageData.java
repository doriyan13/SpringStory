package com.dori.SpringStory.scripts.message;

import com.dori.SpringStory.connection.packet.OutPacket;

public interface NpcMessageData {
    void encode(OutPacket outPacket);
    String getMsg();

    void setMsg(String updatedMsg);
}
