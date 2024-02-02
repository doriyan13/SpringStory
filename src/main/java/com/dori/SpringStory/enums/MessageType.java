package com.dori.SpringStory.enums;

public enum MessageType {
    DROP_PICK_UP_MESSAGE(0x00),
    QUEST_RECORD_MESSAGE(0x01),
    CASH_ITEM_EXPIRE_MESSAGE(0x02),
    INC_EXP_MESSAGE(0x03),
    INC_SP_MESSAGE(0x04),
    INC_FAME_MESSAGE(0x05),
    INC_MESO_MESSAGE(0x06),
    INC_GP_MESSAGE(0x07),
    GIVE_BUFF_MESSAGE(0x08),
    GENERAL_ITEM_EXPIRE_MESSAGE(0x09),
    SYSTEM_MESSAGE(0x0A),
    QUEST_RECORD_EX_MESSAGE(0x0B),
    ITEM_PROTECT_EXPIRE_MESSAGE(0x0C),
    ITEM_EXPIRE_REPLACE_MESSAGE(0x0D),
    SKILL_EXPIRE_MESSAGE(0x0E)
    ;

    private final byte val;

    MessageType(int val) {
        this.val = (byte) val;
    }

    public byte getVal() {
        return val;
    }
}
