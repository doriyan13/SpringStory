package com.dori.SpringStory.scripts.message;

import com.dori.SpringStory.connection.packet.OutPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvatarMsg implements NpcMessageData {

    private String msg;
    private int[] options;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeString(msg); // sMsg
        outPacket.encodeByte(options.length);
        for (int option : options) {
            outPacket.encodeInt(option);
        }
    }
}
