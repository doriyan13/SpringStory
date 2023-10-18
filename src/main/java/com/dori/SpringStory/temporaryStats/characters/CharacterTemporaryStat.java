package com.dori.SpringStory.temporaryStats.characters;

import com.dori.SpringStory.enums.SkillStat;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum CharacterTemporaryStat {
    Pad(0),
    Pdd(1),
    Mad(2),
    Mdd(3),
    Acc(4),
    Evasion(5),
    Craft(6),
    Speed(7),
    Jump(8),
    MagicGuard(9),
    DarkSight(10),
    Booster(11),
    PowerGuard(12),
    MaxHp(13),
    MaxMp(14),
    Invincible(15),
    SoulArrow(16),
    Stun(17),
    Poison(18),
    Seal(19),
    Darkness(20),
    ComboCounter(21),
    WeaponCharge(22),
    DragonBlood(23),
    HolySymbol(24),
    MesoUp(25),
    ShadowPartner(26),
    PickPocket(27),
    MesoGuard(28),
    Thaw(29),
    Weakness(30),
    Curse(31),
    Slow(32),
    Morph(33),
    Ghost(49),
    Regen(34),
    BasicStatUp(35),
    Stance(36),
    SharpEyes(37),
    ManaReflection(38),
    Attract(39),
    SpiritJavelin(40),
    Infinity(41),
    HolyShield(42),
    HamString(43),
    Blind(44),
    Concentration(45),
    BanMap(46),
    MaxLevelBuff(47),
    Barrier(50),
    DojangShield(62),
    ReverseInput(51),
    MesoUpByItem(48),
    ItemUpByItem(52),
    RespectPImmune(53),
    RespectMImmune(54),
    DefenseAtt(55),
    DefenseState(56),
    IncEffectHPPotion(57),
    IncEffectMPPotion(58),
    DojangBerserk(59),
    DojangInvincible(60),
    Spark(61),
    SoulMasterFinal(63),
    WindBreakerFinal(64),
    ElementalReset(65),
    WindWalk(66),
    EventRate(67),
    ComboAbilityBuff(68),
    ComboDrain(69),
    ComboBarrier(70),
    BodyPressure(71),
    SmartKnockback(72),
    RepeatEffect(73),
    ExpBuffRate(74),
    StopPortion(75),
    StopMotion(76),
    Fear(77),
    EvanSlow(78),
    MagicShield(79),
    MagicResistance(80),
    SoulStone(81),
    Flying(82),
    Frozen(83),
    AssistCharge(84),
    Enrage(85),
    SuddenDeath(86),
    NotDamaged(87),
    FinalCut(88),
    ThornsEffect(89),
    SwallowAttackDamage(90),
    MoreWildDamageUp(91),
    Mine(92),
    ExtraMaxHp(93),
    ExtraMaxMp(94),
    ExtraPad(95),
    ExtraPdd(96),
    ExtraMdd(97),
    Guard(98),
    SafetyDamage(99),
    SafetyAbsorb(100),
    Cyclone(101),
    SwallowCritical(102),
    SwallowMaxMP(103),
    SwallowDefence(104),
    SwallowEvasion(105),
    Conversion(106),
    Revive(107),
    Sneak(108),
    Mechanic(109),
    Aura(110),
    DarkAura(111),
    BlueAura(112),
    YellowAura(113),
    SuperBody(114),
    MoreWildMaxHP(115),
    Dice(116),
    BlessingArmor(117),
    DamR(118),
    TeleportMasteryOn(119),
    CombatOrders(120),
    Beholder(121),
    EnergyCharged(122),
    DashSpeed(123),
    DashJump(124),
    RideVehicle(125),
    PartyBooster(126),
    GuidedBullet(127),
    UnDead(128),
    SummonBomb(129);

    private final int bitPos;

    CharacterTemporaryStat(int bitPos) {
        this.bitPos = bitPos;
    }

    public boolean isMovingEffectingStat() {
        return switch (this) {
            case Speed, Jump, Stun, Weakness, Slow, Morph, Ghost, BasicStatUp, Attract, Flying, Frozen, Mechanic -> true;
            default -> false;
        };
    }

    public boolean isSwallowStat() {
        return switch (this) {
            case SwallowAttackDamage, SwallowCritical, SwallowDefence, SwallowMaxMP, SwallowEvasion -> true;
            default -> false;
        };
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

    public static CharacterTemporaryStat getCtsFromSkillStat(SkillStat skillStat){
        CharacterTemporaryStat cts = null;
        switch (skillStat){
            case morph -> cts = Morph;
            case pad -> cts = Pad;
            case pdd -> cts = Pdd;
            case mad -> cts = Mad;
            case mdd -> cts = Mdd;
            case damR -> cts = DamR;
            case jump -> cts =Jump;
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
