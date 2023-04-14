package com.dori.SpringStory.inventory;

import com.dori.SpringStory.enums.InventoryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@AllArgsConstructor

@Entity
@Table(name = "equip_inventories")
public class EquipInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "inventoryId")
    private List<Equip> items;
    @Column(name = "type")
    private InventoryType type;
    private byte slots;

    public EquipInventory() {
        items = new CopyOnWriteArrayList<>();
        type = InventoryType.EQUIP;
    }

    public EquipInventory(InventoryType invType, int slots) {
        this.type = invType;
        items = new ArrayList<>();
        this.slots = (byte) slots;
    }

    public int getFirstOpenSlot() {
        int oldIndex = 0;
        for (Equip item : getItems()) {
            // items are always sorted by bag index
            if (item.getBagIndex() - oldIndex > 1) {
                // there's a gap between 2 consecutive items
                break;
            }
            oldIndex = item.getBagIndex();
        }
        return oldIndex + 1;
    }

    public void addItem(Equip item) {
        if(getItems().size() < getSlots()) {
            if(item.getBagIndex() == 0){
                item.setBagIndex(getFirstOpenSlot());
            }
            getItems().add(item);
            item.setInvType(getType());
            getItems().sort(Comparator.comparingInt(Equip::getBagIndex));
        }
    }
    public void removeItem(Equip item) {
        getItems().remove(item);
        getItems().sort(Comparator.comparingInt(Equip::getBagIndex));
    }

    private Equip getItemByIndex(int bagIndex) {
        return getItems().stream().filter(equip -> equip.getBagIndex() == bagIndex).findAny().orElse(null);
    }

    public Equip getItemByItemID(int equipId) {
        return getItems().stream().filter(equip -> equip.getItemId() == equipId).findFirst().orElse(null);
    }

    private boolean isFull() {
        return getItems().size() >= getSlots();
    }

    public int getEmptySlots() {
        return getSlots() - getItems().size();
    }
}
