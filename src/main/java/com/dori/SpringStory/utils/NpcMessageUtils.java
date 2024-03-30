package com.dori.SpringStory.utils;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface NpcMessageUtils {
    static String blue(@NotNull String input) {
        return "#b" + input + "#k";
    }

    static String red(@NotNull String input) {
        return "#r" + input + "#k";
    }

    static String green(@NotNull String input) {
        return "#g" + input + "#k";
    }

    static String purple(@NotNull String input) {
        return "#d" + input + "#k";
    }

    static String black(@NotNull String input) {
        return "#k" + input + "#k";
    }

    static String bold(@NotNull String input) {
        return "#e" + input + "#n";
    }

    static String blue(Number input) {
        return "#b" + input + "#k";
    }

    static String red(Number input) {
        return "#r" + input + "#k";
    }

    static String green(Number input) {
        return "#g" + input + "#k";
    }

    static String purple(Number input) {
        return "#d" + input + "#k";
    }

    static String black(Number input) {
        return "#k" + input + "#k";
    }

    static String bold(Number input) {
        return "#e" + input + "#n";
    }

    static String itemName(int input) {
        return "#t" + input + "#";
    }

    static String itemCount(int input) {
        return "#c" + input + "#";
    }

    static String itemImage(int input) {
        return "#i" + input + "#";
    }

    static String itemImage(long input) {
        return "#i" + input + "#";
    }

    static String itemDetails(int input) {
        return "#z" + input + "#";
    }

    static String wzImage(@NotNull String input) {
        return "#f" + input + "#";
    }

    static String getPlayerName() {
        return "#h #";
    }

    static String mapName(int input) {
        return "#m" + input + "#";
    }

    static String mapName(long input) {
        return "#m" + input + "#";
    }

    static String mobName(int input) {
        return "#o" + input + "#";
    }

    static String mobName(long input) {
        return "#o" + input + "#";
    }

    static String npcName(int input) {
        return "#p" + input + "#";
    }

    static String npcName(long input) {
        return "#p" + input + "#";
    }

    static String skillName(int input) {
        return "#q" + input + "#";
    }

    static String skillImage(int input) {
        return "#s" + input + "#";
    }

    static String skillImage(long input) {
        return "#s" + input + "#";
    }

    static String toProgressBar(int input) {
        return "#B" + input + "#";
    }

    static String menuLine(int index, @NotNull String val) {
        return "\r\n#L" + index + '#' + val + "#l";
    }

    static String newLine(@NotNull String val) {
        return "\r\n" + val;
    }
}
