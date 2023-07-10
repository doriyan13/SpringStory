package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.enums.InventoryOperation;
import com.dori.SpringStory.enums.InventoryType;
import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.inventory.Item;

import static com.dori.SpringStory.enums.InventoryType.EQUIPPED;

public interface CWvsContext {

    static OutPacket inventoryOperation(boolean exclRequestSent, InventoryOperation type, short oldPos, short newPos, Item item) {
        InventoryType invType = item.getInvType();
        if ((oldPos > 0 && newPos < 0 && invType == EQUIPPED) || (invType == EQUIPPED && oldPos < 0)) {
            invType = InventoryType.EQUIP;
        }
        OutPacket outPacket = new OutPacket(OutHeader.InventoryOperation);
        outPacket.encodeBool(exclRequestSent);
        outPacket.encodeByte(1); // size
        // For each operation - (tho I see it always 1 at a time...)
        outPacket.encodeByte(type.getVal());
        outPacket.encodeByte(invType.getVal());
        outPacket.encodeShort(oldPos);
        // Handling for the diff operations -
        switch (type) {
            case Add -> item.encode(outPacket);
            case UpdateQuantity -> outPacket.encodeShort(item.getQuantity());
            case Move -> outPacket.encodeShort(newPos);
            case Remove -> {/*Do nothing O.o*/}
            case ItemExp ->outPacket.encodeLong(((Equip) item).getExp());
        }
        // I don't know what is this random bool at the end? -
        outPacket.encodeBool(false);
        return outPacket;
    }
}
