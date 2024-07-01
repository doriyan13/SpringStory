package com.dori.SpringStory.dataHandlers.wzData;

import lombok.Getter;

@Getter
public enum TagVarEnum {
    VT_EMPTY(0x0000),
    VT_NULL(0x0001),
    VT_I2(0x0002),
    VT_I4(0x0003),
    VT_R4(0x0004),
    VT_R8(0x0005),
    VT_CY(0x0006),
    VT_DATE(0x0007),
    VT_BSTR(0x0008),
    VT_DISPATCH(0x0009),
    VT_ERROR(0x000A),
    VT_BOOL(0x000B),
    VT_VARIANT(0x000C),
    VT_UNKNOWN(0x000D),
    VT_DECIMAL(0x000E),
    VT_I1(0x0010),
    VT_UI1(0x0011),
    VT_UI2(0x0012),
    VT_UI4(0x0013),
    VT_I8(0x0014),
    VT_UI8(0x0015),
    VT_INT(0x0016),
    VT_UINT(0x0017),
    VT_VOID(0x0018),
    VT_HRESULT(0x0019),
    VT_PTR(0x001A),
    VT_SAFEARRAY(0x001B),
    VT_CARRAY(0x001C),
    VT_USERDEFINED(0x001D),
    VT_LPSTR(0x001E),
    VT_LPWSTR(0x001F),
    VT_RECORD(0x0024),
    VT_INT_PTR(0x0025),
    VT_UINT_PTR(0x0026),
    VT_ARRAY(0x2000),
    VT_BYREF(0x4000)
    ;
    private final int val;

    TagVarEnum(int val) {
        this.val = val;
    }

    public static TagVarEnum getByValue(byte value) {
        return switch (value) {
            case 0x0000 -> VT_EMPTY;
            case 0x0001 -> VT_NULL;
            case 0x0002 -> VT_I2;
            case 0x0003 -> VT_I4;
            case 0x0004 -> VT_R4;
            case 0x0005 -> VT_R8;
            case 0x0006 -> VT_CY;
            case 0x0007 -> VT_DATE;
            case 0x0008 -> VT_BSTR;
            case 0x0009 -> VT_DISPATCH;
            case 0x000A -> VT_ERROR;
            case 0x000B -> VT_BOOL;
            case 0x000C -> VT_VARIANT;
            case 0x000D -> VT_UNKNOWN;
            case 0x000E -> VT_DECIMAL;
            case 0x0010 -> VT_I1;
            case 0x0011 -> VT_UI1;
            case 0x0012 -> VT_UI2;
            case 0x0013 -> VT_UI4;
            case 0x0014 -> VT_I8;
            case 0x0015 -> VT_UI8;
            case 0x0016 -> VT_INT;
            case 0x0017 -> VT_UINT;
            case 0x0018 -> VT_VOID;
            case 0x0019 -> VT_HRESULT;
            case 0x001A -> VT_PTR;
            case 0x001B -> VT_SAFEARRAY;
            case 0x001C -> VT_CARRAY;
            case 0x001D -> VT_USERDEFINED;
            case 0x001E -> VT_LPSTR;
            case 0x001F -> VT_LPWSTR;
            case 0x0024 -> VT_RECORD;
            case 0x0025 -> VT_INT_PTR;
            case 0x0026 -> VT_UINT_PTR;
            default -> throw new IllegalArgumentException("Unknown value: " + value);
        };
    }
}
