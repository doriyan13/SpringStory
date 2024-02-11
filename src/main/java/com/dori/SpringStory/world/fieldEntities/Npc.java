package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.connection.packet.OutPacket;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

@EqualsAndHashCode(callSuper = true)

public class Npc extends Life{
    // Fields -
    private Map<Integer, String> scripts = new HashMap<>();
    private boolean move = false;

    public Npc(int templateId) {
        super(templateId);
    }

    public Npc(Life life){
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
        this.setHold(life.isHold());
        this.setNoFoothold(life.isNoFoothold());
        this.setDummy(life.isDummy());
        this.setSpine(life.isSpine());
    }

    public void encode(OutPacket outPacket){
        // CNpc::Init
        outPacket.encodePosition(getPosition());
        outPacket.encodeBool(!isFlip()); // m_nMoveAction | actually if the npc should be flipped or not
        outPacket.encodeShort(getFh()); // foothold
        outPacket.encodeShort(getRx0()); // rgHorz.low
        outPacket.encodeShort(getRx1()); // rgHorz.high
        outPacket.encodeBool(!isHide()); // is enabled or not?
    }
}
