package com.dori.SpringStory.dataHandlers.dataEntities.quest.check;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.character.quest.Quest;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.QuestMobData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonTypeName("mobCheck")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestMobCheck(int questID,
                            List<QuestMobData> mobs) implements QuestCheck {
    @Override
    public boolean check(MapleChar chr) {
        //TODO: need to handle quest manager and then finish this!!
//        QuestManager qm = locked.get().getQuestManager();
        Optional<Quest> questRecordResult = Optional.empty();//qm.getQuestRecord(id);
        if (questRecordResult.isEmpty()) {
            return false;
        }
        String qrValue = questRecordResult.get().getQrValue();
        if (qrValue == null || qrValue.isEmpty()) {
            return false;
        }
        String requiredValue = mobs.stream()
                .map((mobData) -> String.format("%03d", mobData.count()))
                .collect(Collectors.joining());
        return qrValue.equals(requiredValue);
    }
}
