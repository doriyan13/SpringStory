package com.dori.SpringStory.dataHandlers.dataEntities.quest.check;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.character.quest.Quest;
import com.dori.SpringStory.enums.QuestStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

@JsonTypeName("exCheck")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestExCheck(int questID,
                           @NotNull Set<String> allowedValues) implements QuestCheck {
    @Override
    public boolean check(MapleChar chr) {
        // TODO: need to finish handling!!
        Optional<Quest> optionalQuest = Optional.empty(); // chr.get().getQuestManager().getQuestRecord(getQuestId());
        return optionalQuest
                .filter(quest -> quest.getStatus() == QuestStatus.Started && allowedValues
                        .contains(quest.getQrValue())
                ).isPresent();
    }
}
