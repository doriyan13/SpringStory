package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.enums.ChatType;
import com.dori.SpringStory.enums.QuestResultType;
import com.dori.SpringStory.enums.UIWindowType;
import com.dori.SpringStory.enums.UserEffectTypes;
import com.dori.SpringStory.logger.Logger;
import org.jetbrains.annotations.NotNull;

public interface CUserLocal {
    // Logger -
    Logger logger = new Logger(CUserLocal.class);

    static OutPacket noticeMsg(String msg) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserLocalNoticeMsg);
        outPacket.encodeString(msg);

        return outPacket;
    }

    static OutPacket chatMsg(String msg, ChatType colour) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserLocalChatMsg);

        outPacket.encodeShort(colour.getVal());
        outPacket.encodeString(msg);

        return outPacket;
    }

    static OutPacket teleport(byte portalNum) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserLocalTeleport);
        outPacket.encodeBool(false); // I don't know what this check does?
        outPacket.encodeByte(portalNum); // Portal number (in array i guess?)

        return outPacket;
    }

    static OutPacket effect(UserEffectTypes type,
                            Effect effect) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserEffect);
        outPacket.encodeByte(type.getVal());
        switch (type) {
            case LevelUp, PortalSoundEffect, JobChanged, QuestComplete, MonsterBookCard, ItemLevelUp, ExpItemConsumed,
                 Buff, SoulStoneUse, EvolRing -> {}
            default -> effect.encode(outPacket);
        }
        return outPacket;
    }

    static OutPacket openUIWithOption(@NotNull UIWindowType type,
                                      int nDefaultTab) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserLocalOpenUIWithOption);
        outPacket.encodeInt(type.getVal());
        outPacket.encodeInt(nDefaultTab);

        return outPacket;
    }

    static OutPacket openUI(@NotNull UIWindowType type) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserLocalOpenUI);
        outPacket.encodeByte(type.getVal());

        return outPacket;
    }

    static OutPacket sitResult(boolean sit,
                               short fieldSeatId) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserLocalSitResult);
        outPacket.encodeByte(sit);
        if (sit) {
            outPacket.encodeShort(fieldSeatId);
        }
        return outPacket;
    }

    static OutPacket questResult(int questId,
                                 QuestResultType resultType,
                                 int time,
                                 int npcTemplateID,
                                 int nextQuestId) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserLocalQuestResult);
        outPacket.encodeByte(resultType.getVal());
        switch (resultType) {
            case StartQuestTimer -> {
                outPacket.encodeShort(1);
                outPacket.encodeShort(questId);
                outPacket.encodeInt(time);
            }
            case EndQuestTimer -> {
                outPacket.encodeShort(1);
                outPacket.encodeShort(questId);
            }
            case StartTimeKeepQuestTimer -> {
                outPacket.encodeShort(questId);
                outPacket.encodeInt(time);
            }
            case EndTimeKeepQuestTimer, FailedInventory, FailedTimeOver, ResetQuestTimer -> {
                outPacket.encodeShort(questId);
            }
            case Success -> {
                outPacket.encodeShort(questId);
                outPacket.encodeInt(npcTemplateID);
                outPacket.encodeShort(nextQuestId);
            }
            case FailedUnknown, FailedMeso, FailedEquipped, FailedOnlyItem -> {
            }
        }
        return outPacket;
    }

    static OutPacket resignQuestReturn(int questId) {
        OutPacket outPacket = new OutPacket(OutHeader.CUserLocalResignQuestReturn);
        outPacket.encodeShort(questId); // usQuestID
        return outPacket;
    }
}
