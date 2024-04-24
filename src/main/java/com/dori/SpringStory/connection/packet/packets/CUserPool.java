package com.dori.SpringStory.connection.packet.packets;


import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.enums.FieldType;
import com.dori.SpringStory.logger.Logger;

public interface CUserPool {
    // Logger -
    Logger logger = new Logger(CUserPool.class);

    static OutPacket userEnterField(MapleChar chr) {
        OutPacket outPacket = new OutPacket(OutHeader.UserEnterField);

        outPacket.encodeInt(chr.getId());
        outPacket.encodeByte(chr.getLevel());
        outPacket.encodeString(chr.getName());
        // Guild Encoding -
        outPacket.encodeString(""); // GuildName
        outPacket.encodeShort(0); // GuildMarkBg
        outPacket.encodeByte(0); // GuildMarkBgColor
        outPacket.encodeShort(0); // GuildMark
        outPacket.encodeByte(0); // GuildMarkColor

        chr.getTsm().encodeForRemote(outPacket);

        outPacket.encodeShort(chr.getJob());
        chr.encodeAvatarLook(outPacket);

        outPacket.encodeInt(0); // dwDriverID
        outPacket.encodeInt(0); // dwPassenserID
        outPacket.encodeInt(0); // nChocoCount
        outPacket.encodeInt(0); // nActiveEffectItemID
        outPacket.encodeInt(0); // nCompletedSetItemID
        outPacket.encodeInt(0); // nPortableChairID

        outPacket.encodePosition(chr.getPosition());
        outPacket.encodeByte(chr.getMoveAction());
        outPacket.encodeShort(chr.getFoothold());

        outPacket.encodeByte(false); // bShowAdminEffect
        // TODO: handle pets properly -
//        for (Pet pet : user.getPets()) {
//            if (pet == null) {
//                continue;
//            }
//            outPacket.encodeByte(true);
//            pet.encode(outPacket); // CPet::Init
//        }
        outPacket.encodeByte(0);

        outPacket.encodeInt(0); // nTamingMobLevel
        outPacket.encodeInt(0); // nTamingMobExp
        outPacket.encodeInt(0); // nTamingMobFatigue
        // TODO: handle mini room type -
        outPacket.encodeByte(0); // nMiniRoomType -> p.Encode1((byte)(c.CurMiniRoom?.nMiniRoomType ?? 0))
        //TODO: handle ADBoard properly -
        outPacket.encodeBool(false); // bADBoardRemote
        if (false) { // c.sADBoard.Length > 0
            outPacket.encodeString(""); // c.sADBoard
        }
        //TODO: handle rings properly -
        outPacket.encodeBool(false); // coupleItem
        outPacket.encodeBool(false); // friendShipItem
        outPacket.encodeBool(false); // marriageRecord
        // TODO: handle those effects properly -
        /*
            if (c.Skills.bDarkForce) nActiveUserEffect |= 1;
			if (c.Skills.nDragonFury != 0) nActiveUserEffect |= 2;
			if (c.m_dwSwallowMobID > 0) nActiveUserEffect |= 4;
         */
        outPacket.encodeByte(0); // CUser::DarkForceEffect | CDragon::CreateEffect | CUser::LoadSwallowingEffect | nActiveUserEffect

        outPacket.encodeBool(false); // bool -> int * int (CUserPool::OnNewYearCardRecordAdd)
        outPacket.encodeInt(0); // nPhase

        // field -> DecodeFieldSpecificData
        FieldType fieldType = chr.getField().getFieldType();
        if (fieldType == FieldType.BATTLE_FIELD || fieldType == FieldType.COCONUT) {
            // CField_BattleField::DecodeFieldSpecificData, CField_Coconut::DecodeFieldSpecificData
            outPacket.encodeByte(0); // nTeam
        } else if (fieldType == FieldType.MONSTER_CARNIVAL_S2 || fieldType == FieldType.MONSTER_CARNIVAL_REVIVE) {
            // CField_MonsterCarnival::DecodeFieldSpecificData,  CField_MonsterCarnivalRevive::DecodeFieldSpecificData
            outPacket.encodeByte(0); // nTeamForMCarnival
        }
        return outPacket;
    }

    static OutPacket userLeaveField(MapleChar chr) {
        OutPacket outPacket = new OutPacket(OutHeader.UserLeaveField);
        outPacket.encodeInt(chr.getId()); // dwCharacterId
        return outPacket;
    }
}
