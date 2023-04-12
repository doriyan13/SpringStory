package com.dori.Dori90v.inventory;

import com.dori.Dori90v.enums.InventoryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
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

    public void addItem(Item item) {
        if(getItems().size() < getSlots()) {
            getItems().add(item);
            item.setInvType(getType());
            //sortItemsByIndex();
        }
    }
    public void removeItem(Item item) {
        getItems().remove(item);
        //sortItemsByIndex();
    }
}
