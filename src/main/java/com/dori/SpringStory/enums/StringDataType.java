package com.dori.SpringStory.enums;

import java.util.Arrays;

public enum StringDataType {
    None,
    Item,
    Skill,
    Mob,
    Npc,
    Map
    ;

    public static StringDataType findTypeByName(String name){
        return Arrays.stream(StringDataType.values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(None);
    }
}
