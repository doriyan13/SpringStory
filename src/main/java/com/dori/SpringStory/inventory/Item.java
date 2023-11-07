package com.dori.SpringStory.inventory;

import com.dori.SpringStory.connection.dbConvertors.FileTimeConverter;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.InventoryType;
import com.dori.SpringStory.utils.ItemUtils;
import com.dori.SpringStory.utils.utilEntities.FileTime;
import com.dori.SpringStory.wzHandlers.wzEntities.ItemData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name = "items")
@Inheritance(strategy = InheritanceType.JOINED)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    protected int itemId;
    protected int bagIndex;
    protected long cashItemSerialNumber;
    @Convert(converter = FileTimeConverter.class)
    protected FileTime dateExpire = FileTime.fromType(FileTime.Type.MAX_TIME);
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "inventoryType")
    protected InventoryType invType;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    protected ItemType type;
    protected boolean cash;
    protected int quantity;
    protected String owner = "";

    public Item(int itemId, int bagIndex, long cashItemSerialNumber, FileTime dateExpire, InventoryType invType,
                boolean cash, ItemType type) {
        this.itemId = itemId;
        this.bagIndex = bagIndex;
        this.cashItemSerialNumber = cashItemSerialNumber;
        this.dateExpire = dateExpire;
        this.invType = invType;
        this.cash = cash;
        this.type = type;
    }

    public Item(ItemData itemData) {
        this.itemId = itemData.getItemId();
        this.invType = itemData.getInvType();
        this.cash = itemData.isCash();
        this.quantity = 1;
        this.bagIndex = 0;
        this.type = ItemType.BUNDLE; // ITEM
    }

    public void encodeItemSlotBase(OutPacket outPacket){
        outPacket.encodeInt(getItemId());
        outPacket.encodeByte(isCash());
        if (isCash()) {
            outPacket.encodeLong(getId());
        }
        outPacket.encodeFT(getDateExpire());
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(getType().getVal());
        // GW_ItemSlotBase
        encodeItemSlotBase(outPacket);
        outPacket.encodeShort(getQuantity()); // nQuantity
        outPacket.encodeString(getOwner()); // sOwner
        outPacket.encodeShort(0); // attribute | flag?
        if (ItemUtils.isThrowingItem(getItemId())) {
            outPacket.encodeLong(getId());
        }
    }

    public void removeQuantity(int amount) {
        if (amount > 0) {
            setQuantity(Math.max(0, getQuantity() - amount));
        }
    }
}
