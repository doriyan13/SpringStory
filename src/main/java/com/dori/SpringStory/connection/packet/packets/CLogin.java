package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.Server;
import com.dori.SpringStory.client.character.MapleAccount;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.enums.WorldStatus;
import com.dori.SpringStory.world.MapleChannel;
import com.dori.SpringStory.world.MapleWorld;
import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.enums.AccountType;
import com.dori.SpringStory.enums.LoginType;
import com.dori.SpringStory.enums.PICType;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.utils.utilEntities.Tuple;

import java.util.Set;

import static com.dori.SpringStory.connection.packet.headers.OutHeader.*;

public interface CLogin {

    static OutPacket sendConnect(byte[] siv, byte[] riv) {
        OutPacket outPacket = new OutPacket();
        outPacket.encodeShort((short) 14); // hand-shake packet size
        outPacket.encodeShort(ServerConstants.VERSION);
        outPacket.encodeString(ServerConstants.MINOR_VERSION);
        outPacket.encodeArr(riv); // IV is an int
        outPacket.encodeArr(siv); // IV is an int
        outPacket.encodeByte(ServerConstants.LOCALE); // 7 = MSEA, 8 = GlobalMS, 5 = Test Server
        return outPacket;
    }

    static OutPacket sendAliveReq() {
        return new OutPacket(PING);
    }

    static OutPacket checkPasswordResult(boolean success, LoginType msg, MapleAccount account) {
        OutPacket outPacket = new OutPacket(CheckPasswordResult);
        outPacket.encodeByte(msg.getValue()); // LoginType
        outPacket.encodeByte(0); // sMsg + 500, 0 or 1 decodes a bunch of shit
        outPacket.encodeInt(0); // nUseDay
        if (success) {
            outPacket.encodeInt(account.getId()); // user id
            outPacket.encodeByte(account.getGender()); // gender
            outPacket.encodeBool(account.getAccountType().getLvl() >= AccountType.GameMaster.getLvl()); // nGradeCode
            outPacket.encodeShort(account.getAccountType().getSubGrade());
            // v118 will only be 1 if nSubGradeCode is 0x100 -
            outPacket.encodeBool(false); // nCountryID, admin accounts? / hasCensoredNxLoginID
            outPacket.encodeString(account.getName()); // sNexonClubID -> account name / getCensoredNxLoginID

            outPacket.encodeByte(0); // nPurchaseExp -> gacha exp | dwHighDateTime
            outPacket.encodeBool(false/*account.getChatUnblockDate() != null*/); // is quiet banned -> chat being blocked for that char, nChatBlockReason
            outPacket.encodeFT(account.getChatUnblockDate()); // dtChatUnblockDate -> block usage of chat until that time!
            outPacket.encodeFT(account.getCreationDate()); // creation time, dtRegisterDate
            outPacket.encodeInt(account.getAccountSlots());  // nNumOfCharacter? don't know why there is need for 4 there?
            outPacket.encodeBool(true); // pin ->PICType.Disabled.getVal()
            outPacket.encodeByte(0); // sMsg._m_pStr[432] = CInPacket::Decode1(iPacket);
            outPacket.encodeLong(0L); // dwHighDateTime
        } else if (msg == LoginType.Blocked) {
            outPacket.encodeByte(msg.getValue());
            outPacket.encodeByte(0);
            outPacket.encodeInt(0);
            outPacket.encodeByte(0); // nReason
            outPacket.encodeFT(account.getBanExpireDate());
        }

        return outPacket;
    }

    static OutPacket changeWorldSelectBackgroundImg() {
        //TODO: need to fix -
        OutPacket outPacket = new OutPacket(SetMapObjectVisible);
        // Number of backgrounds -
        outPacket.encodeByte(ServerConstants.WORLD_SELECT_BACKGROUND_IMAGES.length);

        for (String image : ServerConstants.WORLD_SELECT_BACKGROUND_IMAGES) {
            outPacket.encodeString(image); // name of the background
            outPacket.encodeBool(image.equalsIgnoreCase("visitors")); // not sure? maybe visible?
        }

        return outPacket;
    }

    static OutPacket sendWorldInformation(MapleWorld world, Set<Tuple<Position, String>> worldSelectMessages) {
        // CLogin::OnWorldInformation
        OutPacket outPacket = new OutPacket(OutHeader.WorldInformation);
        outPacket.encodeByte(world.getWorldID());
        outPacket.encodeString(world.getName());
        outPacket.encodeByte(world.getWorldState().getValue()); // effect the right side of the world | 0 - nothing, 1 - EVENT, 2 - New symbol, 3- H symbol
        outPacket.encodeString(world.getWorldEventDescription());
        outPacket.encodeShort(world.getWorldEventEXP_WSE());
        outPacket.encodeShort(world.getWorldEventDrop_WSE());
        outPacket.encodeBool(world.isCharCreationBlocked());
        outPacket.encodeByte(world.getMapleChannels().size());
        for (MapleChannel c : world.getChannelList()) {
            outPacket.encodeString(c.getName());
            outPacket.encodeInt(c.getGaugePx());
            outPacket.encodeByte(world.getWorldID());
            outPacket.encodeByte(c.getChannelId()); //was in v90 a short, it seems to be a byte in v92
            outPacket.encodeBool(c.isAdultChannel()); // is Adult channel (default is false)
        }
        // I think this in-charge of making messages on the world select -
        outPacket.encodeShort(worldSelectMessages.size());
        for (Tuple<Position, String> currMsg : worldSelectMessages) {
            outPacket.encodePosition(currMsg.getLeft()); // message position for example - 0,280
            outPacket.encodeString(currMsg.getRight()); // the message content.
        }

        return outPacket;
    }

    static OutPacket sendWorldInformationEnd() {
        OutPacket outPacket = new OutPacket(OutHeader.WorldInformation);
        outPacket.encodeByte(0xFF); // 255 == 0xFF, not sure why i put an int before, seems to be a byte

        return outPacket;
    }

    static OutPacket sendLatestConnectedWorld(int nWorldID) {
        OutPacket outPacket = new OutPacket(LatestConnectedWorld);
        outPacket.encodeInt(nWorldID);

        return outPacket;
    }

    static OutPacket sendRecommendWorldMessage(int nWorldID, String nMsg) {
        OutPacket outPacket = new OutPacket(OutHeader.RecommendWorldMessage);
        outPacket.encodeByte(1); // amount of worlds, worlds.size()
        // for ( each world) -
        outPacket.encodeInt(nWorldID);
        outPacket.encodeString(nMsg);

        return outPacket;
    }

    static OutPacket getCheckUserLimit(int worldID) {
        OutPacket outPacket = new OutPacket(CheckUserLimitResult);
        // Getting The current world instance -
        MapleWorld world = Server.getWorlds().stream()
                .filter(mapleWorld -> mapleWorld.getWorldID() == worldID)
                .findFirst()
                .orElse(null);
        // World status -
        // TODO: maybe add extra handling to display world full (not able to enter the world!)
        outPacket.encodeByte((world != null && !world.isFull()) ? world.getStatus().getValue() : WorldStatus.BUSY.getValue()); // bOverUserLimit ->
        outPacket.encodeByte(0); // bPopulateLevel

        return outPacket;
    }

    static OutPacket selectWorldResult(MapleAccount account, byte code) {
        OutPacket outPacket = new OutPacket(SelectWorldResult);
        outPacket.encodeByte(code);
        outPacket.encodeByte(account.getCharacters() != null ? account.getCharacters().size() : 0);
        // Encode each character -
        for (MapleChar mapleChar : account.getCharacters()) {
            mapleChar.encode(outPacket);
        }
        outPacket.encodeByte(PICType.Disabled.getVal()); // second pw request encode (unregistered | registered | disabled)
        outPacket.encodeInt(account.getAccountSlots());// amount of Slots for characters -> nSlotCount
        outPacket.encodeInt(0); // amount -> nBuyCharCount

        return outPacket;
    }

    static OutPacket charNameResponse(String charName, boolean isUsed) {
        OutPacket outPacket = new OutPacket(CheckDuplicatedIDResult);
        outPacket.encodeString(charName);
        outPacket.encodeBool(isUsed);

        return outPacket;
    }

    static OutPacket createNewCharacterResult(LoginType type, MapleChar chr) {
        OutPacket outPacket = new OutPacket(CreateNewCharacterResult);

        outPacket.encodeByte(type.getValue());
        if (type == LoginType.Success) {
            chr.encode(outPacket);
        }
        return outPacket;
    }

    static OutPacket onSelectCharacterResult(byte[] machineID, int port, int characterID) {
        OutPacket outPacket = new OutPacket(SelectCharacterResult);
        outPacket.encodeByte(0); // World
        outPacket.encodeByte(0); // dwCharacterID | Selected Char
        outPacket.encodeArr(machineID);
        outPacket.encodeShort(port);
        outPacket.encodeInt(characterID);
        outPacket.encodeByte(0); // bAuthenCode
        outPacket.encodeInt(0); // m_ulPremiumArgument
        return outPacket;
    }
}
