package com.dori.SpringStory.scripts.message;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.NpcMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SayMsg implements NpcMessageData {

    private String msg;
    private NpcMessageType type;
    private byte speakerType;
    private int speakerTemplateID;
    private Runnable action;

    @Override
    public void encode(OutPacket outPacket) {
        if ((speakerType & 4) != 0) {
            outPacket.encodeInt(speakerTemplateID); // speakerTemplateID
        }
        outPacket.encodeString(msg); // sMsg
        outPacket.encodeBool(type.isPrevPossible());
        outPacket.encodeBool(type.isNextPossible());
    }

    public void applyAction() {
        if (this.action != null) {
            this.action.run();
            this.action = null;
        }
    }
}
