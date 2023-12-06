package com.dori.SpringStory.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FuncKeyMappingType {
    KeyModified(0),
    PetConsumeItemModified(1),
    PetConsumeMPItemModified(2)
    ;

    private final int val;

    FuncKeyMappingType(int val) {
        this.val = val;
    }

    public static FuncKeyMappingType getMappingTypeByVal(int val) {
        return Arrays.stream(values())
                .filter(funcKeyMappingType -> funcKeyMappingType.getVal() == val)
                .findFirst()
                .orElse(null);
    }
}
