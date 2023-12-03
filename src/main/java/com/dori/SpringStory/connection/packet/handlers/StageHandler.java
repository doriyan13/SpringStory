package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.Server;
import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.headers.InHeader;
import com.dori.SpringStory.enums.ServiceType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.services.ServiceManager;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.Portal;

import java.util.Optional;

import static com.dori.SpringStory.connection.packet.headers.InHeader.MigrateIn;

public class StageHandler {
    // Logger -
    private static final Logger logger = new Logger(StageHandler.class);

    @Handler(op = MigrateIn)
    public static void handleMigrateIn(MapleClient c, InPacket inPacket) {
        int playerID = inPacket.decodeInt();
        inPacket.decodeByte(); // adminClient
        Optional<?> entity = ServiceManager.getService(ServiceType.Character).getEntityById((long) playerID);
        if (entity.isPresent() && entity.get() instanceof MapleChar chr) { // init the chr instance cast inline
            chr.setMapleClient(c);
            // Init the player passive stats -
            chr.initPassiveStats();
            // Handle adding a new user online -
            Server.addNewOnlineUser(chr, c);
            // Set the field for the character to spawn in -
            Field field = c.getMapleChannelInstance().getField(chr.getMapId());
            if (field != null) {
                Portal currPortal = field.getPortalByName("sp");
                // Set char position in field -
                c.getChr().setPosition(new Position(currPortal.getPosition().getX(), currPortal.getPosition().getY()));
                // Add player to the field -
                field.spawnPlayer(chr, true);
                //TODO: need to handle controller for npcs!!
            } else {
                logger.error("got un-valid mapID for a char that cause a null field!, closing session for: " + chr.getName());
                c.close();
            }
        } else {
            logger.error("try to logg-in with invalid playerID?" + playerID);
        }
    }

    @Handler(op = InHeader.UserTransferFieldRequest)
    public static void handleUserTransferFieldRequest(MapleClient c, InPacket inPacket) {
        if (inPacket.getUnreadAmount() == 0) {
            // Coming back from the cash shop
            logger.warning("currently not handled!");
            c.close();
        }
        inPacket.decodeByte(); // fieldKey | 1 = from dying 0 = regular portals
        inPacket.decodeInt(); // targetField
        String portalName = inPacket.decodeString();
        MapleChar chr = c.getChr();
        Portal currentPortal = chr.getField().getPortalByName(portalName);
        // Handle Death respawn -
        if (currentPortal == null && chr.getHp() == 0) {
            currentPortal = chr.getField().findDefaultPortal();
            chr.fullHeal();
        }
        if (currentPortal != null) {
            Field field = c.getMapleChannelInstance().getField(currentPortal.getTargetMapId());
            // this will be the case for death and respawn request -
            if (currentPortal.getTargetMapId() == 999999999) {
                chr.warp(chr.getField(), currentPortal);
            } else if (field != null) {
                inPacket.decodePosition(); // position | short, short
                inPacket.decodeByte(); // townPortal
                inPacket.decodeBool(); // premium
                inPacket.decodeByte(); // chase
                // Update the field and chr instances & warp -
                Portal targetPortal = field.getPortalByName(currentPortal.getTargetPortalName());
                chr.warp(field, targetPortal != null ? targetPortal : chr.getField().findDefaultPortal());
            } else {
                logger.error("Got an invalid field ID while trying to transfer between maps - " + currentPortal.getTargetMapId());
                c.close();
            }
        } else {
            logger.error("Got an invalid portal name while trying to transfer between maps - " + portalName);
            c.close();
        }
    }

    @Handler(op = InHeader.UserPortalScriptRequest)
    public static void handleUserPortalScriptRequest(MapleClient c, InPacket inPacket) {
        inPacket.decodeByte(); // Current Field key
        String portalName = inPacket.decodeString();
        Position position = inPacket.decodePosition();
        //TODO: add portal script handler and such..
    }
}
