package com.dori.SpringStory.utils;

import com.dori.SpringStory.client.character.KeyMapping;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.OutPacket;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Component
public interface FuncKeyMapUtils {

    int MIN_KEY_NUM = 0;
    int MAX_KEY_NUM = 89;

    static void addKeyMappingToChr(MapleChar chr, KeyMapping keyMapping) {
        if (chr.getKeymap().containsKey(keyMapping.getKey())) {
            KeyMapping currKeyMapping = chr.getKeymap().get(keyMapping.getKey());
            currKeyMapping.setType(keyMapping.getType());
            currKeyMapping.setAction(keyMapping.getAction());
        } else {
            chr.getKeymap().put(keyMapping.getKey(), keyMapping);
        }
    }

    static void removeKeyMappingToChr(MapleChar chr, int key) {
        chr.getKeymap().remove(key);
    }

    static void handleKeyModifiedToChr(InPacket inPacket, MapleChar chr) {
        IntStream.range(0, inPacket.decodeInt()).forEach(i -> {
            int key = inPacket.decodeInt();
            if (key >= MIN_KEY_NUM && key <= MAX_KEY_NUM) {
                byte type = inPacket.decodeByte();
                int action = inPacket.decodeInt();
                if (type != 0) {
                    KeyMapping keyMapping = new KeyMapping(key, type, action);
                    addKeyMappingToChr(chr, keyMapping);
                } else {
                    removeKeyMappingToChr(chr, key);
                }
            }
        });
    }

    static void encodeChrKeyMapping(Map<Integer, KeyMapping> keymap, OutPacket outPacket) {
        for (int i = 0; i <= MAX_KEY_NUM; i++) {
            if (keymap.containsKey(i)) {
                keymap.get(i).encode(outPacket);
            } else {
                outPacket.encodeByte(0);
                outPacket.encodeInt(0);
            }
        }
    }

    static Map<Integer, KeyMapping> getDefaultKeyMapping() {
        Map<Integer, KeyMapping> keymap = new HashMap<>();
        int[] array1 = new int[]{2, 3, 35, 4, 36, 5, 37, 6, 38, 7, 8, 41, 43, 44, 13, 45, 46, 16, 17, 18, 21, 23, 57, 26};
        int[] array2 = new int[]{6, 6, 4, 6, 4, 6, 4, 6, 4, 6, 6, 4, 4, 5, 4, 5, 5, 4, 4, 4, 5, 4, 5, 4};
        int[] array3 = new int[]{100, 101, 2, 102, 9, 103, 3, 104, 7, 105, 106, 23, 28, 50, 4, 51, 52, 8, 5, 0, 54, 1, 53, 15};
        IntStream.range(0, array1.length).forEach(i -> {
            KeyMapping keyMapping = new KeyMapping(array1[i], (byte) array2[i], array3[i]);
            keymap.put(keyMapping.getKey(), keyMapping);
        });
        return keymap;
    }
}
