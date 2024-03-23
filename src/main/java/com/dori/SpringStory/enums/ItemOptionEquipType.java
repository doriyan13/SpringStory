package com.dori.SpringStory.enums;

import com.dori.SpringStory.utils.MapleUtils;
import lombok.Getter;

import java.util.Arrays;
@Getter
public enum ItemOptionEquipType {
    AnyEquip(0),
    Weapon(10),
    AnyExceptWeapon(11),
    Armor(20),
    Accessory(40),
    Hat(51),
    Top(52),
    Bottom(53),
    Glove(54),
    Shoes(55);

    private final int val;

    ItemOptionEquipType(int val) {
        this.val = val;
    }

    public static ItemOptionEquipType getByVal(int val) {
        return MapleUtils.findWithPred(Arrays.asList(values()), stat -> stat.getVal() == val);
    }
}
