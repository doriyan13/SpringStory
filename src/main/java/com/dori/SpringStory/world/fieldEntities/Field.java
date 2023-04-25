package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.wzHandlers.wzEntities.MapData;
import lombok.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.dori.SpringStory.constants.GameConstants.DEFAULT_FIELD_MOB_CAPACITY;
import static com.dori.SpringStory.constants.GameConstants.DEFAULT_FIELD_MOB_RATE_BY_MOBGEN_COUNT;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class Field extends MapData {
    // Fields -
    private Map<Integer, MapleChar> players;

    public Field(int id){
        super(id);
    }

    public Field(MapData mapData){
        // TODO: maybe the id management will change in the future!
        this.id = mapData.getId();
        this.fieldType = mapData.getFieldType();
        this.town = mapData.isTown();
        this.swim = mapData.isSwim();
        this.fly = mapData.isFly();
        this.fieldLimit = mapData.getFieldLimit();
        this.returnMap = mapData.getReturnMap();
        this.forcedReturn = mapData.getForcedReturn();
        this.mobRate = mapData.getMobRate();
        this.onFirstUserEnter = mapData.getOnFirstUserEnter();
        this.onUserEnter = mapData.getOnUserEnter();
        this.fieldScript = mapData.getFieldScript();
        this.reactorShuffle = mapData.isReactorShuffle();
        this.partyOnly = mapData.isPartyOnly();
        this.expeditionOnly = mapData.isExpeditionOnly();
        this.needSkillForFly = mapData.isNeedSkillForFly();
        this.fixedMobCapacity = mapData.getFixedMobCapacity() != 0 ? mapData.getFixedMobCapacity() : DEFAULT_FIELD_MOB_CAPACITY;
        this.fixedMobCapacity = mapData.getFixedMobCapacity() != 0 ? mapData.getFixedMobCapacity() : (int) DEFAULT_FIELD_MOB_RATE_BY_MOBGEN_COUNT;
        this.createMobInterval = mapData.getCreateMobInterval();
        this.timeOut = mapData.getTimeOut();
        this.timeLimit = mapData.getTimeLimit();
        this.consumeItemCoolTime = mapData.getConsumeItemCoolTime();
        this.link = mapData.getLink();
        this.setVrTop(mapData.getVrTop());
        this.setVrLeft(mapData.getVrLeft());
        this.setVrBottom(mapData.getVrBottom());
        this.setVrRight(mapData.getVrRight());
        for (Foothold fh : mapData.getFootholds()) {
            this.addFoothold(fh.deepCopy());
        }
        for (Portal p : mapData.getPortals()) {
            this.addPortal(p.deepCopy());
        }
        for (Life l : mapData.getLifes().values()) {
            this.addLife(l.deepCopy());
        }
        this.players = new HashMap<>();
        this.dropsDisabled = mapData.isDropsDisabled();
    }

    public Portal getPortalByName(String name) {
        return MapleUtils.findWithPred(getPortals(), portal -> portal.getName().equals(name));
    }

    public void addPlayer(MapleChar chr){
        players.put(chr.getId(), chr);
    }
}
