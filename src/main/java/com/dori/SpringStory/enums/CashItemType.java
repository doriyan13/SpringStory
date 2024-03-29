package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum CashItemType {
    NONE(0),
    HAIR(1),
    FACE(2),
    SKIN(3),
    SHOP(4),
    SET_PET_LIFE(5),
    EMOTION(6),
    PROTECT_ON_DIE(7),
    PET(8),
    EFFECT(9),
    BULLET(10),
    SHOP_EMPLOYEE(11),
    SPEAKER_CHANNEL(12),
    SPEAKER_WORLD(13),
    ITEM_SPEAKER(14),
    SPEAKER_BRIDGE(15),
    WEATHER(16),
    SET_PET_NAME(17),
    MESSAGEBOX(18),
    MONEY_POCKET(19),
    JUKEBOX(20),
    SEND_MEMO(21),
    MAP_TRANSFER(22),
    STAT_CHANGE(23),
    SKILL_CHANGE(24),
    NAMING(25),
    PROTECTING(26),
    INCUBATOR(27),
    PET_SKILL(28),
    SHOP_SCANNER(29),
    PET_FOOD(30),
    QUICK_DELIVERY(31),
    AD_BOARD(32),
    CONSUME_EFFECT_ITEM(33),
    CONSUME_ARE_A_BUFF_ITEM(34),
    COLOR_LENS(35),
    WEDDING_TICKET(36),
    INVITATION_TICKET(37),
    SELECT_NPC(38),
    REMOTE_SHOP(39),
    GACHAPON_COUPON(40),
    MORPH(41),
    PET_EVOL(42),
    AVATAR_MEGAPHONE(43),
    HEART_SPEAKER(44),
    SKULL_SPEAKER(45),
    REMOVABLE(46),
    MAPLE_TV(47),
    MAPLE_SOLE_TV(48),
    MAPLE_LOVE_TV(49),
    MEGA_TV(50),
    MEGA_SOLE_TV(51),
    MEGA_LOVE_TV(52),
    CHANGE_CHARACTER_NAME(53),
    TRANSFER_WORLD_COUPON(54),
    HAIR_SHOP_MEMBERSHIP_COUPON(55),
    FACE_SHOP_MEMBERSHIP_COUPON(56),
    SKIN_SHOP_MEMBERSHIP_COUPON(57),
    PET_SNACK(58),
    GACHAPON_BOX_MASTER_KEY(59),
    GACHAPON_REMOTE(60),
    ART_SPEAKER_WORLD(61),
    EXTEND_EXPIRE_DATE(62),
    UPGRADE_TOMB(63), // wheel of destiny
    KARMA_SCISSORS(64),
    EXPIRED_PROTECTING(65),
    CHARACTER_SALE(66),
    ITEM_UPGRADE(67),
    CASH_ITEM_GACHAPON(68),
    CASH_GACHAPON_OPEN(69),
    CHANGE_MAPLE_POINT(70),
    VEGA(71),
    REWARD(72),
    MASTERY_BOOK(73),
    CUBE_REVEAL(74),
    SKILL_RESET(75),
    DRAGON_BALL(76),
    RECOVER_UPGRADE_COUNT(77),
    QUEST_DELIVERY(78)
    ;
    private final int val;

    CashItemType (int val) {
        this.val = val;
    }

    public static CashItemType getCashItemTypeByID(int nItemID) {
        return switch (nItemID / 10_000) {
            case 500 -> CashItemType.PET;
            case 501 -> CashItemType.EFFECT;
            case 502 -> CashItemType.BULLET;
            case 503 -> CashItemType.SHOP_EMPLOYEE;
            case 504 -> CashItemType.MAP_TRANSFER;
            case 505 -> nItemID == 5050000 ? CashItemType.STAT_CHANGE : CashItemType.SKILL_CHANGE;
            case 506 -> {
                if (nItemID >= 5062000) {
                    yield CashItemType.CUBE_REVEAL;
                }
                if (nItemID >= 5061000) {
                    yield CashItemType.EXPIRED_PROTECTING;
                }
                yield switch (nItemID % 10) {
                    case 0 -> CashItemType.NAMING;
                    case 1 -> CashItemType.PROTECTING;
                    case 2, 3 -> CashItemType.INCUBATOR;
                    default -> CashItemType.NONE;
                };
            }
            case 507 -> switch (nItemID % 10_000 / 1_000) {
                case 1 -> CashItemType.SPEAKER_CHANNEL;
                case 2 -> CashItemType.SPEAKER_WORLD;
                case 4 -> CashItemType.SKULL_SPEAKER;
                case 5 -> switch (nItemID % 10) {
                    case 0 -> CashItemType.MAPLE_TV;
                    case 1 -> CashItemType.MAPLE_SOLE_TV;
                    case 2 -> CashItemType.MAPLE_LOVE_TV;
                    case 3 -> CashItemType.MEGA_TV;
                    case 4 -> CashItemType.MEGA_SOLE_TV;
                    case 5 -> CashItemType.MEGA_LOVE_TV;
                    default -> CashItemType.NONE;
                };
                case 6 -> CashItemType.ITEM_SPEAKER;
                case 7 -> CashItemType.ART_SPEAKER_WORLD;
                case 8 -> CashItemType.SPEAKER_BRIDGE; // surrogate for ItemSpeaker w/o item
                default -> CashItemType.NONE;
            };
            case 508 -> CashItemType.MESSAGEBOX;
            case 509 -> CashItemType.SEND_MEMO;
            case 510 -> CashItemType.JUKEBOX;
            case 512 -> CashItemType.WEATHER;
            case 513 -> CashItemType.PROTECT_ON_DIE;
            case 514 -> CashItemType.SHOP;
            case 515 -> {
                if (nItemID >= 5150000 && nItemID < 5152000) { // 0 & 1
                    yield CashItemType.HAIR;
                }
                if (nItemID >= 5152000 && nItemID < 5152100) { // 2
                    yield CashItemType.FACE;
                }
                if (nItemID >= 5152100 && nItemID < 5153000) { // 2.5
                    yield CashItemType.COLOR_LENS;
                }
                if (nItemID >= 5153000 && nItemID < 5154000) { // 3
                    yield CashItemType.SKIN;
                }
                if (nItemID >= 5154000 && nItemID < 5155000) { // 4
                    yield CashItemType.HAIR;
                }
                yield CashItemType.NONE;
            }
            case 516 -> CashItemType.EMOTION;
            case 517 -> CashItemType.SET_PET_NAME;
            case 518 -> CashItemType.SET_PET_LIFE;
            case 519 -> CashItemType.PET_SKILL;
            case 520 -> CashItemType.MONEY_POCKET; // meso bag
            case 522 -> CashItemType.GACHAPON_COUPON;
            case 523 -> CashItemType.SHOP_SCANNER;
            case 524 -> CashItemType.PET_FOOD;
            case 525 -> {
                if (nItemID >= 5251000 && nItemID < 5252000) {
                    yield CashItemType.WEDDING_TICKET;
                }
                yield CashItemType.INVITATION_TICKET;
            }
            case 528 -> {
                if (nItemID >= 5280000 && nItemID < 5281000) {
                    yield CashItemType.CONSUME_EFFECT_ITEM;
                }
                yield CashItemType.CONSUME_ARE_A_BUFF_ITEM;
            }
            case 530 -> CashItemType.MORPH;
            case 533 -> CashItemType.QUICK_DELIVERY;
            case 537 -> CashItemType.AD_BOARD;
            case 538 -> CashItemType.PET_EVOL;
            case 539 -> CashItemType.AVATAR_MEGAPHONE;
            case 540 -> {
                if (nItemID == 5400000) {
                    yield CashItemType.CHANGE_CHARACTER_NAME;
                }
                if (nItemID == 5401000) {
                    yield CashItemType.TRANSFER_WORLD_COUPON;
                }
                yield CashItemType.NONE;
            }
            case 542 -> CashItemType.HAIR_SHOP_MEMBERSHIP_COUPON;
            case 543 -> CashItemType.CHARACTER_SALE;
            case 545 -> {
                if (nItemID == 5450000) {
                    yield CashItemType.SELECT_NPC;
                }
                if (nItemID == 5451000) {
                    yield CashItemType.GACHAPON_REMOTE;
                }
                yield CashItemType.NONE;
            }
            case 546 -> CashItemType.PET_SNACK;
            case 547 -> CashItemType.REMOTE_SHOP;
            case 549 -> CashItemType.GACHAPON_BOX_MASTER_KEY;
            case 550 -> CashItemType.EXTEND_EXPIRE_DATE;
            case 551 -> CashItemType.UPGRADE_TOMB;
            case 552 -> CashItemType.KARMA_SCISSORS;
            case 553 -> CashItemType.REWARD;
            case 557 -> CashItemType.ITEM_UPGRADE;
            case 561 -> CashItemType.VEGA;
            case 562 -> CashItemType.MASTERY_BOOK;
            case 564 -> CashItemType.RECOVER_UPGRADE_COUNT;
            case 566 -> CashItemType.QUEST_DELIVERY;

            default -> CashItemType.NONE;
        };
    }
}
