package com.dori.SpringStory.enums;

import java.util.Arrays;

public enum MovementPathAttr {
    Normal(0),
    Jump(1),
    Impact(2),
    Immediate(3),
    Teleport(4),
    HangOnBack(5),
    Assaulter(6),
    Assassination(7),
    Rush(8),
    StatChange(9),
    SitDown(10),
    StartFallDown(11),
    FallDown(12),
    StartWings(13),
    Wings(14),
    AranAdjust(15),
    MobToss(16),
    FlyingBlock(17),
    DashSlide(18),
    BattleMageAdjust(19),
    FlashJump(20),
    RocketBooster(21),
    BackStepShot(22),
    MobPowerKnockBack(23),
    VerticalJump(24),
    CustomImpact(25),
    CombatStep(26),
    Hit(27),
    TimeBombAtk(28),
    SnowBallTouch(29),
    BuffZoneEffect(30),
    MobLadder(31),
    MobRightAngle(32),
    MobStopNodeStart(33),
    MobBeforeNode(34),
    MobAtkRush(35),
    MobAtkRushStop(36)
    ;

    private final int val;

    MovementPathAttr(int val){
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static MovementPathAttr getElementByNum(byte attrNum){
        return Arrays.stream(values())
                .filter(mpa -> mpa.getVal() == attrNum)
                .findFirst()
                .orElse(Normal);
    }
}
