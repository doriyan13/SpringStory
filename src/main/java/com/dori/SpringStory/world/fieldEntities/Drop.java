package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.dataHandlers.ItemDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.MobDropData;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.utilEntities.Position;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Drop {
    private int id;
    private boolean money;
    private Item item;
    private String reactorAction;
    private int minQuantity;
    private int maxQuantity;
    private double chance;
    private int questId;
    private Position position;
    private int ownerID;

    public Drop(Item item) {
        this.item = item;
        this.money = false;
        this.position = new Position();
        this.setQuantity(1);
    }

    public Drop(int amount) {
        setQuantity(amount);
        this.money = true;
        this.position = new Position();
    }

    public Drop(MobDropData mobDropData) {
        if(!mobDropData.isMoney()) {
            this.money = false;
            Item item = ItemDataHandler.getItemByID(mobDropData.getItemId());
            if (item == null) {
                item = ItemDataHandler.getEquipByID(mobDropData.getItemId());
            }
            this.item = item;
        } else {
            this.money = true;
        }
        this.minQuantity = mobDropData.getMinQ();
        this.maxQuantity = mobDropData.getMaxQ();
        this.questId = mobDropData.getQuestId();
        this.chance = mobDropData.getChance();
        this.position = new Position();
    }

    public void setQuantity(int quantity) {
        this.minQuantity = quantity;
        this.maxQuantity = quantity;
    }

    public int getQuantity() {
        if(getMinQuantity() == getMaxQuantity()) {
            return getMinQuantity();
        }
        return MapleUtils.getRandom(getMinQuantity(), getMaxQuantity());
    }
}
