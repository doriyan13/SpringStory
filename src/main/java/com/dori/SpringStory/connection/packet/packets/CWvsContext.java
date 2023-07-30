package com.dori.SpringStory.connection.packet.packets;

import com.dori.SpringStory.client.character.ExtendSP;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.connection.packet.handlers.StageHandler;
import com.dori.SpringStory.connection.packet.headers.OutHeader;
import com.dori.SpringStory.enums.InventoryOperation;
import com.dori.SpringStory.enums.InventoryType;
import com.dori.SpringStory.enums.Stat;
import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.inventory.Item;
import com.dori.SpringStory.logger.Logger;

import java.util.*;

import static com.dori.SpringStory.enums.InventoryType.EQUIPPED;

public interface CWvsContext {
    // Logger -
    Logger logger = new Logger(CWvsContext.class);

    static OutPacket inventoryOperation(boolean exclRequestSent, InventoryOperation type, short oldPos, short newPos,
                                        Item item) {
        InventoryType invType = item.getInvType();
        if ((oldPos > 0 && newPos < 0 && invType == EQUIPPED) || (invType == EQUIPPED && oldPos < 0)) {
            invType = InventoryType.EQUIP;
        }
        OutPacket outPacket = new OutPacket(OutHeader.InventoryOperation);
        outPacket.encodeBool(exclRequestSent);
        outPacket.encodeByte(1); // size
        // For each operation - (tho I see it always 1 at a time...)
        outPacket.encodeByte(type.getVal());
        outPacket.encodeByte(invType.getVal());
        outPacket.encodeShort(oldPos);
        // Handling for the diff operations -
        switch (type) {
            case Add -> item.encode(outPacket);
            case UpdateQuantity -> outPacket.encodeShort(item.getQuantity());
            case Move -> outPacket.encodeShort(newPos);
            case Remove -> {/*Do nothing O.o*/}
            case ItemExp -> outPacket.encodeLong(((Equip) item).getExp());
        }
        // Related to the case if you drop an equip straight to the field -
        outPacket.encodeBool(!(oldPos >= 0)); // bSN == bStat
        return outPacket;
    }

    static OutPacket statChanged(Map<Stat, Object> stats, boolean exclRequestSent, byte charm,
                                 int hpRecovery, int mpRecovery) {
        OutPacket outPacket = new OutPacket(OutHeader.StatChanged);

        outPacket.encodeByte(exclRequestSent); // enableActions
        // GW_CharacterStat::DecodeChangeStat
        int mask = 0;
        for (Stat stat : stats.keySet()) {
            mask |= stat.getVal();
        }
        outPacket.encodeInt(mask);
        // Sort the Stats by their mask val -
        List<Map.Entry<Stat, Object>> sortedListOfStats = new ArrayList<>(stats.entrySet());
        sortedListOfStats.sort(Comparator.comparingInt(stat -> stat.getKey().getVal()));
        // Encode Stats -
        sortedListOfStats.forEach(stat -> {
            try {
                Integer statValue = 0;
                if (stat.getKey() != Stat.SkillPoint) {
                    statValue = (Integer) stat.getValue();
                }
                switch (stat.getKey()) {
                    case Skin, Level -> outPacket.encodeByte(statValue.byteValue());
                    case Face, Hair, Hp, MaxHp, Mp, MaxMp, Exp, Money -> outPacket.encodeInt(statValue);
                    case SubJob, Str, Dex, Inte, Luk, AbilityPoint, Pop -> outPacket.encodeShort(statValue.shortValue());
                    case SkillPoint -> {
                        if (stat.getValue() instanceof ExtendSP) {
                            ((ExtendSP) stat.getValue()).encode(outPacket);
                        } else {
                            outPacket.encodeShort(statValue.shortValue());
                        }
                    }
                    case Pet, Pet2, Pet3 -> outPacket.encodeLong(((Integer) stat.getValue()).longValue());
                    case TempExp -> logger.warning("Attempt to change TempExp, which isn't implemented!");
                }
            } catch (Exception e) {
                logger.error("error occurred!");
                e.printStackTrace();
            }
        });
        // Encode Charm -
        boolean isCharm = charm > 0;
        outPacket.encodeBool(isCharm);
        if (isCharm) {
            outPacket.encodeByte(charm);
        }
        // Encode Recovery -
        boolean isRecovery = hpRecovery > 0 && mpRecovery > 0;
        outPacket.encodeBool(isRecovery);
        if (isRecovery) {
            outPacket.encodeInt(hpRecovery);
            outPacket.encodeInt(mpRecovery);
        }
        return outPacket;
    }
}
