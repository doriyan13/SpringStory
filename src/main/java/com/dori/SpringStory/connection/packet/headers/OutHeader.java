package com.dori.SpringStory.connection.packet.headers;

import java.util.Arrays;
import java.util.List;

public enum OutHeader {
    // GENERAL
    PING(17),
    // CLogin::OnPacket -
    CheckPasswordResult(0),
    GuestIDLoginResult(1),
    AccountInfoResult(2),
    CheckUserLimitResult(3),
    SetAccountResult(4),
    ConfirmEULAResult(5),
    CheckPinCodeResult(6),
    UpdatePinCodeResult(7),
    ViewAllCharResult(8),
    SelectCharacterByVACResult(9),
    WorldInformation(10),
    SelectWorldResult(11), // CHARLIST
    SelectCharacterResult(12),
    CheckDuplicatedIDResult(13),
    CreateNewCharacterResult(14),
    DeleteCharacterResult(15),
    EnableSPWResult(21),
    LatestConnectedWorld(24),
    RecommendWorldMessage(25),
    ExtraCharInfoResult(26),
    CheckSPWResult(27),
    // CMapLoadable::OnPacket -
    SetBackEffect(144),
    SetMapObjectVisible(145),
    ClearBackEffect(146),
    // CStage::OnPacket -
    SetField(141),
    SetITC(142),
    SetCashShop(143),
    // CNpcPool::OnPacket -
    NpcImitateData(84),
    UpdateLimitedDisableInfo(85),
    NpcEnterField(311),
    NpcLeaveField(312),
    NpcChangeController(313),
    // CMobPool::OnPacket -
    MobEnterField(284),
    MobLeaveField(285),
    MobChangeController(286),
    MobCrcKeyChanged(297),
    // CMobPool::OnMobPacket (CMob) -
    MobMove(287),
    MobCtrlAck(288),
    MobStatSet(290),
    MobStatReset(291),
    MobSuspendReset(292),
    MobAffected(293),
    MobDamaged(294),
    MobSpecialEffectBySkill(295),
    MobHPIndicator(298),
    MobCatchEffect(299),
    MobEffectByItem(300),
    MobMobSpeaking(301),
    IncMobChargeCount(302),
    MobSkillDelay(303),
    MobEscortFullPath(304),
    MobEscortStopEndPermmision(305),
    MobEscortStopSay(306),
    MobEscortReturnBefore(307),
    MobNextAttack(308),
    MobAttackedByMob(309),


    /*CHANGE_CHANNEL(16),
    ALIVE_REQ(17),
    CHANNEL_SELECTED(20),
    RELOG_RESPONSE(22),
    SECONDPW_ERROR(23),
    RECOMMENDED_WORLD_MESSAGE((27)),
    // CHANNEL
    INVENTORY_OPERATION(30),
    INVENTORY_GROW(31),
    UPDATE_STATS(32),
    TEMP_STATS(33),
    TEMP_STATS_RESET(34),
    FORCED_STATS(35),
    FORCED_STATS_RESET(36),
    SKILLS_UPDATE(37),
    SKILLS_USE(38), // 1 byte only - >update time.. same as the byte for update skills after header
    FAME_RESPONSE(39),
    SHOW_STATUS_INFO(40),
    GAME_PATCHES(41),
    SHOW_NOTES(42),
    TROCK_LOCATIONS(43), // WEDDING_PHOTO ?
    LIE_DETECTOR(44),
    UPDATE_MOUNT(49),
    SHOW_QUEST_COMPLETION(50),
    ENTRUSTED_SHOP(51),
    USE_SKILL_BOOK(52),
    FINISH_SORT(53), // Ongatheritemresult
    FINISH_GATHER(54), // onsortitemresult
    BBS_OPERATION(60),
    CHARACTER_INFO(62),
    PARTY_OPERATION(63),
    EXPEDITION_OPERATION(65),
    BUDDYLIST(66),
    GUILD_OPERATION(68),
    ALLIANCE_OPERATION(69),
    PORTAL_TOWN(70),
    PORTAL_GATE(71),
    SERVERMESSAGE(72),
    PIGMI_REWARD(73),
    OWL_OF_MINERVA(74), // Shop Scanner Result
    OWL_OF_MINERVA_RESULT(75), // Shop Link Result
    ENGAGE_REQUEST(76),
    ENGAGE_RESULT(77),
    YELLOW_CHAT(81),
    SHOP_DISCOUNT(82), // BYTE(%), ROUND UP
    CATCH_MESSAGE(83), // 53 00 01 00 00 00 00 00 00 00 00
    PLAYER_NPC_RESULT(84),
    PLAYER_NPC(85),
    PLAYER_NPC_DISABLE_INFO(86), // is this the one to remove the text on the head?
    MONSTERBOOK_ADD(87),
    MONSTERBOOK_CHANGE_COVER(89),
    RESET_MINI_MAP(90),
    ENERGY(94), // OnSessionValue
    GHOST_POINT(95), // OnPartyValue
    GHOST_STATUS(96), // OnFieldSetVariable
    BONUS_EXP_CHANGED(97),
    FAMILY_CHART(99),
    FAMILY_INFO(100),
    FAMILY_MESSAGE(101),
    FAMILY_INVITE(102),
    FAMILY_INVITE_RESULT(103),
    FAMILY_JOIN_ACCEPTED(104),
    FAMILY_PRIVILEDGE_LIST(105),
    FAMILY_REP_INC(106),
    FAMILY_LOGGEDIN(107),
    FAMILY_BUFF(108),
    FAMILY_SUMMON_REQUEST(109),
    LEVEL_UPDATE(110),
    MARRIAGE_UPDATE(111),
    JOB_UPDATE(112),
    AVATAR_MEGA(116),
    PENDANT_SLOT(126), // one byte only , 1 = show, 0 = don't show
    FOLLOW_REQUEST(127),
    TOP_MSG(128),
    MAPLE_ADMIN(129),
    UPDATE_JAGUAR(-2),
    // inventory full stuffs = 131 v97
    VISITOR(135), // seemed that the visitor effect was removed..
    SKILL_MACRO(139),
    WARP_TO_MAP(140), // SetField
    MTS_OPEN(141),
    CS_OPEN(142),
    SET_BACK_EFFECT(143), // byte(0/1) + int + byte(0~5? place) + int
    SET_MAP_OBJECT_VISIBLE(144), // used to change world select image !
    CLEAR_MAP_EFFECT(145),
    MAP_BLOCKED(146),
    SERVER_BLOCKED(147),
    SHOW_EQUIP_EFFECT(148),
    MULTICHAT(149),
    WHISPER(150),
    SPOUSE_CHAT(151),
    SUMMON_ITEM_UNAVAILABLE(152),
    BOSS_ENV(153),
    MOVE_ENV(154),
    UPDATE_ENV(155),
    MAP_EFFECT(157),
    CASH_SONG(158),
    GM_EFFECT(159),
    OX_QUIZ(160),
    GMEVENT_INSTRUCTIONS(161),
    CLOCK(162),
    BOAT_EFF(163), // OnContiMove // not boat, is used for many stuffs.
    BOAT_EFFECT(164), // OnContiState
    STOP_CLOCK(169),
    ARIANT_SCOREBOARD(170),
    PYRAMID_UPDATE(172), // or could it be 171?
    PYRAMID_RESULT(173),
    QUICK_SLOT(174),
    MOVE_PLATFORM(175),
    SPAWN_PLAYER(177),
    REMOVE_PLAYER_FROM_MAP(178),
    CHATTEXT(179),
    CHALKBOARD(181),
    UPDATE_CHAR_BOX(182),
    SHOW_ITEM_UPGRADE_EFFECT(184),
    SHOW_ITEM_HYPER_UPGRADE_EFFECT(185),
    SHOW_ITEM_OPTION_UPGRADE_EFFECT(186),
    SHOW_ITEM_RELEASE_EFFECT(187),
    SHOW_ITEM_UNRELEASE_EFFECT(188),
    FOLLOW_EFFECT(191),
    PAMS_SONG(194),
    SPAWN_PET(195),
    MOVE_PET(198),
    PET_CHAT(199),
    PET_NAMECHANGE(200),
    PET_EXCEPTION_LIST(201),
    PET_COMMAND(202),
    SPAWN_SUMMON(203),
    REMOVE_SUMMON(204),
    MOVE_SUMMON(205),
    SUMMON_ATTACK(206),
    SUMMON_SKILL(207),
    DAMAGE_SUMMON(208),
    DRAGON_SPAWN(209),
    DRAGON_MOVE(210),
    DRAGON_REMOVE(211),
    MOVE_PLAYER(213),
    CLOSE_RANGE_ATTACK(214),
    RANGED_ATTACK(215),
    MAGIC_ATTACK(216),
    ENERGY_ATTACK(217),
    SKILL_EFFECT(218),
    CANCEL_SKILL_EFFECT(219),
    DAMAGE_PLAYER(220),
    FACIAL_EXPRESSION(221),
    SHOW_ITEM_EFFECT(222),
    SHOW_CHAIR(224),
    UPDATE_CHAR_LOOK(225),
    SHOW_FOREIGN_EFFECT(226),
    GIVE_FOREIGN_BUFF(227),
    CANCEL_FOREIGN_BUFF(228),
    UPDATE_PARTYMEMBER_HP(229),
    LOAD_GUILD_NAME(230),
    LOAD_GUILD_ICON(231),
    CANCEL_CHAIR(233),
    SHOW_ITEM_GAIN_INCHAT(235),//CUser::OnEffect
    CURRENT_MAP_WARP(236),
    MESOBAG_SUCCESS(238),
    MESOBAG_FAILURE(239),
    UPDATE_QUEST_INFO(244),
    PLAYER_HINT(247),
    REPAIR_WINDOW(254),
    CYGNUS_INTRO_LOCK(255),
    CYGNUS_INTRO_DISABLE_UI(256),
    SUMMON_HINT(257),
    SUMMON_HINT_MSG(258),
    ARAN_COMBO(259),
    FOLLOW_MESSAGE(266),
    FOLLOW_MOVE(271),
    FOLLOW_MSG(272),
    COOLDOWN(274),
    SPAWN_MONSTER(276),
    KILL_MONSTER(277),
    SPAWN_MONSTER_CONTROL(278),
    MOVE_MONSTER(279),
    MOVE_MONSTER_RESPONSE(280),
    APPLY_MONSTER_STATUS(282),
    CANCEL_MONSTER_STATUS(283),
    DAMAGE_MONSTER(286),
    SHOW_MAGNET(287),
    SHOW_MONSTER_HP(290),
    CATCH_MONSTER(292),
    MONSTER_PROPERTIES(296),
    TALK_MONSTER(298), // OnEscortStopSay
    REMOVE_TALK_MONSTER(299), // OnEscortReturnBefore
    SPAWN_NPC(303),
    REMOVE_NPC(304),
    SPAWN_NPC_REQUEST_CONTROLLER(305),
    NPC_ACTION(306),
    NPC_UPDATED_LIMITED_INFO(307),
    SET_NPC_ACTION(308),
    NPC_SCRIPTABLE(309),
    SPAWN_HIRED_MERCHANT(311),
    DESTROY_HIRED_MERCHANT(312),
    UPDATE_HIRED_MERCHANT(313),
    DROP_ITEM_FROM_MAPOBJECT(314),
    REMOVE_ITEM_FROM_MAP(316),
    SPAWN_MIST(320),
    REMOVE_MIST(321),
    SPAWN_DOOR(322),
    REMOVE_DOOR(323),
    REACTOR_HIT(326),
    REACTOR_SPAWN(328),
    REACTOR_DESTROY(329),
    // CField::SnowBall
    ROLL_SNOWBALL(330), // OnSnowBallState
    HIT_SNOWBALL(331),
    SNOWBALL_MESSAGE(332),
    LEFT_KNOCK_BACK(333), // OnSnowBallTouch
    // CField::Coconut
    HIT_COCONUT(334), // OnCoconutHit
    COCONUT_SCORE(335), // OnCoconutScore
    // CField::GuildBoss
    MOVE_HEALER(336), // header+ one short only
    PULLEY_STATE(337), // header+ byte only
    // CField::MonsterCarnival
    MONSTER_CARNIVAL_START(338),
    MONSTER_CARNIVAL_OBTAINED_CP(339),
    MONSTER_CARNIVAL_PARTY_CP(340),
    MONSTER_CARNIVAL_SUMMON(341), // v7(1 // result stuffs
    MONSTER_CARNIVAL_MESSAGE(342), // v7(0 // result stuffs  (structure header+byte)
    MONSTER_CARNIVAL_DIED(343),
    MONSTER_CARNIVAL_LEAVE(344),
    MONSTER_CARNIVAL_RESULT(345), // structure 1 byte only.
    // CField::AriantArena
    ARIANT_SCORE(346), // OnUserScore
    // CField::Battlefield
    SHEEP_RANCH_INFO(348), // OnScoreUpdate (byte+byte)
    SHEEP_RANCH_CLOTHES(349), // OnTeamChanged (int,charid+byte)

    CHAOS_HORNTAIL_SHRINE(351),
    CHAOS_ZAKUM_SHRINE(352),
    HORNTAIL_SHRINE(353),
    ZAKUM_SHRINE(354),
    NPC_TALK(355),
    OPEN_NPC_SHOP(356),
    CONFIRM_SHOP_TRANSACTION(357),
    OPEN_STORAGE(360),
    MERCH_ITEM_MSG(361),
    MERCH_ITEM_STORE(362),
    RPS_GAME(363),
    MESSENGER(364),
    PLAYER_INTERACTION(365),
    DUEY(367),
    // CField::Wedding
    WEDDING_PROGRESS(371),
    WEDDING_END(372),
    CS_CHARGE_CASH(374),
    CS_UPDATE(375),
    CS_OPERATION(376),
    CS_EXP_PURCHASE(377), // one byte only
    CS_GIFT_RESULT(378),
    CS_NAME_ERROR(379),
    CS_NAME_CHANGE(380),
    CS_GACHAPON_STAMPS(383),
    CS_SURPRISE(384),
    CS_XMAS_SURPRISE(385),
    CS_TWIN_DRAGON_EGG(387),
    GO_TO_CS_BY_SN(268),
    KEYMAP(389),
    PET_AUTO_HP(390),
    PET_AUTO_MP(391),
    LUCKY_LOGOUT_GIFT(193),
    //MAPLE_LIFE(404),
    //MAPLE_LIFE_SUBMIT(405),
    ARIANT_PQ_START(-2),
    GET_MTS_TOKENS(0x999),
    MTS_OPERATION(0x999),
    VICIOUS_HAMMER(412),
    VEGAS_SCROLL(416),
    REPORT_RESPONSE(56),
    ENABLE_REPORT(48),
    CLAIM_SERVER_AVAILABLE_TIME(47),
    BUFFED_ZONE_EFFECT(267)*/;


    private static final List<OutHeader> spam = Arrays.asList(
            MobCtrlAck
    );

    private final short value;

    OutHeader(int value) {
        this.value = (short) value;
    }

    public short getValue() {
        return value;
    }

    public static OutHeader getOutHeaderByOp(int op) {
        for (OutHeader outHeader : OutHeader.values()) {
            if (outHeader.getValue() == op) {
                return outHeader;
            }
        }
        return null;
    }

    public static boolean isSpamHeader(OutHeader outHeader) {
        return spam.contains(outHeader);
    }

}
