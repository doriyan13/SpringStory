package com.dori.SpringStory.scripts.message;

import com.dori.SpringStory.connection.packet.OutPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AskTextMsg implements NpcMessageData {

    private String msg;
    private String defaultText;
    private short minLength;
    private short maxLength;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeString(msg); // sMsg
        outPacket.encodeString(defaultText); // default text
        outPacket.encodeShort(minLength); // nLenMin
        outPacket.encodeShort(maxLength); // nLenMax
    }
}
