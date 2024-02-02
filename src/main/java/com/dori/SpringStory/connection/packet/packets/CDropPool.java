package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.enums.DropEnterType;
import com.dori.SpringStory.enums.DropLeaveType;
import com.dori.SpringStory.enums.DropOwnType;
import com.dori.SpringStory.utils.utilEntities.FileTime;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Drop;

import static com.dori.SpringStory.enums.DropEnterType.INSTANT;

public interface CDropPool {
    static OutPacket dropEnterField(Drop drop,
                                    DropEnterType enterType,
                                    DropOwnType dropOwnType,
                                    int srcID,
                                    Position fromPos,
                                    short delay,
                                    boolean canPetPickUp) {
        OutPacket outPacket = new OutPacket(OutHeader.DropEnterField);
        outPacket.encodeByte(enterType.getVal());
        outPacket.encodeInt(drop.getId());
        outPacket.encodeBool(drop.isMoney());
        outPacket.encodeInt(drop.isMoney() ? drop.getQuantity() : drop.getItem().getItemId());
        outPacket.encodeInt(drop.getOwnerID());
        outPacket.encodeByte(dropOwnType.getVal());
        outPacket.encodePosition(drop.getPosition());
        // TODO: in the future need to handle also PARTY_OWN!!!
        outPacket.encodeInt(0); // dropOwnType == DropOwnType.USER_OWN ? srcID : 0
        if (enterType != INSTANT) {
            outPacket.encodePosition(fromPos);
            outPacket.encodeShort(delay); // delay
        }
        if (!drop.isMoney()) {
            FileTime expireTime = FileTime.fromType(FileTime.Type.MAX_TIME);
            outPacket.encodeFT(expireTime); // item expirationTime
        }
        outPacket.encodeBool(canPetPickUp);
        outPacket.encodeBool(false); // idk?

        return outPacket;
    }

    static OutPacket dropLeaveField(DropLeaveType dropLeaveType, int pickupID, int dropID, short delay, int petID) {
        OutPacket outPacket = new OutPacket(OutHeader.DropLeaveField);
        outPacket.encodeByte(dropLeaveType.getVal());
        outPacket.encodeInt(dropID);
        switch (dropLeaveType) {
            case USER_PICKUP, MOB_PICKUP -> outPacket.encodeInt(pickupID);
            case PET_PICKUP -> {
                outPacket.encodeInt(pickupID);
                outPacket.encodeInt(petID);
            }
            case EXPLODE -> outPacket.encodeShort(delay); // pr.p->tLeaveTime = get_update_time() + CInPacket::Decode2(iPacket);
        }
        return outPacket;
    }

}
