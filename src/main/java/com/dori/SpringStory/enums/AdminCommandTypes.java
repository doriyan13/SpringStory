package com.dori.SpringStory.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AdminCommandTypes {
    NONE(-1),
    CREATE_ITEM(0x0), // /create <arg1> | encodeInt(itemID)
    DELETE_ITEM(0x1), // /d <arg1> | encodeByte(inventory type i think?)
    GAIN_EXP(0x2), // /exp <arg1> | encodeInt (exp amount)
    BAN(0x3), // /ban | encode additional String that i think is char name
    BLOCK(0x4), // /block (not sure??) | encodeString(chr name), encodeByte(reason), encodeInteger(duration in days), encodeString(HACK/BOT/AD/HARASS/CURSE/SCAM/MISCONDUCT/SELL/ICASH/TEMP/GM/IPROGRAM/MEGAPHONE)
    WARN(0x2B), // /w <> | String(),String()
    IDK_3(0x11), // idk? maybe /varget? byte(12),String(),String(),String() | byte(13),String()
    HIDE(0x12), // /h <arg1>
    GET_USERS_IN_FIELD(0x13), // /u | have extra byte encoded as 0
    U_CLIP(0x13), // /uclip | have extra byte encoded as 0xF
    KILL(0x17), // /kill | String(),String()
    QUEST_RESET(0x19), // /questreset | short(maybe questID?)
    QUEST(0x1B), // /quest int,int
    SUMMON(0x1C), // /summon | int(mobID), int(amount of mobs to spawn)
    MOB_HP(0x1D), // /mobhp | int(mobID)
    SET_JOB(0x1F), // /job <arg1> | int(jobID) | LINE_2092
    ADD_AP(0x21),  // /apget <arg1>
    ADD_SP(0x22), // /spget <arg1>
    SET_STR(0x23), // /str <arg1>
    SET_DEX(0x24), // /dex <arg1>
    SET_INT(0x25), // /int <arg1>
    SET_LUK(0x26), // /luk <arg1>
    SKILL(0x27), // /skill int,int
    ARTIFACT_RANKING(0x31), // /ArtifactRanking
    PQ_ANSWER(0x3A), // /pqanswer
    SET_LVL(0x1E), // /levelset | byte(desired lvl)
    PARTY_CHECK(0x47), // /partycheck <String> | have extra string encode
    // Passive skill data -
    PSD_VIEW(0x3C), // /psdview | byte()
    PSD_SET(0x3D), // /psdset | String(),int()
    PSD_UPDATE(0x3E), // /psdupdate
    GET_COOLDOWN(0x3F), // /getcooltime | int()
    // '/m' -> <MapID> transfer you to another map | it calls UserTransferFieldRequest packet!
    ;

    private final int cmdFlag;

    AdminCommandTypes(int cmdFlag) {
        this.cmdFlag = cmdFlag;
    }

    public static AdminCommandTypes getCommandByFlag(int cmdFlag) {
        return Arrays.stream(AdminCommandTypes.values())
                .filter(adminCommand -> adminCommand.getCmdFlag() == cmdFlag)
                .findFirst()
                .orElse(NONE);
    }
}
