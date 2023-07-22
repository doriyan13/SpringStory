package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.utils.utilEntities.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Reactor extends Life {
    private byte state;
    private String name = "";
    private int ownerID;
    private int properEventIdx;
    private int reactorTime;
    private int hitCount;

    public Reactor(int templateId) {
        super(templateId);
    }

    public Life deepCopy() {
        Reactor copy = new Reactor(getTemplateId());
        copy.setLifeType(getLifeType());
        copy.setPosition(getPosition());
        copy.setMobTime(getMobTime());
        copy.setFlip(isFlip());
        copy.setLimitedName(getLimitedName());
        copy.setPosition(getPosition().deepCopy());
        copy.setHomePosition(getPosition().deepCopy());
        return copy;
    }

    public void dropDrops() {
        int fhID = getFh();
        if (fhID == 0) {
            Position pos = getPosition();
            pos.setY(pos.getY());
            Foothold fhBelow = getField().findFootHoldBelow(pos);
            if (fhBelow != null) {
                fhID = fhBelow.getId();
            }
        }
        //TODO: need to handle DropInfo stuff related to Reactors -
//        Set<DropInfo> dropInfoSet = ReactorData.getReactorInfoByID(getTemplateId()).getDrops();
//        getField().drop(dropInfoSet, getField().getFootholdById(fhID), getPosition(), ownerID, 100,
//                100);
    }
}
