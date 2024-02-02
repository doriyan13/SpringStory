package com.dori.SpringStory.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AccountType {
    Player(0, 0),
    Tester(1, 0x100),
    Intern(2, 0),
    GameMaster(3, 0x40),
    Admin(5, 0x80);

    /*
     All the Sub GradeCodes:
        PrimaryTrace(0x1),
        SecondaryTrace(0x2),
        AdminClient(0x4),
        MobMoveObserve(0x8),
        ManagerAccount(0x10),
        OutSourceSuperGM(0x20),
        OutSourceGM(0x40),
        UserGM(0x80),
        TesterAccount(0x100),
    */

    private final int lvl;
    private final byte subGrade;

    AccountType(int lvl, int subGrade) {
        this.lvl = lvl;
        this.subGrade = (byte) subGrade;
    }

    public static AccountType getTypeByLvl(int lvl){
        return Arrays.stream(values()).filter(accountType -> accountType.getLvl() == lvl).findFirst().orElse(Player);
    }
}
