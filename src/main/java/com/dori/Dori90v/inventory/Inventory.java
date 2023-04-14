package com.dori.Dori90v.inventory;

import com.dori.Dori90v.enums.InventoryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor

@Entity
@Table(name = "inventories")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "inventoryId")
    private List<Item> items;
    @Column(name = "type")
    private InventoryType type;
    private byte slots;

    public Inventory() {
        items = new ArrayList<>();
        type = InventoryType.ETC;
    }

    public Inventory(InventoryType invType, int slots) {
        this.type = invType;
        items = new ArrayList<>();
        this.slots = (byte) slots;
    }

    public int getFirstOpenSlot() {
        int oldIndex = 0;
        for (Item item : getItems()) {
            // items are always sorted by bag index
            if (item.getBagIndex() - oldIndex > 1) {
                // there's a gap between 2 consecutive items
                break;
            }
            oldIndex = item.getBagIndex();
        }
        return oldIndex + 1;
    }

    public void addItem(Item item) {
        if(getItems().size() < getSlots()) {
            item.setBagIndex(getFirstOpenSlot());
            getItems().add(item);
            item.setInvType(getType());
            getItems().sort(Comparator.comparingInt(Item::getBagIndex));
        }
    }
    public void removeItem(Item item) {
        getItems().remove(item);
        getItems().sort(Comparator.comparingInt(Item::getBagIndex));
    }

    private Item getItemByIndex(int bagIndex) {
        return getItems().stream().filter(item -> item.getBagIndex() == bagIndex).findAny().orElse(null);
    }

    public Item getItemByItemID(int itemId) {
        return getItems().stream().filter(item -> (item.getItemId() == itemId) && item.getQuantity() != 0).findFirst().orElse(null);
    }

    private boolean isFull() {
        return getItems().size() >= getSlots();
    }

    public int getEmptySlots() {
        return getSlots() - getItems().size();
    }
}
