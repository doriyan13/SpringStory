package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.enums.InventoryType;
import com.dori.SpringStory.logger.Logger;

import static com.dori.SpringStory.connection.packet.headers.InHeader.UserChangeSlotPositionRequest;
import static com.dori.SpringStory.enums.InventoryType.EQUIP;
import static com.dori.SpringStory.enums.InventoryType.EQUIPPED;

public class InventoryHandler {
    // Logger -
    private static final Logger logger = new Logger(InventoryHandler.class);

    @Handler(op = UserChangeSlotPositionRequest)
    public static void handleUserChangeSlotPositionRequest(MapleClient c, InPacket inPacket) {

        int updateTime = inPacket.decodeInt();
        InventoryType invType = InventoryType.getInventoryByVal(inPacket.decodeByte());
        short oldPos = inPacket.decodeShort();
        short newPos = inPacket.decodeShort();
        short quantity = inPacket.decodeShort();

        MapleChar chr = c.getChr();
        InventoryType invTypeFrom = invType == EQUIP ? oldPos < 0 ? EQUIPPED : EQUIP : invType;
        InventoryType invTypeTo = invType == EQUIP ? newPos < 0 ? EQUIPPED : EQUIP : invType;
        //Item item = chr.getInventoryByType(invTypeFrom).getItemBySlot(oldPos);
    }
}
