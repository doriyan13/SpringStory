package com.dori.Dori90v.connection.packet.handlers;

import com.dori.Dori90v.client.MapleClient;
import com.dori.Dori90v.client.character.MapleChar;
import com.dori.Dori90v.connection.packet.Handler;
import com.dori.Dori90v.connection.packet.InPacket;
import com.dori.Dori90v.connection.packet.packets.CStage;
import com.dori.Dori90v.enums.ServiceType;
import com.dori.Dori90v.logger.Logger;
import com.dori.Dori90v.services.ServiceManager;
import com.dori.Dori90v.world.fieldEntities.Field;

import java.util.Optional;

import static com.dori.Dori90v.connection.packet.headers.InHeader.MigrateIn;

public class StageHandler {
    // Logger -
    private static final Logger logger = new Logger(StageHandler.class);

    @Handler(op = MigrateIn)
    public static void handleMigrateIn(MapleClient c, InPacket inPacket) {
        int playerID = inPacket.decodeInt();
        byte adminClient = inPacket.decodeByte();
        Optional<?> currChar = ServiceManager.getService(ServiceType.Character).getEntityById((long) playerID);
        if(currChar.isPresent() && currChar.get() instanceof MapleChar){
            //TODO: need to add handling to transfer clients and fix my channel not transferring correctly and fix the creation of dori data which is wrong currently..
            c.setChr((MapleChar) currChar.get());
            c.write(CStage.onSetField(c,c.getChr(), (Field) null, (short) 0, (int) c.getChannel(),
                    0,true, (byte) 1, (short) 0,
                    "", new String[]{""}));
        }
        else {
            logger.error("try to logg-in with invalid playerID?" + playerID);
        }
    }
}
