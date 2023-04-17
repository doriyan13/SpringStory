package com.dori.SpringStory.enums;

import java.util.Arrays;

public enum BodyPart {
    BPBase(0),
    Hair(0),
    Hat(1),
    FaceAccessory(2),
    EyeAccessory(3),
    Earrings(4),
    Top(5),
    Overall(5), // Top and overall share the same body part
    Bottom(6),
    Shoes(7),
    Gloves(8),
    Cape(9),
    Shield(10), // Includes things such as katara, 2ndary
    Weapon(11),
    Ring1(12),
    Ring2(13),
    PetEquip(14),
    Ring3(15),
    Ring4(16),
    Pendant(17),
    TamingMob(18),
    Saddle(19),
    MobEquip(20),
    PetRingLabel(21),
    PetItem(22),
    PetMeso(23),
    PetHpConsume(24),
    PetMpConsume(25),
    PetSweepForDrop(26),
    PetLongRange(27),
    PetPickUpOthers(28),
    PetRingQuote(29),
    Pet2Wear(30),
    Pet2Label(31),
    Pet2Quote(32),
    Pet2Item(33),
    Pet2Meso(34),
    Pet2SweepForDrop(35),
    Pet2LongRange(36),
    Pet2PickUpOthers(37),
    Pet3Wear(38),
    Pet3Label(39),
    Pet3Quote(40),
    Pet3Item(41),
    Pet3Meso(42),
    Pet3SweepForDrop(43),
    Pet3LongRange(44),
    Pet3PickUpOthers(45),
    Pet1IgnoreItems(46),
    Pet2IgnoreItems(47),
    Pet3IgnoreItems(48),
    Medal(49),
    Belt(50),
    Shoulder(51),
    Nothing3(54),
    Nothing2(55),
    Nothing1(56),
    Nothing0(57),
    ExtPendant1(59),
    Ext1(60),
    Ext2(61),
    Ext3(62),
    Ext4(63),
    Ext5(64),
    Ext6(65),
    Sticker(100),
    BPEnd(100),
    CBPBase(101), // CASH
    CashWeapon(111),
    PetConsumeHPItem(200),
    PetConsumeMPItem(201),
    CBPEnd(1000),
    EvanBase(1000),
    EvanHat(1000),
    EvanPendant(1001),
    EvanWing(1002),
    EvanShoes(1003),
    EvanEnd(1004),
    MechBase(1100),
    MachineEngine(1100),
    MachineArm(1101),
    MachineLeg(1102),
    MachineFrame(1103),
    MachineTransistor(1104),
    MechEnd(1105)
    ;

    private final int val;

    BodyPart(int val) {
        this.val = val;
    }

    public static BodyPart getByVal(int bodyPartVal) {
        return Arrays.stream(values()).filter(bp -> bp.getVal() == bodyPartVal).findAny().orElse(null);
    }

    public int getVal() {
        return val;
    }
}
