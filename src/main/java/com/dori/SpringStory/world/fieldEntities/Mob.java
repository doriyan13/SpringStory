package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.MobSummonType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

@EqualsAndHashCode(callSuper = true)
public class Mob extends Life{
    // Fields -
    private int hp;
    private int maxHp;
    private int mp;
    private int maxMp;
    private int exp;
    private short homeFh;
    private MobSummonType appearType;
    private int option;
    private byte teamForMCarnival;

    public Mob(int templateId) {
        super(templateId);

        this.homeFh = 0;
        this.appearType = MobSummonType.Normal;
        this.option = 0;
        this.teamForMCarnival = 0;
    }

    public Mob(Life life){
        this.setTemplateId(life.getTemplateId());
        this.setObjectId(life.getObjectId());
        this.setLifeType(life.getLifeType());
        this.setPosition(life.getPosition());
        this.setMobTime(life.getMobTime());
        this.setFlip(life.isFlip());
        this.setHide(life.isHide());
        this.setFh(life.getFh());
        this.setCy(life.getCy());
        this.setRx0(life.getRx0());
        this.setRx1(life.getRx1());
        this.setLimitedName(life.getLimitedName());
        this.setUseDay(life.isUseDay());
        this.setUseNight(life.isUseNight());
        this.setHold(life.isHold());
        this.setNoFoothold(life.isNoFoothold());
        this.setDummy(life.isDummy());
        this.setSpine(life.isSpine());
        this.setMobTimeOnDie(life.isMobTimeOnDie());
        this.setRegenStart(life.getRegenStart());
        this.setMobAliveReq(life.getMobAliveReq());
        // Additional Default mob data -
        this.setMoveAction((byte) 5); // Need to figure out what is it?
        this.homeFh = 0;
        this.appearType = MobSummonType.Normal;
        this.option = 0;
        this.teamForMCarnival = 0;
    }

    public void encode(OutPacket outPacket){
        //CMob::SetTemporaryStat
        //Temp stats - TODO: need to handle it properly!
        outPacket.encodeArr(new byte[16]);

        //CMob::Init
        outPacket.encodePosition(getPosition()); //m_ptPosPrev.x | m_ptPosPrev.y
        outPacket.encodeByte(getMoveAction());
        outPacket.encodeShort(getFh()); //  m_nFootholdSN
        outPacket.encodeShort(getHomeFh()); //  m_nHomeFoothold
        outPacket.encodeByte(appearType.getVal());

        if(appearType == MobSummonType.Revived || appearType.getVal() >= 0){
            outPacket.encodeInt(option); // summon option
        }
        outPacket.encodeByte(getTeamForMCarnival());
        outPacket.encodeInt(0); // nEffectItemID
        outPacket.encodeInt(0); // this
    }
}
