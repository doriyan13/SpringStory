package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CUser;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.constants.ItemConstants;
import com.dori.SpringStory.dataHandlers.ItemDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.EquipData;
import com.dori.SpringStory.dataHandlers.dataEntities.ItemData;
import com.dori.SpringStory.enums.*;
import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.ItemUtils;

import java.util.Map;

import static com.dori.SpringStory.connection.packet.headers.InHeader.UserUpgradeItemUseRequest;
import static com.dori.SpringStory.enums.InventoryOperation.Add;
import static com.dori.SpringStory.enums.InventoryOperation.UpdateQuantity;
import static com.dori.SpringStory.enums.InventoryType.EQUIP;
import static com.dori.SpringStory.enums.InventoryType.EQUIPPED;

public class ItemUpgradeHandler {
    private static final Logger logger = new Logger(ItemUpgradeHandler.class);
    private static final int WHITE_SCROLL_ID = 2340000;

    @Handler(op = UserUpgradeItemUseRequest)
    public static void handleUserUpgradeItemUseRequest(MapleClient c,
                                                       InPacket inPacket) {
        MapleChar chr = c.getChr();

        inPacket.decodeInt(); // update time
        short useItemPos = inPacket.decodeShort(); //Use Position
        short equipPos = inPacket.decodeShort(); //Eqp Position
        boolean bWhiteScroll = inPacket.decodeShort() > 1;
        boolean bEnchantSkill = inPacket.decodeBool();

        Item scroll = chr.getInventoryByType(InventoryType.CONSUME).getItemByIndex(useItemPos);
        InventoryType invType = equipPos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) chr.getInventoryByType(invType).getItemByIndex(equipPos);
        if (scroll == null || equip == null) {
            chr.message("Couldn't find the scroll or the equip, scrolling failed! ~", ChatType.SpeakerChannel);
            chr.enableAction();
            return;
        }
        if (equip.getTuc() == 0) {
            chr.message("No more upgrade slots available!", ChatType.SpeakerChannel);
            chr.enableAction();
            return;
        }
        ItemData scrollInfo = ItemDataHandler.getItemDataByID(scroll.getItemId());
        if (scrollInfo == null || scrollInfo.getScrollStats().isEmpty()) {
            chr.message("Couldn't find the scroll data, scrolling failed! ~", ChatType.SpeakerChannel);
            chr.enableAction();
            return;
        }
        // White scroll validation & handling -
        if (bWhiteScroll) {
            Item whiteScroll = chr.getConsumeInventory().getItemByItemID(WHITE_SCROLL_ID);
            if (whiteScroll == null) {
                c.logout();
                return;
            }
            if (whiteScroll.getQuantity() < 1) {
                chr.removeItem(InventoryType.CONSUME, whiteScroll.getItemId());
                c.logout();
                return;
            }
            chr.consumeItem(InventoryType.CONSUME, whiteScroll.getItemId(), 1);
        }
        Map<ScrollStat, Integer> scrollStats = scrollInfo.getScrollStats();
        boolean success = ItemUtils.willSuccess(scrollStats.getOrDefault(ScrollStat.success, 100));
        boolean chaosScroll = scrollStats.getOrDefault(ScrollStat.randStat, 0) > 0;
        boolean recover = scrollStats.getOrDefault(ScrollStat.recover, 0) != 0;
        boolean boomTheItem = ItemUtils.willSuccess(scrollStats.getOrDefault(ScrollStat.cursed, 0));

        // First remove the scroll (avoid duplication after use) -
        chr.consumeItem(InventoryType.CONSUME, scroll.getItemId(), 1);

        if (success) {
            if (chaosScroll) {
                int max = scrollStats.containsKey(ScrollStat.incRandVol) ? ItemConstants.INC_RAND_CHAOS_MAX : ItemConstants.RAND_CHAOS_MAX;
                for (EquipBaseStat ebs : ScrollStat.getRandStats()) {
                    int cur = (int) equip.getBaseStat(ebs);
                    if (cur == 0) {
                        continue;
                    }
                    int randStat = ItemUtils.getRandom(0, max);
                    randStat = ItemUtils.willSuccess(50) ? -randStat : randStat;
                    equip.addStat(ebs, randStat);
                }
            } else {
                if (equip.getBaseStat(EquipBaseStat.tuc) <= 0 && !recover) {
                    chr.enableAction();
                    return;
                }
                if (recover) {
                    EquipData equipData = ItemDataHandler.getEquipDataByID(equip.getItemId());
                    if (equipData != null) {
                        int maxTuc = equipData.getTuc();
                        if (equip.getTuc() + equip.getCuc() < maxTuc) {
                            equip.addStat(EquipBaseStat.tuc, 1);
                        } else {
                            return; //clean slate scroll won't be consumed on items that it cannot be used on
                        }
                    }
                } else {
                    for (Map.Entry<ScrollStat, Integer> entry : scrollStats.entrySet()) {
                        ScrollStat ss = entry.getKey();
                        int val = entry.getValue();
                        if (ss.getEquipStat() != null) {
                            equip.addStat(ss.getEquipStat(), val);
                        }
                    }
                }
            }
            // Update slots usage -
            equip.addStat(EquipBaseStat.tuc, -1);
            equip.addStat(EquipBaseStat.cuc, 1);
        } else {
            if (boomTheItem) {
                chr.consumeItem(InventoryType.CONSUME, scroll.getItemId(),1);
                chr.removeItem(equip.getInvType(), equip.getItemId());
                chr.write(CUser.showItemUpgradeEffect(chr.getId(), false, true, bEnchantSkill, bWhiteScroll, 0));
                return;
            }
            if (!bWhiteScroll) { //todo: is white scroll ever get to here?
                equip.addStat(EquipBaseStat.tuc, -1);
            }
            if (!recover) {
                equip.removeAttribute(EquipAttribute.UpgradeCountProtection);
            }
            chr.enableAction();
        }
        short bagIndex = (short) (equip.getInvType() == EQUIPPED ? -equip.getBagIndex() : equip.getBagIndex());
        // Update the equip for the client -
        chr.write(CWvsContext.inventoryOperation(true, Add, bagIndex, (short) 0, equip));
        chr.write(CUser.showItemUpgradeEffect(chr.getId(), success, boomTheItem, bEnchantSkill, bWhiteScroll, 0));
    }
}
