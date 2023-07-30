package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.packets.CUserRemote;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.Portal;
import com.dori.SpringStory.world.fieldEntities.movement.MovementData;

import static com.dori.SpringStory.connection.packet.headers.InHeader.UserMove;

public class UserHandler {
    // Logger -
    private static final Logger logger = new Logger(UserHandler.class);

    @Handler(op = UserMove)
    public static void handleUserMove(MapleClient c, InPacket inPacket) {
        // CVecCtrlUser::EndUpdateActive
        MapleChar chr = c.getChr();
        Field field = chr.getField();

        inPacket.decodeInt(); // dr0
        inPacket.decodeInt(); // dr1
        byte fieldKey = inPacket.decodeByte(); // Field Key
        inPacket.decodeInt(); // dr2
        inPacket.decodeInt(); // dr3
        inPacket.decodeInt(); // CRC
        inPacket.decodeInt(); // dwKey
        inPacket.decodeInt(); // CRC32

        // CMovePath::Flush -> CMovePath::Encode (line 85)
        MovementData movementInfo = new MovementData(inPacket);
        movementInfo.applyTo(chr);

        //TODO: need to handle char inAffectedArea -
        //field.checkCharInAffectedAreas(chr);

        // Handle sending player move to other players -
        field.broadcastPacket(CUserRemote.move(chr, movementInfo), chr);
        // Fail-safe when the char falls outside the map
        if (chr.getPosition().getY() > 5000) {
            Portal portal = field.findDefaultPortal();
            chr.warp(chr.getField(),portal);
        }
        // client has stopped moving. this might not be the best way
        if (chr.getMoveAction() == 4 || chr.getMoveAction() == 5) {
            //TODO: need to handle TSM (Temporary stat manager)
        }
    }

}
