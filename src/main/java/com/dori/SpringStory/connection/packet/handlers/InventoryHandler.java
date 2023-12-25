package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.constants.GameConstants;
import com.dori.SpringStory.enums.ChatType;
import com.dori.SpringStory.enums.InventoryType;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.ItemUtils;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Drop;
import com.dori.SpringStory.world.fieldEntities.Foothold;

import static com.dori.SpringStory.connection.packet.headers.InHeader.UserChangeSlotPositionRequest;
import static com.dori.SpringStory.enums.InventoryOperation.*;
import static com.dori.SpringStory.enums.InventoryType.EQUIP;
import static com.dori.SpringStory.enums.InventoryType.EQUIPPED;

public class InventoryHandler {
    // Logger -
    private static final Logger logger = new Logger(InventoryHandler.class);

    @Handler(op = UserChangeSlotPositionRequest)
    public static void handleUserChangeSlotPositionRequest(MapleClient c, InPacket inPacket) {
        // CWvsContext::SendChangeSlotPositionRequest
        inPacket.decodeInt(); // updateTime
        InventoryType invType = InventoryType.getInventoryByVal(inPacket.decodeByte());
        short oldPos = inPacket.decodeShort();
        short newPos = inPacket.decodeShort();
        short quantity = inPacket.decodeShort();

        MapleChar chr = c.getChr();
        InventoryType invTypeFrom = invType == EQUIP ? oldPos < 0 ? EQUIPPED : EQUIP : invType;
        InventoryType invTypeTo = invType == EQUIP ? newPos < 0 ? EQUIPPED : EQUIP : invType;
        Item item = chr.getInventoryByType(invTypeFrom).getItemByIndex(oldPos);
        //TODO: some times equips get fucked bagIndex again (happen after changing to mechanic and then move / equip a stuff switching to battle mage?)
        if (item != null && quantity <= item.getQuantity()) {
            // Handling of Drop -
            if (newPos == 0) {
                if (chr.getField().isDropsDisabled()) {
                    chr.message("Drops are disabled in this map!", ChatType.SpeakerChannel);
                    return;
                }
                boolean fullDrop = !item.getInvType().isStackable() || (quantity - item.getQuantity() == 0)
                        || ItemUtils.isThrowingStar(item.getItemId()) || ItemUtils.isBullet(item.getItemId());
                Drop drop = fullDrop ? chr.dropItem(item) : chr.dropItem(item, quantity);
                //TODO: in the future make a proper handling if the item is trade-able?
                c.write(CWvsContext.inventoryOperation(true, fullDrop ? Remove : UpdateQuantity, oldPos, newPos, item));
                chr.getField().spawnDrop(drop, chr.getPosition());
            } else {
                // Change item position operation -
                Item swappedItem = chr.getInventoryByType(invTypeTo).getItemByIndex(newPos);
                item.setBagIndex(newPos);
                // Handle equip operation -
                if (invType == EQUIP && invTypeFrom != invTypeTo) {
                    int equippedSizeBefore = chr.getEquippedInventory().getItems().size();
                    int equipSizeBefore = chr.getEquipInventory().getItems().size();
                    // Handle swap items -
                    chr.swapItems(item, swappedItem, invTypeFrom == EQUIPPED);
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
            chr.enableAction();
            chr.message("Error occurred while doing illegal inventory operation, please contact Admin!", ChatType.SpeakerChannel);
        }
    }
}
