package com.dori.SpringStory.scripts.message;

import com.dori.SpringStory.connection.packet.OutPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AskNumberMsg implements NpcMessageData {

    private String msg;
    private int defaultNum;
    private int min;
    private int max;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeString(msg); // sMsg
        outPacket.encodeInt(defaultNum); // nDef
        outPacket.encodeInt(min); // nMin
        outPacket.encodeInt(max); // nMax
    }
}
