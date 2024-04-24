package com.dori.SpringStory.client.effects.parsers;


import com.dori.SpringStory.client.effects.Effect;
import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.enums.UserEffectTypes;
import com.dori.SpringStory.utils.utilEntities.Position;
import lombok.Data;

@Data
public class SkillUseEffect implements Effect {

    private int skillID;
    private int chrLvl;
    private int slv;
    private boolean skillActive;
    private boolean bLeft;
    private int mobOID;
    private Position pos;
    private int nCaptureMsg;

    private static final int DARK_KNIGHT_DARK_FORCE = 1320006;
    private static final int EVAN_DRAGON_FURY = 22160000;
    private static final int ASSASSINATION = 4341005;
    private static final int WILD_HUNTER_CAPTURE = 30001061;
    private static final int WILD_HUNTER_SUMMON_MONSTER = 30001062;

    public SkillUseEffect(int skillID,
                          int chrLvl,
                          int slv) {
        this.skillID = skillID;
        this.chrLvl = chrLvl;
        this.slv = slv;
    }

    public SkillUseEffect(int skillID,
                          int chrLvl,
                          int slv,
                          boolean skillActive) {
        this.skillID = skillID;
        this.chrLvl = chrLvl;
        this.slv = slv;
        this.skillActive = skillActive;
    }

    public SkillUseEffect(int skillID,
                          int chrLvl,
                          int slv,
                          boolean bLeft,
                          int mobOID) {
        this.skillID = skillID;
        this.chrLvl = chrLvl;
        this.slv = slv;
        this.bLeft = bLeft;
        this.mobOID = mobOID;
    }

    public SkillUseEffect(int skillID,
                          int chrLvl,
                          int slv,
                          int nCaptureMsg) {
        this.skillID = skillID;
        this.chrLvl = chrLvl;
        this.slv = slv;
        this.nCaptureMsg = nCaptureMsg;
    }

    public SkillUseEffect(int skillID,
                          int chrLvl,
                          int slv,
                          boolean bLeft,
                          Position pos) {
        this.skillID = skillID;
        this.chrLvl = chrLvl;
        this.slv = slv;
        this.bLeft = bLeft;
        this.pos = pos;
    }

    @Override
    public UserEffectTypes getType() {
        return UserEffectTypes.SkillUse;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(skillID);
        outPacket.encodeByte(chrLvl);
        outPacket.encodeByte(slv);

        switch (skillID) {
            case EVAN_DRAGON_FURY, DARK_KNIGHT_DARK_FORCE -> outPacket.encodeBool(skillActive);
            case ASSASSINATION -> {
                outPacket.encodeBool(bLeft);
                outPacket.encodeInt(mobOID);
            }
            case WILD_HUNTER_CAPTURE -> outPacket.encodeByte(nCaptureMsg);
            case WILD_HUNTER_SUMMON_MONSTER -> {
                outPacket.encodeByte(bLeft);
                outPacket.encodePosition(pos);
            }
        }
    }
}
