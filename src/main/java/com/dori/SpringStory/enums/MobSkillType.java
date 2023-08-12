package com.dori.SpringStory.enums;

public enum MobSkillType {
    // Found it in local types in IDA so good to keep :D
    PowerUp(0x64),
    MagicUp(0x65),
    PGuardUp(0x66),
    MGuardUp(0x67),
    Haste(0x68),
    PowerUp_M(0x6E),
    MagicUp_M(0x6F),
    PGuardUp_M(0x70),
    MGuardUp_M(0x71),
    Heal_M(0x72),
    Haste_M(0x73),
    Seal(0x78),
    Darkness(0x79),
    Weakness(0x7A),
    Stun(0x7B),
    Curse(0x7C),
    Poison(0x7D),
    Slow(0x7E),
    Dispel(0x7F),
    Attract(0x80),
    BanMap(0x81),
    AreaFire(0x82),
    AreaPoison(0x83),
    ReverseInput(0x84),
    Undead(0x85),
    StopPortion(0x86),
    StopMotion(0x87),
    Fear(0x88),
    Frozen(0x89),
    PhysicalImmune(0x8C),
    MagicImmune(0x8D),
    HardSkin(0x8E),
    PCounter(0x8F),
    MCounter(0x90),
    PmCounter(0x91),
    Pad(0x96),
    Mad(0x97),
    Pdr(0x98),
    Mdr(0x99),
    Acc(0x9A),
    Eva(0x9B),
    Speed(0x9C),
    SealSkill(0x9D),
    BalrogCounter(0x9E),
    SpreadSkillFromUser(0xA0),
    HealByDamage(0xA1),
    Bind(0xA2),
    Summon(0xC8),
    SummonCube(0xC9);

    private final int value;

    MobSkillType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
