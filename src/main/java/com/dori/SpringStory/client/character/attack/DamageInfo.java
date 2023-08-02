package com.dori.SpringStory.client.character.attack;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.mob.Mob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DamageInfo {
    private int mobId;
    private byte foreAction;
    private int calcDamageStatIndex;
    private byte hitAction;
    private byte frameIdx;
    private short delay;
    private Position hitPos;
    private Position prevPos;
    private int[] damages;

    public void decode(InPacket inPacket, byte hits) {
        this.mobId = inPacket.decodeInt();
        this.hitAction = inPacket.decodeByte();
        this.foreAction = inPacket.decodeByte();
        this.frameIdx = inPacket.decodeByte();
        this.calcDamageStatIndex = inPacket.decodeByte();
        this.hitPos = inPacket.decodePosition();
        this.prevPos = inPacket.decodePosition();
        this.delay = inPacket.decodeShort();
        this.damages = new int[hits];
        for (int i = 0; i < hits; i++) {
            this.damages[i] = inPacket.decodeInt();
        }
        inPacket.decodeInt(); // Mob CRC
    }

    public void apply(MapleChar chr) {
        Mob mob = chr.getField().getMobs().get(mobId);
        if (mob != null) {
            int totalDmg = Arrays.stream(damages).sum();
            mob.setController(chr);
            mob.damage(chr, totalDmg);
        }
    }
}
