package com.dori.SpringStory.dataHandlers.dataEntities.quest.check;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.QuestItemData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Set;
import java.util.stream.Collectors;

@JsonTypeName("itemCheck")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestItemCheck(Set<QuestItemData> items) implements QuestCheck{

    private Set<QuestItemData> getFilteredItems(int gender,
                                                int job) {
        return items.stream()
                .filter(itemData -> itemData.checkGender(gender) && itemData.checkJob(job))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean check(MapleChar chr) {
        Set<QuestItemData> filteredItems = getFilteredItems(chr.getGender().getValue(), chr.getJob());
        for (QuestItemData item : filteredItems) {
            boolean hasItem = chr.hasItem(item.itemID(), item.count());
            if ((!hasItem && item.count() > 0) || (hasItem && item.count() <= 0)) {
                return false;
            }
        }
        return true;
    }
}
