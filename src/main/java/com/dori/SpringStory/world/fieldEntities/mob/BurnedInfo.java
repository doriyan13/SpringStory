package com.dori.SpringStory.world.fieldEntities.mob;

import com.dori.SpringStory.connection.packet.OutPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BurnedInfo {
    private int chrId;
    private int skillId;
    private int dmg;
    private int interval;
    private int end;
    private int dotCount;

    public void encode(OutPacket outPacket){
        outPacket.encodeInt(chrId);
        outPacket.encodeInt(skillId);
        outPacket.encodeInt(dmg);
        outPacket.encodeInt(interval);
        outPacket.encodeInt(end);
        outPacket.encodeInt(dotCount);
    }
}
