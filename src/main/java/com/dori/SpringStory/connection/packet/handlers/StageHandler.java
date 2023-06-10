package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.Server;
import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.headers.InHeader;
import com.dori.SpringStory.connection.packet.packets.CStage;
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
        byte adminClient = inPacket.decodeByte();
        Optional<?> entity = ServiceManager.getService(ServiceType.Character).getEntityById((long) playerID);
        if (entity.isPresent() && entity.get() instanceof MapleChar chr) { // init the chr instance cast inline
            chr.setMapleClient(c);
            // Handle adding a new user online -
            Server.addNewOnlineUser(chr, c);
            // Set the field for the character to spawn in -
            c.write(CStage.onSetField(c.getChr(), (Field) null, (short) 0, (int) c.getChannel(),
                    0, true, (byte) 1, (short) 0,
                    "", new String[]{""}));
            Field field = c.getMapleChannelInstance().getField(chr.getMapId());
            if (field != null){
                Portal currPortal = field.getPortalByName("sp");
                // Set char position in field -
                c.getChr().setPosition(new Position(currPortal.getPosition().getX(), currPortal.getPosition().getY()));
                // Add player to the field -
                field.addPlayer(chr);
                // Ref the field straight to the player (easier management)
                chr.setField(field);
                // Spawn lifes for the client -
                field.spawnLifesForCharacter(chr);
                // Assign Controllers For life -
                field.assignControllerToMobs(chr);
                //TODO: need to handle controller for npcs!!
            }
            else {
                logger.error("got un-valid mapID for a char that cause a null field!, closing session for: " + chr.getName());
                c.close();
            }
        } else {
            logger.error("try to logg-in with invalid playerID?" + playerID);
        }
    }

    @Handler(op = InHeader.UserTransferFieldRequest)
    public static void handleUserTransferFieldRequest(MapleClient c, InPacket inPacket){
        if (inPacket.getUnreadAmount() == 0) {
            // Coming back from the cash shop
            logger.warning("currently not handled!");
            c.close();
        }
        byte fieldKey = inPacket.decodeByte(); // 1 = from dying 0 = regular portals
        int targetField = inPacket.decodeInt();
        String portalName = inPacket.decodeString();
        MapleChar chr = c.getChr();
        Portal targetPortal = chr.getField().getPortalByName(portalName);
        if(portalName != null && !portalName.isEmpty() && targetPortal != null){
            Field field = c.getMapleChannelInstance().getField(targetPortal.getTargetMapId());
            if(field != null){
                Position position = inPacket.decodePosition(); // short, short
                Portal portal = field.getPortalByName(targetPortal.getTargetPortalName());
                logger.debug("portal position: " + portal.getPosition() + ", pos we get from client: " + position);
                byte townPortal = inPacket.decodeByte();
                boolean premium = inPacket.decodeBool();
                byte chase = inPacket.decodeByte();
                // Update the field and chr instances -
                chr.warp(chr.getField(),field, portal);
                // Set the field for the character to spawn in -
                c.write(CStage.onSetField(c.getChr(), field, (short) 0, (int) c.getChannel(),
                        0, false, (byte) 1, (short) 0,
                        "", new String[]{""}));
                // Spawn lifes for the client -
                field.spawnLifesForCharacter(chr);
                // Assign Controllers For life -
                field.assignControllerToMobs(chr);
            }
            else {
                logger.error("Got an invalid field ID while trying to transfer between maps - " + targetPortal.getTargetMapId());
                c.close();
            }
        }
        else {
            logger.error("Got an invalid portal name while trying to transfer between maps - " + portalName);
            c.close();
        }
    }
}
