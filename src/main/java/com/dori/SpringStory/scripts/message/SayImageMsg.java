package com.dori.SpringStory.scripts.message;

import com.dori.SpringStory.connection.packet.OutPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SayImageMsg implements NpcMessageData {

    private String[] images;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(images.length);
        for (String image : images) {
            outPacket.encodeString(image);
        }
    }

    @Override
    public String getMsg() {
        return "";
    }

    @Override
    public void setMsg(String updatedMsg) {
        // empty
    }
}
