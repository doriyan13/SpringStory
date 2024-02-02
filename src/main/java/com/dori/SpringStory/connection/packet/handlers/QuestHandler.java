package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.quest.Quest;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CUserLocal;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.enums.QuestRequestType;
import com.dori.SpringStory.enums.QuestResulType;
import com.dori.SpringStory.enums.QuestStatus;
import com.dori.SpringStory.logger.Logger;

import java.util.stream.IntStream;

import static com.dori.SpringStory.connection.packet.headers.InHeader.UserQuestRequest;

public class QuestHandler {
    // Logger -
    private static final Logger logger = new Logger(QuestHandler.class);

    @Handler(op = UserQuestRequest)
    public static void handleUserQuestRequest(MapleClient c, InPacket inPacket) {
        if(true) {
            //TODO: need to properly handle it!
            return;
        }
        QuestRequestType questType = QuestRequestType.getQuestTypeByVal(inPacket.decodeByte());
        short questID = inPacket.decodeShort();
        int dwNpcTemplateID = 0;
        boolean success = false;
        // TODO: add handling for managing quest in game data (need to wz read them + instance manage them per player!)

        if (questType != QuestRequestType.LostItem && questType != QuestRequestType.ResignQuest) {
            dwNpcTemplateID = inPacket.decodeInt();
            // TODO: if quest isn't autoStart? | probablly wz propertiy to manage!
            if (inPacket.getUnreadAmount() > 4) {
                inPacket.decodePosition(); // userPosition
            }
        }
        // TODO: After handling quest, all the decoding will be passed to the Quest Class!!
        switch (questType) {
            case LostItem -> {
                int lostCount = inPacket.decodeInt();
                int[] lostItems = new int[lostCount];
                IntStream.range(0, lostCount).forEach(index -> {
                    lostItems[index] = inPacket.decodeInt();
                });
                //TODO: need to do better handling how to obtain again a lost item
            }
            case AcceptQuest -> {
                // TODO: add to list of player quests, this also didn't rlly fix the spamming issue...
                Quest tempQuest = new Quest();
                tempQuest.setQRKey(questID);
                tempQuest.setQrValue("");
                tempQuest.setStatus(QuestStatus.Completed);
                c.write(CWvsContext.questRecordMessage(tempQuest));
                success = true;
            }
            case CompleteQuest -> {
                int select = inPacket.decodeInt();
                // TODO: check if the quest exist (if not, probably mismatch info / hack) => either way close client!
            }
            case ResignQuest -> {
                // TODO: probably remove the quest from list of quests for the player
            }
            case OpeningScript -> {
                // TODO: find the quest, invoke the "start" linked script to that quest
            }
            case CompleteScript -> {
                // TODO: find the quest, invoke the "end" linked script to that quest
            }
            case null, default ->
                    logger.warning("Unhandled questRequestType was given - " + questType + " for the player - " + c.getChr().getId());
        }

        if (success) {
            // TODO: handle the secondQuestID properly!
            c.write(CUserLocal.questResult(QuestResulType.Success, questID, dwNpcTemplateID, 0));
        }
    }
}
