package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;

public interface CUser {

    static OutPacket chat(int charID, boolean isAdmin, String msg, boolean isOnlyBalloon){
        OutPacket outPacket = new OutPacket(OutHeader.UserChat);
        outPacket.encodeInt(charID);
        outPacket.encodeBool(isAdmin);
        outPacket.encodeString(msg);
        outPacket.encodeBool(!isOnlyBalloon);

        return outPacket;
    }

    static OutPacket showItemUpgradeEffect(int charID,
                                           boolean success,
                                           boolean cursed,
                                           boolean enchantSkill,
                                           boolean whiteScroll,
                                           int nEnchantCategory){
        OutPacket outPacket = new OutPacket(OutHeader.UserShowItemUpgradeEffect);
        outPacket.encodeInt(charID);
        outPacket.encodeBool(success);
        outPacket.encodeBool(cursed);
        outPacket.encodeBool(enchantSkill);
        outPacket.encodeInt(nEnchantCategory);
        outPacket.encodeBool(whiteScroll);
        outPacket.encodeByte(0); // bRecoverable

        return outPacket;
    }

    static OutPacket showItemHyperUpgradeEffect(int charID,
                                           boolean success,
                                           boolean enchantSkill,
                                           int nEnchantCategory){
        OutPacket outPacket = new OutPacket(OutHeader.UserShowItemHyperUpgradeEffect);
        outPacket.encodeInt(charID);
        outPacket.encodeBool(success);
        outPacket.encodeBool(!success);
        outPacket.encodeBool(enchantSkill);
        outPacket.encodeInt(nEnchantCategory);

        return outPacket;
    }
}
