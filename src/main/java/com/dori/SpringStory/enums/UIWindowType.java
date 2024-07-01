package com.dori.SpringStory.enums;

import lombok.Getter;

@Getter
public enum UIWindowType {
    UI_ITEM(0x0),
    UI_EQUIP(0x1),
    UI_STAT(0x2),
    UI_SKILL(0x3),
    UI_MINIMAP(0x4),
    UI_KEY_CONFIG(0x5),
    UI_QUEST_INFO(0x6),
    UI_USER_LIST(0x7),
    UI_MESSENGER(0x8),
    UI_MONSTER_BOOK(0x9),
    UI_USERINFO(0x0A),
    UI_SHORTCUT(0x0B),
    UI_MENU(0x0C),
    UI_QUEST_ALARM(0x0D),
    UI_PARTY_HP(0x0E),
    UI_QUEST_TIMER(0x0F),
    UI_QUEST_TIMER_ACTION(0x10),
    UI_MONSTER_CARNIVAL(17),
    UI_ITEM_SEARCH(0x12),
    UI_ENERGY_BAR(0x13),
    UI_GUILD_BOARD(20),
    UI_PARTY_SEARCH(0x15),
    UI_ITEM_MAKE(22),
    UI_CONSULT(0x17),
    UI_CLASS_COMPETITION(24),
    UI_RANKING(0x19),
    UI_FAMILY(0x1A),
    UI_FAMILY_CHART(0x1B),
    UI_OPERATOR_BOARD(0x1C),
    UI_OPERATOR_BOARD_STATE(0x1D),
    UI_MEDAL_QUEST_INFO(0x1E),
    UI_WEB_EVENT(0x1F), // open window with this packet -> RequestWebBoardAuthKey
    UI_SKILL_EX(32), // open the Evan skill UI
    UI_REPAIR_DURABILITY(0x21),
    UI_CHAT_WND(0x22),
    UI_BATTLE_RECORD(35), // this is rlly cool experimental battle record UI (maybe functional)
    UI_GUILD_MAKE_MARK(36),
    UI_GUILD_MAKE(37),
    UI_GUILD_RANK(38),
    UI_GUILD_BBS(39),
    UI_ACCOUNT_MORE_INFO(0x28),
    UI_FIND_FRIEND(0x29),
    UI_DRAGON_BOX(42),
    UI_WND_NO(0x2B),
    UI_UN_RELEASE(0x2C)
    ;
    private final int val;

    UIWindowType(int val) {
        this.val = val;
    }
}
