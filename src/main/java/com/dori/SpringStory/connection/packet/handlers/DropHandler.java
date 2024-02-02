package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.world.fieldEntities.Drop;

import static com.dori.SpringStory.connection.packet.headers.InHeader.DropPickUpRequest;
import static com.dori.SpringStory.connection.packet.headers.InHeader.UserChangeSlotPositionRequest;

public class DropHandler {
    // Logger -
    private static final Logger logger = new Logger(DropHandler.class);

    @Handler(op = DropPickUpRequest)
    public static void handleDropPickUpRequest(MapleClient c, InPacket inPacket) {
        MapleChar chr = c.getChr();
        inPacket.decodeByte(); // fieldKey
        inPacket.decodeInt(); // tick
        inPacket.decodePosition(); // drop position
        int dropID = inPacket.decodeInt();
        inPacket.decodeInt(); // CliCrc

        Drop drop = chr.getField().getDrops().get(dropID);
        if (drop != null) {
            chr.pickupItem(drop);
        }
    }
}
