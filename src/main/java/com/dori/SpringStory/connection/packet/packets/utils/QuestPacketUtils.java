package com.dori.SpringStory.connection.packet.packets.utils;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.packets.CUserLocal;
import com.dori.SpringStory.enums.QuestResultType;

public interface QuestPacketUtils {

    static OutPacket successQuestResult(int questId,
                             int templateId,
                             int nextQuestId) {
        return CUserLocal.questResult(questId, QuestResultType.Success, 0, templateId, nextQuestId);
    }

    static OutPacket failedUnknownQuestResult() {
        return CUserLocal.questResult(0, QuestResultType.FailedUnknown, 0, 0, 0);
    }

    static OutPacket failedInventoryQuestResult(int questId) {
        return CUserLocal.questResult(questId, QuestResultType.FailedInventory, 0, 0, 0);
    }
}
