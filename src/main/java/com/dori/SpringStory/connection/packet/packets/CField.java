package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CField {

    static @NotNull OutPacket adminResult(int cmdType,
                                          boolean hidden) {
        OutPacket outPacket = new OutPacket(OutHeader.AdminResult);
        outPacket.encodeByte(cmdType);
        switch (cmdType) {
            case 4, 5 -> {
                // 4 - "You have successfully blocked access"
                // 5 - "The unblocking has been successful"
                outPacket.encodeByte(0); // idk what the value required?
            }
            case 6 -> {
                // removing chr from ranking response
                boolean failedToNameFromRanks = true;
                outPacket.encodeBool(failedToNameFromRanks);
            }
            case 11 -> {
                // display some kind of red message: "[channel name: world name] = msg"
                outPacket.encodeString("channel name");
                outPacket.encodeString("world name");
                outPacket.encodeString("msg");
            }
            case 0x12 -> { //hide
                outPacket.encodeByte(hidden); // m_RefCount
            }
            case 21 -> {
                boolean check = false;
                outPacket.encodeBool(false);
                if(check) {
                    // Hired Merchant licated at: <CH.1>
                    outPacket.encodeByte(0); // seperate from if <= 253 | else - msg?
                } else {
                    // Hired Merchant located at: <Map>
                    outPacket.encodeInt(100000000); // mapID?
                    outPacket.encodeByte(0); // idk?
                }
            }
            case 40, 41 -> {} // 40 - reload of miniMap | 41 - disable minimap
            case 42 -> {
                // idk? seems to do nothing?
                outPacket.encodeBool(true);
            }
            case 43 -> {
                // Warning response - true for successful warning sent, false for failed cuz user name is wrong
                boolean successfullySentWarningToUser = false;
                outPacket.encodeBool(successfullySentWarningToUser);
            }
            case 51,52,53,54,55,56,57 -> {
                // Write chat msg to everyone as a admin color (black & white)
                outPacket.encodeString("idk");
            }
            case 58 -> {
                // notice red msg to chat (i think all users get it)
                outPacket.encodeString("idk what the purpose?");
            }
            case 71 -> {
                // notice red msg to chat (i think all users get it)
                outPacket.encodeString("hello hello");
            }
        }
        return outPacket;
    }

    static OutPacket quickSlotMappedInit(List<Integer> quickSlotKeyMap) {
        OutPacket outPacket = new OutPacket(OutHeader.QuickSlotMappedInit);
        outPacket.encodeByte(true); // defaults if false
        for (int key : quickSlotKeyMap) {
            outPacket.encodeInt(key);
        }
        return outPacket;
    }
}
