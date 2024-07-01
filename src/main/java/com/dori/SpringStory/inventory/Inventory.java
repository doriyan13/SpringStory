package com.dori.SpringStory.inventory;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.BodyPart;
import com.dori.SpringStory.enums.EquipType;
import com.dori.SpringStory.enums.InventoryOperation;
import com.dori.SpringStory.enums.InventoryType;
import com.dori.SpringStory.utils.ItemUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.dori.SpringStory.enums.InventoryOperation.Remove;
import static com.dori.SpringStory.enums.InventoryOperation.UpdateQuantity;
import static com.dori.SpringStory.utils.ItemUtils.isFullItemConsume;

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

    public void encode(OutPacket outPacket) {
        for (Item item : getItems()) {
            outPacket.encodeByte(item.getBagIndex());
            item.encode(outPacket);
        }
        outPacket.encodeByte(0);
    }

    public void encodeEquips(OutPacket outPacket, EquipType type, boolean isCashIndex) {
        for (Item item : getItems()) {
            Equip equip = (Equip) item;
            if (ItemUtils.shouldEncodeEquipByType(type, equip)) {
                outPacket.encodeShort(isCashIndex ? (equip.getBagIndex() - 100) : equip.getBagIndex());
                equip.encode(outPacket);
            }
        }
        outPacket.encodeShort(0);
    }

    public void sortItemsByIndex() {
        // workaround for sort not being available for CopyOnWriteArrayList
        List<Item> temp = new ArrayList<>(getItems());
        temp.sort(Comparator.comparingInt(Item::getBagIndex));
        getItems().clear();
        getItems().addAll(temp);
    }

    public int getFirstOpenSlot() {
        int oldIndex = 0;
        sortItemsByIndex();
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
        if (getItems().size() < getSlots()) {
            item.setBagIndex(Math.abs(item.getBagIndex()));
            if (getType() == InventoryType.EQUIPPED && item.getBagIndex() < 100) {
                item.setBagIndex(Math.abs(ItemUtils.getBodyPartFromItem(item.getItemId()).getVal()));
            }
            if (item.getBagIndex() == 0) {
                item.setBagIndex(getFirstOpenSlot());
            }
            getItems().add(item);
            item.setInvType(getType());
            getItems().sort(Comparator.comparingInt(Item::getBagIndex));
        }
    }

    public void removeItem(Item item) {
        getItems().remove(item);
        getItems().sort(Comparator.comparingInt(Item::getBagIndex));
    }

    public Item getItemByIndex(int bagIndex) {
        int bagIndexAbsVal = Math.abs(bagIndex) /*bagIndex < 0 ? (Math.abs(bagIndex) % 100) : Math.abs(bagIndex)*/;
        return getItems().stream().filter(item -> item.getBagIndex() == bagIndexAbsVal)
                .findAny()
                .orElse(null);
    }

    public Item getItemByItemID(int itemId) {
        return getItems()
                .stream()
                .filter(item -> (item.getItemId() == itemId) && item.getQuantity() != 0)
                .findFirst()
                .orElse(null);
    }

    public Item getFirstItemByIdAndMinAmount(int itemId,
                                             int minAmount) {
        return getItems()
                .stream()
                .filter(item -> item.getItemId() == itemId && item.getQuantity() >= minAmount)
                .findFirst()
                .orElse(null);
    }

    public List<Item> getItemsByItemID(int itemId) {
        return getItems()
                .stream()
                .filter(item -> (item.getItemId() == itemId) && item.getQuantity() != 0)
                .toList();
    }

    public InventoryOperation updateItemQuantity(Item item, int quantity) {
        if (isFullItemConsume(item, quantity)) {
            removeItem(item);
            return Remove;
        } else {
            item.setQuantity(Math.max(item.getQuantity() + quantity, 0));
            return UpdateQuantity;
        }
    }

    public boolean isFull() {
        return getItems().size() >= getSlots();
    }

    public int getEmptySlots() {
        return getSlots() - getItems().size();
    }

    public int getRemainingSlots() {
        return Math.max(slots - items.size(), 0);
    }

    public List<Item> getItemsByBodyPart(BodyPart bodyPart) {
        return getItems()
                .stream()
                .filter(item -> item.getBagIndex() == bodyPart.getVal())
                .toList();
    }

    public boolean hasItem(int itemId,
                           int amount) {
        boolean hasItem = false;
        for (Item item : getItems()) {
            if (item.getItemId() == itemId) {
                hasItem = true;
                amount -= item.getQuantity();
            }
        }
        return hasItem && amount <= 0;
    }

    public int getItemCount(int itemId) {
        int quantity = 0;
        for (Item item : getItems()) {
            if (item.getItemId() == itemId) {
                quantity += item.getQuantity();
            }
        }
        return quantity;
    }
}
