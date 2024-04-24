package com.dori.SpringStory.temporaryStats.characters;

import com.dori.SpringStory.enums.SkillStat;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum CharacterTemporaryStat {
    Pad(0, false),
    Pdd(1, false),
    Mad(2, false),
    Mdd(3, false),
    Acc(4, false),
    Evasion(5, false),
    Craft(6, false),
    Speed(7, false),
    Jump(8, false),
    MagicGuard(9, false),
    DarkSight(10, false), // dark-sight && hide skill
    Booster(11, false),
    PowerGuard(12, false),
    MaxHp(13, false),
    MaxMp(14, false),
    Invincible(15, false),
    SoulArrow(16, false),
    Stun(17, false),
    Poison(18, false),
    Seal(19, false),
    Darkness(20, false),
    ComboCounter(21, false),
    WeaponCharge(22, false),
    DragonBlood(23, false),
    HolySymbol(24, false),
    MesoUp(25, false),
    ShadowPartner(26, false),
    PickPocket(27, false),
    MesoGuard(28, false),
    Thaw(29, false),
    Weakness(30, false),
    Curse(31, false),
    Slow(32, false),
    Morph(33, false),
    Ghost(49, false),
    Regen(34, false),
    BasicStatUp(35, false),
    Stance(36, false),
    SharpEyes(37, false),
    ManaReflection(38, false),
    Attract(39, false),
    SpiritJavelin(40, false),
    Infinity(41, false),
    HolyShield(42, false),
    HamString(43, false),
    Blind(44, false),
    Concentration(45, false),
    BanMap(46, false),
    MaxLevelBuff(47, false),
    Barrier(50, false),
    DojangShield(62, false),
    ReverseInput(51, false),
    MesoUpByItem(48, false),
    ItemUpByItem(52, false),
    RespectPImmune(53, false),
    RespectMImmune(54, false),
    DefenseAtt(55, false),
    DefenseState(56, false),
    IncEffectHPPotion(57, false),
    IncEffectMPPotion(58, false),
    DojangBerserk(59, false),
    DojangInvincible(60, false),
    Spark(61, false),
    SoulMasterFinal(63, false),
    WindBreakerFinal(64, false),
    ElementalReset(65, false),
    WindWalk(66, false),
    EventRate(67, false),
    ComboAbilityBuff(68, false),
    ComboDrain(69, false), // Aran combo!
    ComboBarrier(70, false), // Aran combo!
    BodyPressure(71, false),
    SmartKnockback(72, false),
    RepeatEffect(73, false),
    ExpBuffRate(74, false),
    StopPortion(75, false),
    StopMotion(76, false),
    Fear(77, false),
    EvanSlow(78, false),
    MagicShield(79, false),
    MagicResistance(80, false),
    SoulStone(81, false),
    Flying(82, false),
    Frozen(83, false),
    AssistCharge(84, false),
    Enrage(85, false),
    SuddenDeath(86, false),
    NotDamaged(87, false),
    FinalCut(88, false),
    ThornsEffect(89, false),
    SwallowAttackDamage(90, false),
    MoreWildDamageUp(91, false),
    Mine(92, false),
    ExtraMaxHp(93, false),
    ExtraMaxMp(94, false),
    ExtraPad(95, false),
    ExtraPdd(96, false),
    ExtraMdd(97, false),
    Guard(98, false),
    SafetyDamage(99, false),
    SafetyAbsorb(100, false),
    Cyclone(101, false),
    SwallowCritical(102, false),
    SwallowMaxMP(103, false),
    SwallowDefence(104, false),
    SwallowEvasion(105, false),
    Conversion(106, false),
    Revive(107, false),
    Sneak(108, false),
    Mechanic(109, false),
    Aura(110, false),
    DarkAura(111, false),
    BlueAura(112, false),
    YellowAura(113, false),
    SuperBody(114, false),
    MoreWildMaxHP(115, false),
    Dice(116, false),
    BlessingArmor(117, false),
    DamR(118, false),
    TeleportMasteryOn(119, false),
    CombatOrders(120, false),
    Beholder(121, false),
    EnergyCharged(122, true),
    DashSpeed(123, true),
    DashJump(124, true),
    RideVehicle(125, true),
    PartyBooster(126, true),
    GuidedBullet(127, true),
    UnDead(128, true),
    SummonBomb(129, false);

    private final int bitPos;
    private final boolean twoState;

    CharacterTemporaryStat(int bitPos, boolean twoState) {
        this.bitPos = bitPos;
        this.twoState = twoState;
    }

    public boolean isMovingEffectingStat() {
        return switch (this) {
            case Speed, Jump, Stun, Weakness, Slow, Morph, Ghost, BasicStatUp, Attract, Flying, Frozen, Mechanic ->
                    true;
            default -> false;
        };
    }

    public boolean isSwallowStat() {
        return switch (this) {
            case SwallowAttackDamage, SwallowCritical, SwallowDefence, SwallowMaxMP, SwallowEvasion -> true;
            default -> false;
        };
    }

    public static List<CharacterTemporaryStat> getEncodingLocalStats() {
        return Arrays.asList(
                Pad, Pdd, Mad, Mdd, Acc, Evasion, Craft, Speed, Jump, MagicGuard,
                DarkSight, // dark-sight && hide skill
                Booster,
                PowerGuard,
                MaxHp,
                MaxMp,
                Invincible,
                SoulArrow,
                Stun,
                Poison,
                Seal,
                Darkness,
                ComboCounter,
                WeaponCharge,
                DragonBlood,
                HolySymbol,
                MesoUp,
                ShadowPartner,
                PickPocket,
                MesoGuard,
                Thaw,
                Weakness,
                Curse,
                Slow,
                Morph,
                Ghost,
                Regen,
                BasicStatUp,
                Stance,
                SharpEyes,
                ManaReflection,
                Attract,
                SpiritJavelin,
                Infinity,
                HolyShield,
                HamString,
                Blind,
                Concentration,
                BanMap,
                MaxLevelBuff,
                Barrier,
                DojangShield,
                ReverseInput,
                MesoUpByItem,
                ItemUpByItem,
                RespectPImmune,
                RespectMImmune,
                DefenseAtt,
                DefenseState,
                IncEffectHPPotion,
                IncEffectMPPotion,
                DojangBerserk,
                DojangInvincible,
                Spark,
                SoulMasterFinal,
                WindBreakerFinal,
                ElementalReset,
                WindWalk,
                EventRate,
                ComboAbilityBuff,
                ComboDrain, // Aran combo!
                ComboBarrier, // Aran combo!
                BodyPressure,
                SmartKnockback,
                RepeatEffect,
                ExpBuffRate,
                StopPortion,
                StopMotion,
                Fear,
                EvanSlow,
                MagicShield,
                MagicResistance,
                SoulStone,
                Flying,
                Frozen,
                AssistCharge,
                Enrage,
                SuddenDeath,
                NotDamaged,
                FinalCut,
                ThornsEffect,
                SwallowAttackDamage,
                MoreWildDamageUp,
                Mine,
                ExtraMaxHp,
                ExtraMaxMp,
                ExtraPad,
                ExtraPdd,
                ExtraMdd,
                Guard,
                SafetyDamage,
                SafetyAbsorb,
                Cyclone,
                SwallowCritical,
                SwallowMaxMP,
                SwallowDefence,
                SwallowEvasion,
                Conversion,
                Revive,
                Sneak,
                Mechanic,
                Aura,
                DarkAura,
                BlueAura,
                YellowAura,
                SuperBody,
                MoreWildMaxHP,
                Dice,
                BlessingArmor,
                DamR,
                TeleportMasteryOn,
                CombatOrders,
                Beholder);
    }

    public static List<CharacterTemporaryStat> getEncodingRemoteStats() {
        return Arrays.asList(
                Speed, ComboCounter, WeaponCharge, Stun, Darkness, Seal, Weakness, Curse, Poison, ShadowPartner, DarkSight,
                SoulArrow, Morph, Ghost, Attract, SpiritJavelin, BanMap, Barrier, DojangShield, ReverseInput,
                RespectPImmune, RespectMImmune, DefenseAtt, DefenseState, DojangBerserk, DojangInvincible, WindWalk,
                RepeatEffect, StopPortion, StopMotion, Fear, MagicShield, Flying, Frozen, SuddenDeath, FinalCut, Cyclone,
                Sneak, MoreWildDamageUp, Mechanic, DarkAura, BlueAura, YellowAura, BlessingArmor
        );
    }

    public static List<CharacterTemporaryStat> getEncodingTwoStateOrderRemote() {
        return Arrays.asList(
                EnergyCharged,
                DashSpeed,
                DashJump,
                RideVehicle,
                PartyBooster,
                GuidedBullet,
                UnDead);
    }

    public static CharacterTemporaryStat getCtsFromSkillStat(SkillStat skillStat) {
        CharacterTemporaryStat cts = null;
        switch (skillStat) {
            case morph -> cts = Morph;
            case pad -> cts = Pad;
            case pdd -> cts = Pdd;
            case mad -> cts = Mad;
            case mdd -> cts = Mdd;
            case damR -> cts = DamR;
            case jump -> cts = Jump;
            case acc -> cts = Acc;
            case eva -> cts = Evasion;
            case speed -> cts = Speed;
            case emhp -> cts = ExtraMaxHp;
            case emmp -> cts = ExtraMaxMp;
            case epad -> cts = ExtraPad;
            case epdd -> cts = ExtraPdd;
            case emdd -> cts = ExtraMdd;
        }
        return cts;
    }
}
