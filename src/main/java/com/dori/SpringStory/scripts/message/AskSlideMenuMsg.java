package com.dori.SpringStory.scripts.message;

import com.dori.SpringStory.connection.packet.OutPacket;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AskSlideMenuMsg implements NpcMessageData {
    @Override
    public void encode(OutPacket outPacket) {
        //TODO: need to handle properly!!!
        outPacket.encodeInt(0);
        // start CSlideMenuDlg::SetSlideMenuDlg
        outPacket.encodeInt(0); // last selected
        StringBuilder sb = new StringBuilder();
        for (int destination : new int[0]/*Map of destinations*/) {
            // TODO: need to map and replace with index with actual numbers 1-10 for ex' and add descriptions!
            sb.append("#").append("index").append("#").append("Description");
        }
        outPacket.encodeString(sb.toString());
    }

    @Override
    public String getMsg() {
        //TODO: need to think how to handle it?
        return "";
    }

    @Override
    public void setMsg(String updatedMsg) {
        //empty
    }
}
