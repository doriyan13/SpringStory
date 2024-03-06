package com.dori.SpringStory.scripts.message;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.AvatarMsgType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvatarMsg implements NpcMessageData {

    private String msg;
    private List<Integer> options;
    private AvatarMsgType type;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeString(msg); // sMsg
        outPacket.encodeByte(options.size());
        for (int option : options) {
            outPacket.encodeInt(option);
        }
    }
}
