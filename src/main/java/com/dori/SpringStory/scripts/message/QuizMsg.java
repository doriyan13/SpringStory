package com.dori.SpringStory.scripts.message;

import com.dori.SpringStory.connection.packet.OutPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizMsg implements NpcMessageData {

    private byte type;
    private String title;
    private String problemText;
    private String hintText;
    private int min;
    private int max;
    private int remainTimeInSec;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type); // type
        if(type != 1) {
            outPacket.encodeString(title); // title
            outPacket.encodeString(problemText); // sProblemText
            outPacket.encodeString(hintText); // sHintText
            outPacket.encodeInt(min); // nMin
            outPacket.encodeInt(max); // nMax
            outPacket.encodeInt(remainTimeInSec); // remaining time in seconds
        }
    }

    @Override
    public String getMsg() {
        // TODO: maybe will need to change it?
        return title;
    }

    @Override
    public void setMsg(String updatedMsg) {
        title = updatedMsg;
    }
}
