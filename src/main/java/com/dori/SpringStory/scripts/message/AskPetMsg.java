package com.dori.SpringStory.scripts.message;

import com.dori.SpringStory.connection.packet.OutPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AskPetMsg implements NpcMessageData {
    private String msg;
    private long[] options;
    private boolean allPet;
    private boolean exceptionExist;

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeString(msg); // sMsg
        outPacket.encodeByte(options.length); // nCount
        if (allPet) {
            outPacket.encodeBool(exceptionExist); // bExceptionExist
        }
        for (long option : options) {
            outPacket.encodeLong(option); // pQDlg ?
            outPacket.encodeByte(0); // unk?
        }
    }
}
