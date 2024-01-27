package com.dori.SpringStory.scripts.message;

import com.dori.SpringStory.connection.packet.OutPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AskBoxTextMsg implements NpcMessageData {

    private String msg;
    private String defaultText;
    private short numOfCols;
    private short numbOfLines;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeString(msg); // sMsg
        outPacket.encodeString(defaultText); // default text
        outPacket.encodeShort(numOfCols); // nCol
        outPacket.encodeShort(numbOfLines); // nLine
    }
}
