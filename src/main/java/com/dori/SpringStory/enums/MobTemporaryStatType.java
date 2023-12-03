package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum MobTemporaryStatType {
    Pad(0),
    Pdr(1),
    Mad(2),
    Mdr(3) ,
    Acc(4) ,
    Eva(5) ,
    Speed(6) ,
    Stun(7) ,
    Freeze(8),
    Poison(9) ,
    Seal(10),
    Darkness(11),
    PowerUp(12),
    MagicUp(13),
    PGuardUp(14),
    MGuardUp(15) ,
    Doom(16) ,
    Web(17),
    PImmune(18),
    MImmune(19) ,
    Showdown(20),
    HardSkin(21),
    Ambush(22) ,
    DamagedElemAttr(23),
    Venom(24),
    Blind(25),
    SealSkill(26),
    Burned(27),
    Dazzle(28),
    PCounter(29),
    MCounter(30),
    Disable(31),
    RiseByToss(32),
    BodyPressure(33),
    Weakness(34),
    TimeBomb(35),
    MagicCrash(36),
    HealByDamage(37)
    ;
    private final int bitPos, value;
    private final byte pos;

    MobTemporaryStatType(int bitPos){
        this.bitPos = bitPos;
        this.value = 1 << (bitPos % 32);
        this.pos = (byte) (bitPos >> 5);
    }

}
