package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.client.character.KeyMapping;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.utils.FuncKeyMapUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.IntStream;

public interface CFuncKeyMappedMan {

    static @NotNull OutPacket funcKeyMappedManInit(Map<Integer, KeyMapping> funcKeyMap) {
        OutPacket outPacket = new OutPacket(OutHeader.FunkKeyMappedManInit);
        outPacket.encodeBool(false);
        FuncKeyMapUtils.encodeChrKeyMapping(funcKeyMap, outPacket);

        return outPacket;
    }
}
