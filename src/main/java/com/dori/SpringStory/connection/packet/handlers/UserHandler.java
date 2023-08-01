package com.dori.SpringStory.connection.packet.handlers;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.character.attack.AttackInfo;
import com.dori.SpringStory.connection.packet.Handler;
import com.dori.SpringStory.connection.packet.InPacket;
import com.dori.SpringStory.connection.packet.headers.InHeader;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.connection.packet.packets.CUserRemote;
import com.dori.SpringStory.enums.AttackType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.Portal;
import com.dori.SpringStory.world.fieldEntities.movement.MovementData;

import static com.dori.SpringStory.connection.packet.headers.InHeader.*;
import static com.dori.SpringStory.enums.AttackType.*;

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
            chr.warp(chr.getField(), portal);
        }
        // client has stopped moving. this might not be the best way
        if (chr.getMoveAction() == 4 || chr.getMoveAction() == 5) {
            //TODO: need to handle TSM (Temporary stat manager)
        }
    }

    @Handler(ops = {UserMeleeAttack, UserShootAttack, UserMagicAttack, UserBodyAttack})
    public static void handleUserAttack(MapleClient c, InPacket inPacket, InHeader header) {
        // CUserLocal::TryDoingMeleeAttack -> line 2878
        MapleChar chr = c.getChr();
        AttackInfo ai = new AttackInfo();
        AttackType type = None;
        switch (header) {
            case UserMeleeAttack -> type = Melee;
            case UserShootAttack -> type = Shoot;
            case UserMagicAttack -> type = Magic;
            case UserBodyAttack -> type = Body;
        }
        if (type != None) {
            ai.decode(type, inPacket);
            chr.getField().broadcastPacket(CUserRemote.attack(chr, ai), chr);
            ai.apply(chr);
        }
    }

}
