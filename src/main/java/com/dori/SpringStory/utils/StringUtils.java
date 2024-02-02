package com.dori.SpringStory.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface StringUtils {

    static boolean isSupportedFormatString(String stringToCheck){
        for(char letter: stringToCheck.toCharArray()){
            if(!StandardCharsets.US_ASCII.newEncoder().canEncode(letter)){
                return false;
            }
        };
        return true;
    }
}
