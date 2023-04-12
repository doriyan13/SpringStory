package com.dori.Dori90v.inventory;

import com.dori.Dori90v.enums.InventoryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
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

    public void addItem(Equip item) {
        if(getItems().size() < getSlots()) {
            getItems().add(item);
            item.setInvType(getType());
            //sortItemsByIndex();
        }
    }
    public void removeItem(Equip item) {
        getItems().remove(item);
        //sortItemsByIndex();
    }
}
