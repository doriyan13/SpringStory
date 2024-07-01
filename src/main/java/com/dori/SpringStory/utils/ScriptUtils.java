package com.dori.SpringStory.utils;

import java.io.File;

public interface ScriptUtils {

    static String getClassName(File file) {
        return file.getPath()
                .replaceAll("[\\\\|/]", ".")
                .split("src\\.main\\.java\\.")[1]
                .replaceAll("\\.java", "");
    }

    static String getQuestScriptExtension(boolean start) {
        return start ? "s" : "e";
    }
}
