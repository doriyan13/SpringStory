package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CUser;
import com.dori.SpringStory.connection.packet.packets.CWvsContext;
import com.dori.SpringStory.dataHandlers.ItemDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.ItemData;
import com.dori.SpringStory.enums.*;
import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.ItemUtils;

import java.util.Map;

import static com.dori.SpringStory.connection.packet.headers.InHeader.*;
import static com.dori.SpringStory.constants.GameConstants.*;
import static com.dori.SpringStory.enums.InventoryOperation.Add;
import static com.dori.SpringStory.enums.InventoryType.EQUIP;
import static com.dori.SpringStory.enums.InventoryType.EQUIPPED;

public class ItemUpgradeHandler {
    private static final Logger logger = new Logger(ItemUpgradeHandler.class);

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
        if (!ItemUtils.isScrollingEquipValid(chr, scroll, equip)) {
            return;
        }
        ItemData scrollInfo = ItemDataHandler.getItemDataByID(scroll.getItemId());
        if (!ItemUtils.isScrollDataValid(chr, scrollInfo)) {
            return;
        }
        // White scroll validation & handling -
        if (bWhiteScroll) {
            ItemUtils.applyWhiteScrollToChar(chr);
        }
        Map<ScrollStat, Integer> scrollStats = scrollInfo.getScrollStats();
        boolean success = ItemUtils.willSuccess(scrollStats.getOrDefault(ScrollStat.success, 100));
        boolean chaosScroll = scrollStats.getOrDefault(ScrollStat.randStat, 0) > 0;
        boolean cleanSlateScroll = scrollStats.getOrDefault(ScrollStat.recover, 0) != 0;
        boolean boomTheItem = ItemUtils.willSuccess(scrollStats.getOrDefault(ScrollStat.cursed, 0));
        // First remove the scroll (avoid duplication after use) -
        chr.consumeItem(InventoryType.CONSUME, scroll.getItemId(), 1);
        if (success) {
            if (!ItemUtils.isEquipScrollable(chr, equip, cleanSlateScroll)) {
                return;
            }
            if (chaosScroll) {
                ItemUtils.applyChaosScrollToEquip(equip, scrollStats);
            } else if (cleanSlateScroll) {
                ItemUtils.applyCleanSlateToEquip(equip);
            } else {
                ItemUtils.applyNormalScrollToEquip(equip, scrollStats);
            }
            // Update slots usage -
            equip.addStat(EquipBaseStat.cuc, 1);
        } else if (boomTheItem) {
            ItemUtils.applyDarkScrollBoom(chr, equip, scroll, bEnchantSkill, bWhiteScroll);
            chr.enableAction();
            return;
        }
        if (!bWhiteScroll) {
            equip.addStat(EquipBaseStat.tuc, -1);
        }
        short bagIndex = (short) (equip.getInvType() == EQUIPPED ? -equip.getBagIndex() : equip.getBagIndex());
        // Update the equip for the client -
        chr.write(CWvsContext.inventoryOperation(true, Add, bagIndex, (short) 0, equip));
        chr.write(CUser.showItemUpgradeEffect(chr.getId(), success, boomTheItem, bEnchantSkill, bWhiteScroll, 0));
    }

    @Handler(op = UserHyperUpgradeItemUseRequest)
    public static void handleUserHyperUpgradeItemUseRequest(MapleClient c,
                                                            InPacket inPacket) {
        MapleChar chr = c.getChr();

        inPacket.decodeInt(); // update time
        short useItemPos = inPacket.decodeShort(); //Use Position
        short equipPos = inPacket.decodeShort(); //Eqp Position
        boolean enchantSkill = inPacket.decodeBool(); // bEnchantSkill

        Item scroll = chr.getInventoryByType(InventoryType.CONSUME).getItemByIndex(useItemPos);
        InventoryType invType = equipPos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) chr.getInventoryByType(invType).getItemByIndex(equipPos);
        if (!ItemUtils.isScrollingEquipValid(chr, scroll, equip) || !ItemUtils.canEnchantmentEquip(chr, equip)) {
            return;
        }
        boolean advanceEnhancement = scroll.getItemId() % 2 == 0;
        int basePercentage = advanceEnhancement ? ADVANCE_ENHANCEMENT_BASE_PERCENTAGE : ENHANCEMENT_BASE_PERCENTAGE;
        int successRate = LOWEST_ENHANCEMENT_PERCENTAGE;
        if (equip.getStarUpgradeCount() == 0) {
            successRate = basePercentage;
            //SKip calc
        } else if (equip.getStarUpgradeCount() <= 7) {
            successRate = basePercentage - (equip.getStarUpgradeCount() - 1) * 10;
        }
        boolean success = ItemUtils.willSuccess(successRate);
        // First remove the scroll (avoid duplication after use) -
        chr.consumeItem(InventoryType.CONSUME, scroll.getItemId(), 1);
        if (success) {
            ItemUtils.applyEnchantment(equip);
            // Update the equip for the client -
            chr.write(CWvsContext.inventoryOperation(true, Add, (short) (equip.getInvType() == EQUIPPED ? -equip.getBagIndex() : equip.getBagIndex()), (short) 0, equip));
            chr.write(CUser.showItemHyperUpgradeEffect(chr.getId(), true, enchantSkill, 0));
        } else {
            ItemUtils.applyEnchantmentBoom(chr, equip, scroll, enchantSkill);
        }
    }


    @Handler(op = UserItemOptionUpgradeItemUseRequest)
    public static void handleUserItemOptionUpgradeItemUseRequest(MapleClient c,
                                                                 InPacket inPacket) {
        MapleChar chr = c.getChr();

        inPacket.decodeInt(); // update time
        short useItemPos = inPacket.decodeShort(); //Use Position
        short equipPos = inPacket.decodeShort(); //Eqp Position
        boolean enchantSkill = inPacket.decodeBool();

        Item scroll = chr.getInventoryByType(InventoryType.CONSUME).getItemByIndex(useItemPos);
        InventoryType invType = equipPos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) chr.getInventoryByType(invType).getItemByIndex(equipPos);
        if (!ItemUtils.isScrollingEquipValid(chr, scroll, equip) || ItemUtils.isNotItemOptionUpgradeItem(scroll.getItemId())) {
            return;
        } else if (!ItemUtils.canEquipHavePotential(equip)) {
            logger.warning(String.format("Character %d tried to add potential an eligible item (id %d)", chr.getId(), equip.getItemId()));
            chr.enableAction();
            return;
        }
        boolean advancePotentialScroll = scroll.getItemId() % 2 == 0;
        int successRate = advancePotentialScroll ? ADVANCE_POTENTIAL_BASE_PERCENTAGE : POTENTIAL_BASE_PERCENTAGE;
        boolean success = ItemUtils.willSuccess(successRate);

        // First remove the scroll (avoid duplication after use) -
        chr.consumeItem(InventoryType.CONSUME, scroll.getItemId(), 1);
        if (success) {
            equip.setGrade(PotentialGradeCode.HiddenRare);
            // Update the equip for the client -
            chr.write(CWvsContext.inventoryOperation(true, Add, (short) (equip.getInvType() == EQUIPPED ? -equip.getBagIndex() : equip.getBagIndex()), (short) 0, equip));
            chr.write(CUser.showOptionItemUpgradeEffect(chr.getId(), true, enchantSkill, 0));
        } else {
            ItemUtils.applyPotentialBoom(chr, equip, scroll, enchantSkill);
        }
    }

    @Handler(op = UserItemReleaseRequest)
    public static void handleUserItemReleaseRequest(MapleClient c,
                                                    InPacket inPacket) {
        MapleChar chr = c.getChr();

        inPacket.decodeInt(); // update time
        short useItemPos = inPacket.decodeShort(); //Use Position
        short equipPos = inPacket.decodeShort(); //Eqp Position

        Item scroll = chr.getInventoryByType(InventoryType.CONSUME).getItemByIndex(useItemPos);
        InventoryType invType = equipPos < 0 ? EQUIPPED : EQUIP;
        Equip equip = (Equip) chr.getInventoryByType(invType).getItemByIndex(equipPos);
        if (!ItemUtils.isScrollingEquipValid(chr, scroll, equip) || !PotentialGradeCode.isHiddenPotential(equip.getGrade())) {
            return;
        }
        //TODO: need to handle calcs for adding option - and wz read it & also third line calc & recalc rates!
    }
}
