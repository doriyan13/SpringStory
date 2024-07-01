package com.dori.SpringStory.dataHandlers.dataEntities.quest;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.character.quest.Quest;
import com.dori.SpringStory.client.character.quest.QuestManager;
import com.dori.SpringStory.connection.packet.packets.utils.QuestPacketUtils;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.act.QuestAct;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.act.QuestItemAct;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.check.QuestCheck;
import com.dori.SpringStory.enums.QuestStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestData(int id,
                        int nextQuest,
                        boolean autoStart,
                        boolean autoComplete,
                        Set<QuestAct> startActs,
                        Set<QuestAct> completeActs,
                        Set<QuestCheck> startChecks,
                        Set<QuestCheck> completeChecks) {

    public boolean isAutoAlert() {
        return autoStart || autoComplete;
    }

    public void restoreLostItems(@NotNull MapleChar chr,
                                 @NotNull Set<Integer> lostItems) {
        // Check that the quest has been started
        Optional<Quest> questRecordResult = chr.getQuestManager().getQuest(id);
        if (questRecordResult.isEmpty() || questRecordResult.get().getStatus() != QuestStatus.Started) {
            chr.write(QuestPacketUtils.failedUnknownQuestResult());
            return;
        }
        for (QuestAct questAct : startActs) {
            if (questAct instanceof QuestItemAct questItemAct) {
                questItemAct.restoreLostItems(chr, lostItems);
            }
        }
    }

    public Optional<Quest> startQuest(MapleChar chr) {
        // Check that the quest can be started
        for (QuestCheck startCheck : startChecks) {
            if (!startCheck.check(chr)) {
                return Optional.empty();
            }
        }
        if (startActs != null) {
            // Verify the chr have all the requirements -
            for (QuestAct startAct : startActs) {
                if (!startAct.canAct(chr, -1)) {
                    return Optional.empty();
                }
            }
            // Perform start acts
            for (QuestAct startAct : startActs) {
                if (!startAct.doAct(chr, -1)) {
                    throw new RuntimeException("Failed to perform quest start act");
                }
            }
        }
        // Add quest record and return
        return Optional.of(chr.getQuestManager().forceStartQuest(id));
    }

    public Optional<QuestCompleteResult> completeQuest(MapleChar chr,
                                                       int rewardIndex) {
        // Check that the quest has been started
        QuestManager qm = chr.getQuestManager();
        if (!qm.hasQuestStarted(id)) {
            return Optional.empty();
        }
        if (completeChecks != null) {
            // Check that the quest can be completed
            for (QuestCheck completeCheck : completeChecks) {
                if (!completeCheck.check(chr)) {
                    return Optional.empty();
                }
            }
        }
        if (completeActs != null) {
            for (QuestAct completeAct : completeActs) {
                if (!completeAct.canAct(chr, rewardIndex)) {
                    return Optional.empty();
                }
            }
            // Perform complete acts
            for (QuestAct completeAct : completeActs) {
                if (!completeAct.doAct(chr, rewardIndex)) {
                    throw new RuntimeException("Failed to perform quest complete act");
                }
            }
        }
        // Mark as completed and return
        Quest qr = qm.forceCompleteQuest(id);
        return Optional.of(new QuestCompleteResult(qr, nextQuest));
    }

    public Optional<Quest> resignQuest(MapleChar chr) {
        QuestManager qm = chr.getQuestManager();
        Optional<Quest> questRecordResult = qm.getQuest(id);
        if (questRecordResult.isEmpty() || questRecordResult.get().getStatus() != QuestStatus.Started) {
            return Optional.empty();
        }
        Optional<Quest> removeQuestRecordResult = qm.removeQuest(id);
        if (removeQuestRecordResult.isEmpty()) {
            return Optional.empty();
        }
        for (QuestAct questAct : startActs) {
            if (questAct instanceof QuestItemAct questItemAct) {
                questItemAct.removeQuestItems(chr);
            }
        }
        Quest qr = removeQuestRecordResult.get();
        qr.setStatus(QuestStatus.NotStarted);
        return Optional.of(qr);
    }

}
