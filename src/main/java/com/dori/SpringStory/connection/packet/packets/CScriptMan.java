package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.enums.NpcMessageType;
import com.dori.SpringStory.scripts.message.NpcMessageData;

public interface CScriptMan {
    static OutPacket scriptMessage(byte speakerType,
                                   int npcID,
                                   NpcMessageType npcMsgType,
                                   NpcMessageData npcMessageData) {
        OutPacket outPacket = new OutPacket(OutHeader.ScriptMessage);
        outPacket.encodeByte(speakerType); // SpeakerTypeID -> should be 4?
        outPacket.encodeInt(npcID); // nSpeakerTemplateID
        outPacket.encodeByte(npcMsgType.getVal()); // nMsgType
        outPacket.encodeBool(speakerType > 0);
        // Additional encode of the specific msg -
        npcMessageData.encode(outPacket);

        return outPacket;
    }
}
