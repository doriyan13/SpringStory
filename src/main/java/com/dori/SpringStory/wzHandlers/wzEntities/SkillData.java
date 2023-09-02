package com.dori.SpringStory.wzHandlers.wzEntities;

import com.dori.SpringStory.enums.SkillStat;
import com.dori.SpringStory.utils.utilEntities.Rect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillData {
    // Fields -
    private int skillId;
    private int rootId;
    private int masterLevel;
    private int maxLevel;
    private Map<SkillStat, String> skillStatInfo = new HashMap<>();
    private List<Rect> rects = new ArrayList<>();
    private Map<Integer, Integer> reqSkills = new HashMap<>();
    private Map<Integer, Integer> mpCostByLevel = new HashMap<>(); // this is relevant for some skills that don't manage the mpConsumption in common node in the WZ

    public void addReqSkill(int skillID, int slv) {
        getReqSkills().put(skillID, slv);
    }

    public void addRect(Rect rect) {
        getRects().add(rect);
    }

    public void addMpCostByLevel(int lvl, int mpCost) {
        getMpCostByLevel().putIfAbsent(lvl, mpCost);
    }

    public void addSkillStatInfo(SkillStat sc, String value) {
        skillStatInfo.put(sc, value);
    }
}
