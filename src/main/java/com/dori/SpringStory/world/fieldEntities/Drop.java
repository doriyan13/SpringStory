package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.inventory.Item;
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
    private float chance;
    private int questId;

    public Drop(Item item) {
        this.item = item;
        this.money = false;
    }

    public Drop(int amount) {
        setQuantity(amount);
        this.money = true;
    }

    public void setQuantity(int quantity) {
        this.minQuantity = quantity;
        this.maxQuantity = quantity;
    }
}
