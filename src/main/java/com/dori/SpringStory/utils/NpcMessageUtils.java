package com.dori.SpringStory.utils;

@SuppressWarnings("unused")
public interface NpcMessageUtils {
    static String blue(String input) {
        return "#b" + input + "#k";
    }

    static String red(String input) {
        return "#r" + input + "#k";
    }

    static String green(String input) {
        return "#g" + input + "#k";
    }

    static String purple(String input) {
        return "#d" + input + "#k";
    }

    static String black(String input) {
        return "#k" + input + "#k";
    }

    static String bold(String input) {
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

    static String itemDetails(int input) {
        return "#z" + input + "#";
    }

    static String wzImage(String input) {
        return "#f" + input + "#";
    }

    static String getPlayerName() {
        return "#h #";
    }

    static String mapName(int input) {
        return "#m" + input + "#";
    }

    static String mobName(int input) {
        return "#o" + input + "#";
    }

    static String npcName(int input) {
        return "#p" + input + "#";
    }

    static String skillName(int input) {
        return "#q" + input + "#";
    }

    static String skillImage(int input) {
        return "#s" + input + "#";
    }

    static String toProgressBar(int input) {
        return "#B" + input + "#";
    }

    static String menuLine(int index, String val) {
        return "\\r\\n#L" + index + '#' + val + "#l";
    }
}
