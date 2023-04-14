package com.dori.Dori90v.inventory;

import com.dori.Dori90v.connection.dbConvertors.FileTimeConverter;
import com.dori.Dori90v.connection.packet.OutPacket;
import com.dori.Dori90v.enums.InventoryType;
import com.dori.Dori90v.utils.ItemUtils;
import com.dori.Dori90v.utils.utilEntities.FileTime;
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
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    private int itemId;
    private int bagIndex;
    private long cashItemSerialNumber;
    @Convert(converter = FileTimeConverter.class)
    private FileTime dateExpire = FileTime.fromType(FileTime.Type.MAX_TIME);
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "inventoryType")
    private InventoryType invType;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private ItemType type;
    private boolean isCash;
    private int quantity;
    private String owner = "";

    public Item(int itemId, int bagIndex, long cashItemSerialNumber, FileTime dateExpire, InventoryType invType,
                boolean isCash, ItemType type) {
        this.itemId = itemId;
        this.bagIndex = bagIndex;
        this.cashItemSerialNumber = cashItemSerialNumber;
        this.dateExpire = dateExpire;
        this.invType = invType;
        this.isCash = isCash;
        this.type = type;
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(getType().getVal());
        // GW_ItemSlotBase
        outPacket.encodeInt(getItemId());
        outPacket.encodeByte(isCash());
        if (isCash()) {
            outPacket.encodeLong(getId());
        }
        outPacket.encodeFT(getDateExpire());
        outPacket.encodeInt(getBagIndex());
        outPacket.encodeShort(getQuantity()); // nQuantity
        outPacket.encodeString(getOwner()); // sOwner
        outPacket.encodeShort(0); // attribute | flag?
        if (ItemUtils.isThrowingItem(getItemId())) {
            outPacket.encodeLong(getId());
        }
    }
}
