package com.dori.SpringStory.scripts.message;

import com.dori.SpringStory.connection.packet.OutPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseAskMsg implements NpcMessageData {

    private String msg;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeString(msg); // sMsg
    }
}
