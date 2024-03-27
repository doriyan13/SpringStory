package com.dori.SpringStory.utils;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.packets.CUser;
import com.dori.SpringStory.constants.ItemConstants;
import com.dori.SpringStory.dataHandlers.ItemDataHandler;
import com.dori.SpringStory.dataHandlers.dataEntities.EquipData;
import com.dori.SpringStory.dataHandlers.dataEntities.ItemData;
import com.dori.SpringStory.dataHandlers.dataEntities.ItemOptionData;
import com.dori.SpringStory.enums.*;
import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.world.fieldEntities.Drop;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.dori.SpringStory.constants.GameConstants.*;
import static com.dori.SpringStory.constants.ItemConstants.WHITE_SCROLL_ID;

@SuppressWarnings("unused")
@Component
public interface ItemUtils {

    Random rnd = new Random();

    private static int getItemPrefix(int nItemID) {
        return nItemID / 10000;
    }

    private static int getGenderFromItemID(int nItemID) {
        if (nItemID / 1000000 != 1 && getItemPrefix(nItemID) != 254 || getItemPrefix(nItemID) == 119 || getItemPrefix(nItemID) == 168) {
            return 2;
        }
        int result;
        switch (nItemID / 1000 % 10) {
            case 0 -> result = 0;
            case 1 -> result = 1;
            default -> result = 2;
        }
        return result;
    }

    static BodyPart getBodyPartFromItem(int itemID) {
        EquipPrefix prefix = EquipPrefix.getByVal(getItemPrefix(itemID));
        BodyPart bodyPart = BodyPart.BPBase;
        if (prefix != null) {
            switch (prefix) {
                case Hat -> bodyPart = BodyPart.Hat;
                case FaceAccessory -> bodyPart = BodyPart.FaceAccessory;
                case EyeAccessory -> bodyPart = BodyPart.EyeAccessory;
                case Earrings -> bodyPart = BodyPart.Earrings;
                case Top, Overall -> bodyPart = BodyPart.Top;
                case Bottom -> bodyPart = BodyPart.Bottom;
                case Shoes -> bodyPart = BodyPart.Shoes;
                case Gloves -> bodyPart = BodyPart.Gloves;
                case Shield, Katara, SecondaryWeapon -> bodyPart = BodyPart.Shield;
                case Cape -> bodyPart = BodyPart.Cape;
                case Ring -> bodyPart = BodyPart.Ring1;
                case Pendant -> bodyPart = BodyPart.Pendant;
                case Belt -> bodyPart = BodyPart.Belt;
                case Medal -> bodyPart = BodyPart.Medal;
                case Shoulder -> bodyPart = BodyPart.Shoulder;
                case PetWear -> bodyPart = BodyPart.PetEquip;
                case TamingMob -> bodyPart = BodyPart.TamingMob;
                case Saddle -> bodyPart = BodyPart.Saddle;
                case EvanHat -> bodyPart = BodyPart.EvanHat;
                case EvanPendant -> bodyPart = BodyPart.EvanPendant;
                case EvanWing -> bodyPart = BodyPart.EvanWing;
                case EvanShoes -> bodyPart = BodyPart.EvanShoes;
                case OneHandedAxe, OneHandedSword, OneHandedBluntWeapon, TwoHandedBluntWeapon, TwoHandedAxe, TwoHandedSword, PoleArm, Spear,
                        Staff, Wand, Bow, Crossbow, Claw, Dagger, Gauntlet, Gun, Knuckle, Katana ->
                        bodyPart = BodyPart.Weapon;
                case CashWeapon -> bodyPart = BodyPart.CashWeapon;
                default -> System.out.println("idk? " + prefix);
            }
        }
        return bodyPart;
    }

    static void fillEquipsMaps(MapleChar chr,
                               Map<BodyPart, Integer> charEquips,
                               Map<BodyPart, Integer> charMaskedEquips,
                               List<Integer> cWeapon) {
        for (Item item : chr.getEquippedInventory().getItems()) {
            BodyPart bodyPart = getBodyPartFromItem(item.getItemId());
            if (bodyPart != BodyPart.BPBase) {
                if (bodyPart.getVal() < BodyPart.BPEnd.getVal()) {
                    if (!charEquips.containsKey(bodyPart)) {
                        charEquips.put(bodyPart, item.getItemId());
                    } else if (item.isCash()) {
                        int nonCashItem = charEquips.remove(bodyPart);
                        charEquips.put(bodyPart, item.getItemId());
                        charMaskedEquips.put(bodyPart, nonCashItem);
                    } else {
                        charMaskedEquips.put(bodyPart, item.getItemId());
                    }
                }
                if (bodyPart.getVal() < BodyPart.BPEnd.getVal() && !item.isCash()) {
                    charEquips.put(bodyPart, item.getItemId());
                } else if (bodyPart.getVal() < BodyPart.BPEnd.getVal() && bodyPart.getVal() != BodyPart.CashWeapon.getVal()) {
                    charMaskedEquips.put(bodyPart, item.getItemId());
                } else if (bodyPart.getVal() == BodyPart.CashWeapon.getVal()) {
                    cWeapon.add(item.getItemId());
                }
            }
        }
    }

    static boolean isBullet(int itemID) {
        return getItemPrefix(itemID) == 233;
    }

    static boolean isBowArrow(int itemID) {
        return itemID / 1000 == 2060;
    }

    static boolean isEnhancementScroll(int scrollID) {
        return scrollID / 100 == 20493;
    }

    static boolean isHat(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Hat.getVal();
    }

    static boolean isWeapon(int itemID) {
        return itemID >= 1210000 && itemID < 1600000;
    }

    static boolean isSecondary(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.SecondaryWeapon.getVal();
    }

    static boolean isShield(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Shield.getVal();
    }

    static boolean isAccessory(int itemID) {
        return (itemID >= 1010000 && itemID < 1040000) || (itemID >= 1122000 && itemID < 1153000) ||
                (itemID >= 1112000 && itemID < 1113000) || (itemID >= 1670000 && itemID < 1680000);
    }

    static boolean isFaceAccessory(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.FaceAccessory.getVal();
    }

    static boolean isEyeAccessory(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.EyeAccessory.getVal();
    }

    static boolean isEarrings(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Earrings.getVal();
    }

    static boolean isTop(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Top.getVal();
    }

    static boolean isOverall(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Overall.getVal();
    }

    static boolean isBottom(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Bottom.getVal();
    }

    static boolean isShoe(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Shoes.getVal();
    }

    static boolean isGlove(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Gloves.getVal();
    }

    static boolean isCape(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Cape.getVal();
    }

    static boolean isRing(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Ring.getVal();
    }

    static boolean isPendant(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Pendant.getVal();
    }

    static boolean isBelt(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Belt.getVal();
    }

    static boolean isMedal(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Medal.getVal();
    }

    static boolean isShoulder(int itemID) {
        return getItemPrefix(itemID) == EquipPrefix.Shoulder.getVal();
    }

    static boolean isArmor(int itemID) {
        return !isAccessory(itemID) && !isWeapon(itemID);
    }

    static boolean isThrowingItem(int itemID) {
        return isThrowingStar(itemID) || isBullet(itemID) || isBowArrow(itemID);
    }

    static boolean isThrowingStar(int itemID) {
        return getItemPrefix(itemID) == 207;
    }

    static boolean canEquipTypeHavePotential(int itemId) {
        return isRing(itemId) ||
                isPendant(itemId) ||
                isWeapon(itemId) ||
                isBelt(itemId) ||
                isHat(itemId) ||
                isFaceAccessory(itemId) ||
                isEyeAccessory(itemId) ||
                isOverall(itemId) ||
                isTop(itemId) ||
                isBottom(itemId) ||
                isShoe(itemId) ||
                isEarrings(itemId) ||
                isShoulder(itemId) ||
                isGlove(itemId) ||
                isShield(itemId) ||
                isCape(itemId);
    }

    static boolean shouldEncodeEquipByType(EquipType type,
                                           Equip equip) {
        boolean result = true;
        switch (type) {
            case Equipped ->
                    result = equip.getBagIndex() > BodyPart.BPBase.getVal() && equip.getBagIndex() < BodyPart.BPEnd.getVal();
            case Cash ->
                    result = equip.getBagIndex() >= BodyPart.CBPBase.getVal() && equip.getBagIndex() <= BodyPart.CBPEnd.getVal();
            case Evan ->
                    result = equip.getBagIndex() >= BodyPart.EvanBase.getVal() && equip.getBagIndex() < BodyPart.EvanEnd.getVal();
            case Mechanic ->
                    result = equip.getBagIndex() >= BodyPart.MechBase.getVal() && equip.getBagIndex() < BodyPart.MechEnd.getVal();
        }
        return result;
    }

    static boolean isPet(int itemId) {
        return getItemPrefix(itemId) == 500;
    }

    static boolean isFullItemConsume(@NotNull Item item,
                                     int quantity) {
        return item.getQuantity() + quantity <= 0 && !ItemUtils.isThrowingItem(item.getItemId());
    }

    static boolean isDropMeso(@NotNull Drop drop) {
        return drop.getItem() == null && drop.getQuantity() > 0;
    }

    static boolean isOneOfAKindItem(@Nullable Item item) {
        return item != null && item.isOnly();
    }

    static boolean isChrCanObtainOneOfAKindItem(@NotNull MapleChar chr,
                                                @Nullable Item item) {
        if (item == null) {
            return false;
        }
        return switch (item.getType()) {
            case EQUIP -> !chr.haveEquip(item.getItemId());
            case BUNDLE -> !chr.haveItem(item.getItemId());
            case null, default -> false;
        };
    }

    static boolean isChrCanObtainItem(@NotNull MapleChar chr,
                                      @NotNull Drop drop) {
        Item droppedItem = drop.getItem();
        return ItemUtils.isDropMeso(drop)
                || !ItemUtils.isOneOfAKindItem(droppedItem)
                || isChrCanObtainOneOfAKindItem(chr, droppedItem);
    }

    static boolean willDrop(double chance,
                            float dropRate) {
        float randomValue = rnd.nextFloat();
        return randomValue <= (chance * dropRate);
    }

    static int getRandom(int lower, int upper) {
        return rnd.nextInt((upper - lower) + 1) + lower;
    }

    static boolean willSuccess(int chance) {
        return getRandom(0, 100) < chance;
    }

    static boolean isScrollingEquipValid(@NotNull MapleChar chr,
                                         @Nullable Item scroll,
                                         @Nullable Equip equip) {
        if (equip == null) {
            chr.message("Couldn't find the scroll, scrolling failed! ~", ChatType.SpeakerChannel);
            chr.enableAction();
            return false;
        } else if (scroll == null) {
            chr.message("Couldn't find the equip, scrolling failed! ~", ChatType.SpeakerChannel);
            chr.enableAction();
            return false;
        }
        return true;
    }

    static boolean isEquipScrollable(@NotNull MapleChar chr,
                                     @NotNull Equip equip,
                                     boolean recover) {
        if (equip.getTuc() <= 0 && !recover) {
            chr.message("No more upgrade slots available!", ChatType.SpeakerChannel);
            chr.enableAction();
            return false;
        }
        return true;
    }

    static boolean isScrollDataValid(@NotNull MapleChar chr,
                                     @Nullable ItemData scrollInfo) {
        if (scrollInfo == null || scrollInfo.getScrollStats().isEmpty()) {
            chr.message("Couldn't find the scroll data, scrolling failed! ~", ChatType.SpeakerChannel);
            chr.enableAction();
            return false;
        }
        return true;
    }

    static boolean isWhiteScrollValid(@NotNull MapleChar chr,
                                      @Nullable Item whiteScroll) {
        MapleClient c = chr.getMapleClient();
        if (whiteScroll == null) {
            c.logout();
            return false;
        }
        if (whiteScroll.getQuantity() < 1) {
            chr.removeItem(InventoryType.CONSUME, whiteScroll.getItemId());
            c.logout();
            return false;
        }
        return true;
    }

    static void applyWhiteScrollToChar(@NotNull MapleChar chr) {
        Item whiteScroll = chr.getConsumeInventory().getItemByItemID(WHITE_SCROLL_ID);
        if (!isWhiteScrollValid(chr, whiteScroll)) {
            return;
        }
        chr.consumeItem(InventoryType.CONSUME, whiteScroll.getItemId(), 1);
    }

    static void applyChaosScrollToEquip(@NotNull Equip equip,
                                        @NotNull Map<ScrollStat, Integer> scrollStats) {
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
    }

    static void applyCleanSlateToEquip(@NotNull Equip equip) {
        EquipData equipData = ItemDataHandler.getEquipDataByID(equip.getItemId());
        if (equipData != null) {
            int maxTuc = equipData.getTuc();
            if (equip.getTuc() + equip.getCuc() < maxTuc) {
                equip.addStat(EquipBaseStat.tuc, 1);
            }
        }
    }

    static void applyNormalScrollToEquip(@NotNull Equip equip,
                                         @NotNull Map<ScrollStat, Integer> scrollStats) {
        for (Map.Entry<ScrollStat, Integer> entry : scrollStats.entrySet()) {
            ScrollStat ss = entry.getKey();
            int val = entry.getValue();
            if (ss.getEquipStat() != null) {
                equip.addStat(ss.getEquipStat(), val);
            }
        }
    }

    static void applyDarkScrollBoom(@NotNull MapleChar chr,
                                    @NotNull Equip equip,
                                    @NotNull Item scroll,
                                    boolean bEnchantSkill,
                                    boolean bWhiteScroll) {
        chr.removeItem(equip.getInvType(), equip.getItemId());
        chr.write(CUser.showItemUpgradeEffect(chr.getId(), false, true, bEnchantSkill, bWhiteScroll, 0));
    }

    static boolean canEnchantmentEquip(@NotNull MapleChar chr,
                                       @NotNull Equip equip) {
        if (equip.getStarUpgradeCount() >= 17) {
            chr.message("The item is fully enchantment!", ChatType.SpeakerChannel);
            chr.enableAction();
            return false;
        }
        return true;
    }

    static void applyEnchantmentBoom(@NotNull MapleChar chr,
                                     @NotNull Equip equip,
                                     @NotNull Item scroll,
                                     boolean bEnchantSkill) {
        chr.consumeItem(InventoryType.CONSUME, scroll.getItemId(), 1);
        chr.removeItem(equip.getInvType(), equip.getItemId());
        chr.write(CUser.showItemHyperUpgradeEffect(chr.getId(), false, bEnchantSkill, 0));
    }

    static boolean isHyperUpgradeItem(int itemID) {
        return itemID / 100 == 20493;
    }

    static void applyEnchantment(@NotNull Equip equip) {
        // upgrade the amount of stars by 1
        equip.setStarUpgradeCount((short) (equip.getStarUpgradeCount() + 1));
        // handle all the stats changes - exist + percentage
        if (equip.getIStr() > 0 || ItemUtils.willSuccess(BASE_STAT_CHANCE)) {
            equip.addStat(EquipBaseStat.iStr, ItemUtils.getRandom(0, BASE_STAT_ENHANCEMENT));
        }
        if (equip.getIDex() > 0 || ItemUtils.willSuccess(BASE_STAT_CHANCE)) {
            equip.addStat(EquipBaseStat.iDex, ItemUtils.getRandom(0, BASE_STAT_ENHANCEMENT));
        }
        if (equip.getILuk() > 0 || ItemUtils.willSuccess(BASE_STAT_CHANCE)) {
            equip.addStat(EquipBaseStat.iLuk, ItemUtils.getRandom(0, BASE_STAT_ENHANCEMENT));
        }
        if (equip.getIInt() > 0 || ItemUtils.willSuccess(BASE_STAT_CHANCE)) {
            equip.addStat(EquipBaseStat.iInt, ItemUtils.getRandom(0, BASE_STAT_ENHANCEMENT));
        }
        if (equip.getIMaxHp() > 0) {
            equip.addStat(EquipBaseStat.iMaxHP, ItemUtils.getRandom(0, BASE_HP_MP_ENHANCEMENT));
        }
        if (equip.getIMaxMp() > 0) {
            equip.addStat(EquipBaseStat.iMaxMP, ItemUtils.getRandom(0, BASE_HP_MP_ENHANCEMENT));
        }
        if (equip.getIPad() > 0) {
            equip.addStat(EquipBaseStat.iPAD, ItemUtils.getRandom(0, BASE_ATK_ENHANCEMENT));
        }
        if (equip.getIMad() > 0) {
            equip.addStat(EquipBaseStat.iMAD, ItemUtils.getRandom(0, BASE_ATK_ENHANCEMENT));
        }
        if (equip.getIPDD() > 0) {
            equip.addStat(EquipBaseStat.iPDD, ItemUtils.getRandom(0, BASE_SECONDARY_STAT_ENHANCEMENT));
        }
        if (equip.getIMDD() > 0) {
            equip.addStat(EquipBaseStat.iMDD, ItemUtils.getRandom(0, BASE_SECONDARY_STAT_ENHANCEMENT));
        }
        if (equip.getIAcc() > 0) {
            equip.addStat(EquipBaseStat.iACC, ItemUtils.getRandom(0, BASE_SECONDARY_STAT_ENHANCEMENT));
        }
        if (equip.getIEva() > 0) {
            equip.addStat(EquipBaseStat.iEVA, ItemUtils.getRandom(0, BASE_SECONDARY_STAT_ENHANCEMENT));
        }
        if (equip.getISpeed() > 0) {
            equip.addStat(EquipBaseStat.iSpeed, ItemUtils.getRandom(0, BASE_MOBILITY_STAT_ENHANCEMENT));
        }
        if (equip.getIJump() > 0) {
            equip.addStat(EquipBaseStat.iJump, ItemUtils.getRandom(0, BASE_MOBILITY_STAT_ENHANCEMENT));
        }
    }

    static boolean isNotItemOptionUpgradeItem(int nItemID) {
        return nItemID / 100 != 20494;
    }

    static boolean canEquipHavePotential(Equip equip) {
        return !equip.isCash() &&
                canEquipTypeHavePotential(equip.getItemId()) &&
                (ItemDataHandler.getEquipDataByID(equip.getItemId()).getTuc() >= 1 || isSecondary(equip.getItemId()));
    }

    static void applyPotentialBoom(@NotNull MapleChar chr,
                                   @NotNull Equip equip,
                                   @NotNull Item scroll,
                                   boolean bEnchantSkill) {
        chr.consumeItem(InventoryType.CONSUME, scroll.getItemId(), 1);
        chr.removeItem(equip.getInvType(), equip.getItemId());
        chr.write(CUser.showOptionItemUpgradeEffect(chr.getId(), false, bEnchantSkill, 0));
    }

    static PotentialGrade revealNewPotentialGrade(@NotNull Equip equip) {
        PotentialGrade grade = PotentialGrade.transformHiddenPotentialToRevealed(equip.getGrade());
        if (grade == PotentialGrade.Rare && ItemUtils.willSuccess(EPIC_POTENTIAL_RANK_UP_PERCENTAGE)) {
            grade = PotentialGrade.Epic;
        } else if (grade == PotentialGrade.Epic && ItemUtils.willSuccess(UNIQUE_POTENTIAL_RANK_UP_PERCENTAGE)) {
            grade = PotentialGrade.Unique;
        }
        return grade;
    }

    static ItemOptionEquipType getItemOptionEquipType(int equipID) {
        ItemOptionEquipType itemOptionEquipType = null;
        if (isShield(equipID)) {
            itemOptionEquipType = ItemOptionEquipType.Armor;
        } else if (isWeapon(equipID) || isSecondary(equipID)) {
            itemOptionEquipType = ItemOptionEquipType.Weapon;
        } else if (isRing(equipID) || isPendant(equipID) || isFaceAccessory(equipID) || isEyeAccessory(equipID) || isEarrings(equipID)) {
            itemOptionEquipType = ItemOptionEquipType.Accessory;
        } else if (isHat(equipID)) {
            itemOptionEquipType = ItemOptionEquipType.Hat;
        } else if (isTop(equipID) || isOverall(equipID)) {
            itemOptionEquipType = ItemOptionEquipType.Top;
        } else if (isBottom(equipID)) {
            itemOptionEquipType = ItemOptionEquipType.Bottom;
        } else if (isShoe(equipID)) {
            itemOptionEquipType = ItemOptionEquipType.Shoes;
        } else if (isGlove(equipID)) {
            itemOptionEquipType = ItemOptionEquipType.Glove;
        } else if (isArmor(equipID) || isShoulder(equipID) || isBelt(equipID)) {
            itemOptionEquipType = ItemOptionEquipType.Armor;
        } else {
            itemOptionEquipType = ItemOptionEquipType.AnyExceptWeapon;
        }
        return itemOptionEquipType;
    }

    static List<ItemOptionData> getOptionalPotentialsForEquip(@NotNull Equip equip,
                                                              @NotNull ItemOptionEquipType itemOptionEquipType) {
        return ItemDataHandler.getAllItemOptions()
                .stream()
                .filter(itemOption -> itemOption.getReqLevel() <= (equip.getRLevel() + equip.getIIncReq()) // throw out incorrect level range
                        && (itemOption.getOptionType() == itemOptionEquipType.getVal() || itemOption.getOptionType() == ItemOptionEquipType.AnyEquip.getVal())
                        && PotentialGrade.getItemOptionPotentialGrade(itemOption.getId()) == equip.getGrade() // throw out incorrect grade codes
                        && itemOption.getOptionType() != 90) // these show "Hidden" for some reason o___o
                .toList();
    }

    static int getEquipAmountOfPotentialLines(@NotNull Equip equip) {
        int index = 0;
        for (Integer option : equip.getOptions()) {
            if (option == 0) {
                break;
            }
            index++;
        }
        int amountOfLines = index > 0 ? equip.getOptions().size() : MIN_AMOUNT_OF_POTENTIAL_LINES;
        if (amountOfLines < MAX_AMOUNT_OF_POTENTIAL_LINES && ItemUtils.willSuccess(ADDITIONAL_LINE_POTENTIAL_PERCENTAGE)) {
            amountOfLines += 1;
        }
        return amountOfLines;
    }
}
