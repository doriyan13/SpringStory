package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.utils.utilEntities.Position;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Life {
    private int objectId = -1;
    private Position position;
    protected int cy, templateId, mobTime, rx0, rx1, type;
    protected short fh;
    protected boolean flip;
    private String lifeType = "";
    private boolean hide;
    private String limitedName = "";
    private boolean useDay;
    private boolean useNight;
    private boolean hold;
    private boolean noFoothold;
    private int regenStart;
    private int mobAliveReq;
    private boolean dummy;
    private boolean spine;
    private boolean mobTimeOnDie;
    private boolean respawnable;
    private byte moveAction;
    private Field field;
    private Position homePosition;
    private Position vPosition;
    private  byte team;

    public Life(int templateId) {
        this.templateId = templateId;
        this.position = new Position(0, 0);
        this.vPosition = new Position(0, 0);
    }

    public Life deepCopy() {
        Life copy = new Life(getTemplateId());
        copy.setObjectId(getObjectId());
        copy.setLifeType(getLifeType());
        copy.setPosition(getPosition());
        copy.setVPosition(getVPosition());
        copy.setMobTime(getMobTime());
        copy.setFlip(isFlip());
        copy.setHide(isHide());
        copy.setFh(getFh());
        copy.setCy(getCy());
        copy.setRx0(getRx0());
        copy.setRx1(getRx1());
        copy.setLimitedName(getLimitedName());
        copy.setUseDay(isUseDay());
        copy.setUseNight(isUseNight());
        copy.setHold(isHold());
        copy.setNoFoothold(isNoFoothold());
        copy.setDummy(isDummy());
        copy.setSpine(isSpine());
        copy.setMobTimeOnDie(isMobTimeOnDie());
        copy.setRegenStart(getRegenStart());
        copy.setMobAliveReq(getMobAliveReq());
        return copy;
    }
}
