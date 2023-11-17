package com.dori.SpringStory.dataHandlers.dataEntities;

import com.dori.SpringStory.world.fieldEntities.Foothold;
import com.dori.SpringStory.world.fieldEntities.Life;
import com.dori.SpringStory.world.fieldEntities.Portal;
import com.dori.SpringStory.constants.GameConstants;
import com.dori.SpringStory.enums.FieldType;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapData {
    protected int id;
    protected int vrTop, vrLeft, vrBottom, vrRight;
    protected float mobRate;
    protected FieldType fieldType;
    protected Set<Portal> portals = new HashSet<>();
    protected Set<Foothold> footholds = new HashSet<>();
    protected Set<Life> lifes = new HashSet<>();
    protected String onFirstUserEnter = "", onUserEnter = "";
    protected int fixedMobCapacity;
    protected long fieldLimit;
    protected int returnMap, forcedReturn, createMobInterval, timeOut, timeLimit, lvLimit, lvForceMove;
    protected int consumeItemCoolTime, link;
    protected boolean town, swim, fly, reactorShuffle, expeditionOnly, partyOnly, needSkillForFly;
    protected Map<Integer, List<String>> directionInfo = new HashMap<>();
    protected boolean dropsDisabled;
    protected String fieldScript = "";
    protected boolean everLast;
    protected boolean personalShop;
    protected int decHP;
    protected int decInterval;
    protected int protectItem;
    protected float recovery;
    protected float dropRate;


    public MapData(int fieldID) {
        this.id = fieldID;
        this.portals = new HashSet<>();
        this.footholds = new HashSet<>();
        this.lifes = new HashSet<>();
        this.fixedMobCapacity = GameConstants.DEFAULT_FIELD_MOB_CAPACITY; // default
    }

    public void addFoothold(Foothold foothold) {
        getFootholds().add(foothold);
    }

    public Foothold findFootHoldBelow(Position pos) {
        Set<Foothold> footholds = getFootholds().stream().filter(
                fh -> fh.getX1() <= pos.getX() && fh.getX2() >= pos.getX()).collect(Collectors.toSet());
        Foothold res = null;
        int lastY = Integer.MAX_VALUE;
        for (Foothold fh : footholds) {
            int y = fh.findYFromX(pos.getX());
            if (res == null && y >= pos.getY()) {
                res = fh;
                lastY = y;
            } else {
                if (y < lastY && y >= pos.getY()) {
                    res = fh;
                    lastY = y;
                }
            }
        }
        return res;
    }

    public void addPortal(Portal portal) {
        getPortals().add(portal);
    }

    public void addLife(Life life){
        getLifes().add(life);
    }

    public void addDirectionInfo(int node, List<String> scripts) {
        directionInfo.put(node, scripts);
    }
}
