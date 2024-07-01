package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.character.quest.Quest;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CUserLocal;
import com.dori.SpringStory.connection.packet.packets.CUserRemote;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.connection.packet.packets.utils.QuestPacketUtils;
import com.dori.SpringStory.dataHandlers.QuestDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.QuestData;
import com.dori.SpringStory.enums.QuestRequestType;
import com.dori.SpringStory.enums.UserEffectTypes;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.scripts.handlers.ScriptHandler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static com.dori.SpringStory.connection.packet.headers.InHeader.UserQuestRequest;

public class QuestHandler {
    // Logger -
    private static final Logger logger = new Logger(QuestHandler.class);

    @Handler(op = UserQuestRequest)
    public static void handleUserQuestRequest(MapleChar chr, InPacket inPacket) {
        QuestRequestType questType = QuestRequestType.getQuestTypeByVal(inPacket.decodeByte());
        short questID = inPacket.decodeShort();
        Optional<QuestData> optionalQuestData = QuestDataHandler.getQuestData(questID);
        if (optionalQuestData.isEmpty()) {
            logger.error("Could not retrieve quest ID: " + questID);
            return;
        }
        QuestData questData = optionalQuestData.get();

        switch (questType) {
            case LostItem -> {
                int lostCount = inPacket.decodeInt();
                Set<Integer> lostItems = new HashSet<>();
                IntStream.range(0, lostCount).forEach(index -> {
                    lostItems.add(inPacket.decodeInt());
                });
                questData.restoreLostItems(chr, lostItems);
            }
            case AcceptQuest -> {
                int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                inPacket.decodeInt(); // itemPos | CWvsContext.m_nQuestDeliveryItemPos
                if (!questData.isAutoAlert()) {
                    inPacket.decodePosition(); // ptUserPos.x + ptUserPos.y
                }
                questData.startQuest(chr).ifPresentOrElse(quest -> {
                    chr.write(CWvsContext.questRecordMessage(quest));
                    chr.write(QuestPacketUtils.successQuestResult(questID, templateId, 0));
                    chr.enableAction();
                }, () -> {
                    logger.error("Failed to accept quest : " + questID);
                    chr.enableAction();
                });
            }
            case CompleteQuest -> {
                int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                inPacket.decodeInt(); // itemPos | CWvsContext.m_nQuestDeliveryItemPos
                if (!questData.isAutoAlert()) {
                    inPacket.decodePosition(); // ptUserPos.x + ptUserPos.y
                }
                int rewardIndex = inPacket.decodeInt(); // nIdx - for selecting reward
                questData.completeQuest(chr, rewardIndex).ifPresentOrElse(
                        questCompleteResult -> {
                            Quest questRecord = questCompleteResult.currQuest();
                            int nextQuest = questCompleteResult.nextQuestID();
                            chr.write(CWvsContext.questRecordMessage(questRecord));
                            chr.write(QuestPacketUtils.successQuestResult(questID, templateId, nextQuest));
                            chr.enableAction();
                            // Quest complete effect
                            chr.write(CUserLocal.effect(UserEffectTypes.QuestComplete, null));
                            chr.getField().broadcastPacket(CUserRemote.remoteEffect(chr.getId(), UserEffectTypes.QuestComplete, null));
                        }, () -> {
                            logger.error("Failed to complete quest : " + questID);
                            chr.enableAction();
                        }
                );
            }
            case ResignQuest -> {
                questData.resignQuest(chr).ifPresentOrElse(
                        quest -> {
                            chr.write(CWvsContext.questRecordMessage(quest));
                            chr.write(CUserLocal.resignQuestReturn(questID));
                            chr.enableAction();
                        }, () -> logger.error("Failed to resign quest : " + questID)
                );
            }
            case OpeningScript, CompleteScript -> {
                int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                inPacket.decodePosition(); // ptUserPos.x + ptUserPos.y
                boolean start = questType == QuestRequestType.OpeningScript;
                ScriptHandler.getInstance().handleQuestScript(chr, templateId, questID, start);
            }
            case null, default ->
                    logger.warning("Unhandled questRequestType was given - " + questType + " for the player - " + chr.getId());
        }
    }
}
