package com.dori.SpringStory.client.messages;

import com.dori.SpringStory.connection.packet.OutPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IncEXPMessage {
    private boolean isLastHit;
    private boolean onQuest;
    private int incEXP;
    private int selectedMobBonusExp;
    private int weddingBonusExp;
    private int partyBonusExp;
    private int itemBonusExp;
    private int premiumIPBonusExp;
    private int rainbowWeekEventBonusExp;
    private int partyExpRingExp;
    private int cakePieEventBonus;
    private byte mobEventBonusPercentage;
    private byte partyBonusPercentage;
    private byte playTimeHour;
    private byte questBonusRate;
    private byte questBonusRemainCount;
    private byte partyBonusEventRate;

    public void encode(OutPacket outPacket){
        outPacket.encodeBool(isLastHit);
        outPacket.encodeInt(incEXP);
        outPacket.encodeBool(onQuest);
        outPacket.encodeInt(selectedMobBonusExp);
        outPacket.encodeByte(mobEventBonusPercentage);
        outPacket.encodeByte(partyBonusPercentage);
        outPacket.encodeInt(weddingBonusExp);
        if(mobEventBonusPercentage > 0){
            outPacket.encodeByte(playTimeHour);
        }
        if(onQuest){
            outPacket.encodeByte(questBonusRate);
            if(questBonusRate > 0){
                outPacket.encodeByte(questBonusRemainCount);
            }
        }
        outPacket.encodeByte(partyBonusEventRate);
        outPacket.encodeInt(partyBonusExp);
        outPacket.encodeInt(itemBonusExp);
        outPacket.encodeInt(premiumIPBonusExp);
        outPacket.encodeInt(rainbowWeekEventBonusExp);
        outPacket.encodeInt(partyExpRingExp);
        outPacket.encodeInt(cakePieEventBonus);
    }

}
