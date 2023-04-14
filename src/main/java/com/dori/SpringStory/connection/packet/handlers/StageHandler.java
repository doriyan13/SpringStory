package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.Server;
import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CStage;
import com.dori.SpringStory.enums.ServiceType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.services.ServiceManager;
import com.dori.SpringStory.world.fieldEntities.Field;

import java.util.Optional;

import static com.dori.SpringStory.connection.packet.headers.InHeader.MigrateIn;

public class StageHandler {
    // Logger -
    private static final Logger logger = new Logger(StageHandler.class);

    @Handler(op = MigrateIn)
    public static void handleMigrateIn(MapleClient c, InPacket inPacket) {
        int playerID = inPacket.decodeInt();
        byte adminClient = inPacket.decodeByte();
        Optional<?> entity = ServiceManager.getService(ServiceType.Character).getEntityById((long) playerID);
        if (entity.isPresent() && entity.get() instanceof MapleChar chr) { // init the chr instance cast inline
            chr.setMapleClient(c);
            // Handle adding a new user online -
            Server.addNewOnlineUser(chr, c);
            // Set the field for the character to spawn in -
            c.write(CStage.onSetField(c, c.getChr(), (Field) null, (short) 0, (int) c.getChannel(),
                    0, true, (byte) 1, (short) 0,
                    "", new String[]{""}));
        } else {
            logger.error("try to logg-in with invalid playerID?" + playerID);
        }
    }
}
