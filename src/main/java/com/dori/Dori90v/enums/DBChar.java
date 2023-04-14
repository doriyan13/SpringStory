package com.dori.Dori90v.enums;

/**
 * Flags of encoding CharactersInfo.
 * @author Dori
 */
public enum DBChar {
    Character(0x1),
    Money(0x2),
    ItemSlotEquip(0x4),
    ItemSlotConsume(0x8),
    ItemSlotInstall(0x10),
    ItemSlotEtc(0x20),
    ItemSlotCash(0x40),
    InventorySize(0x80),
    SkillRecord(0x100),
    QuestRecord(0x200),
    MiniGameRecord(0x400),
    CoupleRecord(0x800),
    MapTransfer(0x1000),
    Avatar(0x2000),
    QuestComplete(0x4000),
    SkillCooltime(0x8000),
    MonsterBookCard(0x10000),
    MonsterBookCover(0x20000),
    QuestRecordEx(0x40000),
    NewYearCard(0x80000),
    AdminShopCount(0x100000),
    EquipExt(0x100000),
    WildHunterInfo(0x200000),
    QuestCompleteOld(0x400000),
    Familiar(0x800000),
    ItemPot(0x800000), // was Visitor
    CoreAura(0x1000000),
    ExpConsumeItem(0x2000000),
    RedLeafInfo(0x2000000),
    ShopBuyLimit(0x4000000),
    VisitorLog(0x800000),
    VisitorLog1(0x1000000),
    VisitorLog2(0x2000000),
    VisitorLog3(0x4000000),
    VisitorLog4(0x8000000),
    All(-1),
    Specific(0x1 ^ 0x2 ^ 0x80 ^ 0x100000 ^ 0x4 ^ 0x8 ^ 0x10 ^ 0x20 ^ 0x40)
    ;

    public final long uFlag;

    DBChar(long uFlag) {
        this.uFlag = uFlag;
    }

    public long getFlag() {
        return uFlag;
    }

    public boolean isInMask(long mask){
        return (mask & getFlag()) != 0;
    }

    public boolean isInMask(DBChar mask){
        return (mask.getFlag() & getFlag()) != 0;
    }
}
