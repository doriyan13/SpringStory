package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum UserEffectTypes {
    LevelUp(0), // No special handling required!
    SkillUse(1),
    SkillAffected(2),
    SkillAffectedSelect(3),
    SkillAffectedSpecial(4),
    Quest(5),
    PetShowEffect(6),
    ShowSkillSpecialEffect(7),
    ProtectOnDieItemUse(8),
    PortalSoundEffect(9), // No special handling required!
    JobChanged(10), // No special handling required!
    QuestComplete(11), // No special handling required!
    IncDecHPEffect(12),
    BuffItemEffect(13),
    SquibEffect(14),
    MonsterBookCard(15), // No special handling required!
    LotteryUse(16),
    ItemLevelUp(17), // No special handling required!
    ItemMaker(18),
    ExpItemConsumed(19), // No special handling required!
    ReservedEffect(20),
    Buff(21), // No special handling required!
    ConsumeEffect(22),
    UpgradeTombItemUse(23),
    BattlefieldItemUse(24),
    AvatarOriented(25),
    IncubatorUse(26),
    PlaySoundWithMuteBgm(27),
    SoulStoneUse(28), // spirit stone use notification | No special handling required!
    MakeIncDecHPEffect(29), //TODO: make sure it's correct?
    DeliveryQuestItemUse(30),
    RepeatEffectRemove(31), // RepeatEffectRemove
    EvolRing(32) // gives "gained upgrade potion for playing an hour" notification | EvolRing
    ;

    private final int val;

    UserEffectTypes(int val) {
        this.val = val;
    }
}
