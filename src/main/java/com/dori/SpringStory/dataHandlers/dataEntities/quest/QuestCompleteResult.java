package com.dori.SpringStory.dataHandlers.dataEntities.quest;

import com.dori.SpringStory.client.character.quest.Quest;

public record QuestCompleteResult(Quest currQuest,
                                  int nextQuestID) {
}
