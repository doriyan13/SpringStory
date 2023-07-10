package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.enums.InventoryType;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.logger.Logger;

import static com.dori.SpringStory.connection.packet.headers.InHeader.UserChangeSlotPositionRequest;
import static com.dori.SpringStory.enums.InventoryOperation.Add;
import static com.dori.SpringStory.enums.InventoryOperation.Move;
import static com.dori.SpringStory.enums.InventoryType.EQUIP;
import static com.dori.SpringStory.enums.InventoryType.EQUIPPED;

public class InventoryHandler {
    // Logger -
    private static final Logger logger = new Logger(InventoryHandler.class);

    @Handler(op = UserChangeSlotPositionRequest)
    public static void handleUserChangeSlotPositionRequest(MapleClient c, InPacket inPacket) {
        // CWvsContext::SendChangeSlotPositionRequest
        int updateTime = inPacket.decodeInt();
        InventoryType invType = InventoryType.getInventoryByVal(inPacket.decodeByte());
        short oldPos = inPacket.decodeShort();
        short newPos = inPacket.decodeShort();
        short quantity = inPacket.decodeShort();

        MapleChar chr = c.getChr();
        InventoryType invTypeFrom = invType == EQUIP ? oldPos < 0 ? EQUIPPED : EQUIP : invType;
        InventoryType invTypeTo = invType == EQUIP ? newPos < 0 ? EQUIPPED : EQUIP : invType;
        Item item = chr.getInventoryByType(invTypeFrom).getItemByIndex(oldPos);

        if (item != null) {
            // Handling of Drop -
            if (newPos == 0) {
                //TODO: need to add handling for drops!
            } else {
                // Change item position operation -
                Item swappedItem = chr.getInventoryByType(invTypeFrom).getItemByIndex(newPos);
                item.setBagIndex(newPos);
                // Handle equip operation -
                if (invType == EQUIP && invTypeFrom != invTypeTo) {
                    int equippedSizeBefore = chr.getEquippedInventory().getItems().size();
                    int equipSizeBefore = chr.getEquipInventory().getItems().size();
                    // Handle swap items -
                    chr.swapItems(item,swappedItem, invTypeFrom == EQUIPPED);
                    // Verify there wasn't a data duplication -
                    if (chr.getEquipInventory().getItems().size() + chr.getEquippedInventory().getItems().size()
                            != equipSizeBefore + equippedSizeBefore) {
                        logger.error("Data duplication has occurred from the char: " + chr.getName() + " | " + chr.getId());
                        c.close();
                        // TODO: will need to add a ban to the player that tried to duplicate data !
                    }
                }
                // If there is an item to swap - give the swapped item the old position -
                if (swappedItem != null) {
                    swappedItem.setBagIndex(oldPos);
                }
                c.write(CWvsContext.inventoryOperation(true, Move, oldPos, newPos, item));
            }
        } else {
            //need to use dispose for the char | need to add at CWvsContext the packet and handling in MapleChar!
        }
    }
}
