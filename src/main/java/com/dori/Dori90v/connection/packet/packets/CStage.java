package com.dori.Dori90v.connection.packet.packets;

import com.dori.Dori90v.client.MapleClient;
import com.dori.Dori90v.client.character.MapleChar;
import com.dori.Dori90v.connection.packet.OutPacket;
import com.dori.Dori90v.connection.packet.headers.OutHeader;
import com.dori.Dori90v.enums.DBChar;
import com.dori.Dori90v.utils.utilEntities.FileTime;
import com.dori.Dori90v.world.fieldEntities.Field;

import java.security.SecureRandom;

import static com.dori.Dori90v.enums.DBChar.*;

public interface CStage {
    static OutPacket onSetField(MapleClient client, MapleChar chr, Field field, short optNum,
                                int channelId, int oldDriverID, boolean characterData,
                                byte sNotifierMessage, short nNotifierCheck, String pChatBlockReason, String[] sMsg2){
        OutPacket outPacket = new OutPacket(OutHeader.SetField);

        outPacket.encodeShort(optNum);
        for (int i = 0; i < optNum; i++) {
            outPacket.encodeInt(i + 1); // dwType
            outPacket.encodeInt(0); // idk?
        }
        outPacket.encodeInt(channelId - 1); // Nexon moving mad, going sub 1 the original channel num?
        outPacket.encodeInt(oldDriverID);
        outPacket.encodeByte(sNotifierMessage);
        outPacket.encodeByte(characterData);
        outPacket.encodeShort(nNotifierCheck);
        if(nNotifierCheck > 0){
            outPacket.encodeString(pChatBlockReason);
            for (int j = 0; j < nNotifierCheck; j++) {
                outPacket.encodeString(sMsg2[j]);
            }
        }
        if(characterData){
            // Calc dmg - (need to generate 3 random ints)
            SecureRandom rand = new SecureRandom();
            for (int z = 0; z < 3; z++) {
                outPacket.encodeInt(rand.nextInt());
            }
            // TODO: charData -> decode
            DBChar mask = DBChar.Specific;
            chr.encodeInfo(outPacket, mask);
            // TODO: CWvsContext::OnSetLogoutGiftConfig -
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
        }
        else {
            outPacket.encodeByte(0); // usingBuffProtector
            outPacket.encodeInt(field.getId());
            outPacket.encodeByte(0); // portal
            outPacket.encodeShort(0); // char hp ?
            outPacket.encodeBool(false);
            if(false){
                outPacket.encodeInt(0);
                outPacket.encodeInt(0);
            }
        }

        outPacket.encodeFT(new FileTime(System.currentTimeMillis()));

        return outPacket;
    }
}
