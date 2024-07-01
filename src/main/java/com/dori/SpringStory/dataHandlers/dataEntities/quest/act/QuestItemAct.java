package com.dori.SpringStory.dataHandlers.dataEntities.quest.act;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.effects.parsers.QuestEffect;
import com.dori.SpringStory.connection.packet.packets.CUserLocal;
import com.dori.SpringStory.connection.packet.packets.utils.QuestPacketUtils;
import com.dori.SpringStory.dataHandlers.ItemDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.ItemData;
import com.dori.SpringStory.dataHandlers.dataEntities.quest.QuestItemData;
import com.dori.SpringStory.enums.InventoryType;
import com.dori.SpringStory.enums.UserEffectTypes;
import com.dori.SpringStory.inventory.Inventory;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.utils.MapleUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@JsonTypeName("itemAct")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestItemAct(int questID,
                           Set<QuestItemData> items,
                           List<QuestItemData> choices) implements QuestAct {

    private Set<QuestItemData> getFilteredItems(int gender,
                                                int job) {
        return items.stream()
                .filter(itemData -> itemData.checkGender(gender) && itemData.checkJob(job))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean canAct(@NotNull MapleChar chr,
                          int rewardIndex) {
        Set<QuestItemData> filteredItems = getFilteredItems(chr.getGender().getValue(), chr.getJob());

        // Handle required slots for random items
        Map<InventoryType, Integer> requiredSlots = new EnumMap<>(InventoryType.class);
        for (QuestItemData itemData : filteredItems) {
            if (!itemData.isRandom()) {
                continue;
            }
            InventoryType inventoryType = InventoryType.getTypeByItemId(itemData.itemID());
            requiredSlots.put(inventoryType, 1);
        }
        // Handle required slots for choice items
        if (rewardIndex > 0) {
            if (choices.size() < rewardIndex) {
                return false;
            }
            QuestItemData choiceItemData = choices.get(rewardIndex);
            InventoryType inventoryType = InventoryType.getTypeByItemId(choiceItemData.itemID());
            requiredSlots.put(inventoryType, requiredSlots.getOrDefault(inventoryType, 0) + 1);
        }
        // Handle static items - required slots if count > 0, else check if present in inventory
        for (QuestItemData itemData : filteredItems) {
            if (!itemData.isStatic()) {
                continue;
            }
            if (itemData.count() > 0) {
                InventoryType inventoryType = InventoryType.getTypeByItemId(itemData.itemID());
                requiredSlots.put(inventoryType, requiredSlots.getOrDefault(inventoryType, 0) + 1);
            } else {
                if (!chr.hasItem(itemData.itemID(), itemData.count())) {
                    return false;
                }
            }
        }
        // Check for required slots
        for (var entry : requiredSlots.entrySet()) {
            if (chr.getInventoryByType(entry.getKey()).getRemainingSlots() < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean doAct(@NotNull MapleChar chr,
                         int rewardIndex) {
        Set<QuestItemData> filteredItems = getFilteredItems(chr.getGender().getValue(), chr.getJob());
        // Take required items
        for (QuestItemData itemData : filteredItems) {
            if (!itemData.isStatic() || itemData.count() >= 0) {
                continue;
            }
            int quantity = -itemData.count();
            boolean success = chr.consumeItem(itemData.itemID(), quantity);
            if (!success) {
                return false;
            }
            chr.write(CUserLocal.effect(UserEffectTypes.Quest, new QuestEffect(Collections.singletonMap(itemData.itemID(), itemData.count()))));
        }

        // Give choice item
        if (rewardIndex > 0) {
            if (choices.size() < rewardIndex) {
                return false;
            }
            QuestItemData choiceItemData = choices.get(rewardIndex);
            if (attemptAddItemToChr(chr, choiceItemData)) return false;
        }

        // Give static items
        for (QuestItemData itemData : filteredItems) {
            if (!itemData.isStatic() || itemData.count() <= 0) {
                continue;
            }
            if (attemptAddItemToChr(chr, itemData)) return false;
        }

        // Give random item
        Set<QuestItemData> randomItems = filteredItems.stream()
                .filter(QuestItemData::isRandom)
                .collect(Collectors.toUnmodifiableSet());
        Optional<QuestItemData> randomResult = MapleUtils.getRandomFromCollection(randomItems, QuestItemData::prop);
        if (randomResult.isPresent()) {
            QuestItemData itemData = randomResult.get();
            return !attemptAddItemToChr(chr, itemData);
        }
        return true;
    }

    private boolean attemptAddItemToChr(@NotNull MapleChar chr,
                                        @NotNull QuestItemData choiceItemData) {
        ItemData itemInfoResult = ItemDataHandler.getItemDataByID(choiceItemData.itemID());
        if (itemInfoResult == null) {
            return true;
        }
        Item item = new Item(itemInfoResult);
        item.setQuantity(choiceItemData.count());
        boolean success = chr.addItem(item);
        if (!success) {
            return true;
        }
        chr.write(CUserLocal.effect(UserEffectTypes.Quest, new QuestEffect(Collections.singletonMap(choiceItemData.itemID(), choiceItemData.count()))));
        return false;
    }

    public void restoreLostItems(@NotNull MapleChar chr,
                                 @NotNull Set<Integer> lostItems) {
        Set<QuestItemData> filteredItems = getFilteredItems(chr.getGender().getValue(), chr.getJob())
                .stream()
                .filter(itemData -> lostItems.contains(itemData.itemID()))
                .collect(Collectors.toUnmodifiableSet());
        Map<InventoryType, Integer> requiredSlots = new EnumMap<>(InventoryType.class);

        // Calculate required slots and validate lost items
        for (QuestItemData itemData : filteredItems) {
            if (!itemData.isStatic() || itemData.count() <= 0) {
                continue;
            }
            ItemData itemInfoResult = ItemDataHandler.getItemDataByID(itemData.itemID());
            if (itemInfoResult == null) {
                chr.write(QuestPacketUtils.failedUnknownQuestResult());
                return;
            }
            if (!itemInfoResult.isQuest()) {
                chr.write(QuestPacketUtils.failedUnknownQuestResult());
                return;
            }
            InventoryType inventoryType = InventoryType.getTypeByItemId(itemData.itemID());
            requiredSlots.put(inventoryType, requiredSlots.getOrDefault(inventoryType, 0) + 1);
        }

        // Check for required slots
        for (var entry : requiredSlots.entrySet()) {
            Inventory inventory = chr.getInventoryByType(entry.getKey());
            int remainingSlots = inventory.getRemainingSlots();
            if (remainingSlots < entry.getValue()) {
                chr.write(QuestPacketUtils.failedInventoryQuestResult(questID));
                return;
            }
        }

        // Give missing items
        for (QuestItemData itemData : filteredItems) {
            if (!itemData.isStatic() || itemData.count() <= 0) {
                continue;
            }
            ItemData itemInfoResult = ItemDataHandler.getItemDataByID(itemData.itemID());
            if (itemInfoResult == null) {
                chr.write(QuestPacketUtils.failedUnknownQuestResult());
                return;
            }
            int count = itemData.count() - chr.getItemCount(itemData.itemID());
            if (count <= 0) {
                chr.write(QuestPacketUtils.failedUnknownQuestResult());
                return;
            }
            Item item = new Item(itemInfoResult);
            item.setQuantity(count);
            boolean success = chr.addItem(item);
            if (!success) {
                chr.write(QuestPacketUtils.failedUnknownQuestResult());
                return;
            }
            chr.write(CUserLocal.effect(UserEffectTypes.Quest, new QuestEffect(Collections.singletonMap(item.getItemId(), count))));
        }
    }

    public void removeQuestItems(MapleChar chr) {
        // Remove quest items
        for (QuestItemData questItemData : items) {
            ItemData itemData = ItemDataHandler.getItemDataByID(questItemData.itemID());
            if (itemData == null || !itemData.isQuest()) {
                continue;
            }
            int count = chr.getItemCount(itemData.getItemId());
            if (count <= 0) {
                continue;
            }
            boolean success = chr.consumeItem(itemData.getItemId(), count);
            if (!success) {
                chr.write(QuestPacketUtils.failedUnknownQuestResult());
                return;
            }
            chr.write(CUserLocal.effect(UserEffectTypes.Quest, new QuestEffect(Collections.singletonMap(itemData.getItemId(), count))));
        }
    }
}
