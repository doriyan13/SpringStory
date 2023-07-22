package com.dori.SpringStory.wzHandlers.wzEntities;

import com.dori.SpringStory.enums.InventoryType;
import com.dori.SpringStory.enums.ScrollStat;
import com.dori.SpringStory.enums.SpecStat;
import com.dori.SpringStory.inventory.ItemRewardInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemData {
    private int itemId;
    private InventoryType invType;
    private boolean cash;
    private int price;
    private int slotMax = 200;
    private boolean tradeBlock;
    private boolean notSale;
    private String path = "";
    private boolean noCursed;
    private Map<ScrollStat, Integer> scrollStats = new HashMap<>();
    private Map<SpecStat, Integer> specStats = new HashMap<>();
    private int bagType;
    private boolean quest;
    private int reqQuestOnProgress;
    private Set<Integer> questIDs = new HashSet<>();
    private int mobID;
    private int mobHP;
    private int createID;
    private int npcID;
    private int linkedID;
    private boolean monsterBook;
    private boolean notConsume;
    private String script = "";
    private int scriptNPC;
    private int life;
    private int masterLv;
    private int reqSkillLv;
    private Set<Integer> skills = new HashSet<>();
    private int moveTo;
    private Set<ItemRewardInfo> listOfItemRewardInfo = new HashSet<>();
    private int skillId;
    private int grade;
    private Set<Integer> reqItemIds = new HashSet<>();

    public void addSkill(int skill) {
        skills.add(skill);
    }

    public void putScrollStat(ScrollStat scrollStat, int val) {
        getScrollStats().put(scrollStat, val);
    }

    public void addQuest(int questID) {
        getQuestIDs().add(questID);
    }

    public void putSpecStat(SpecStat ss, int i) {
        getSpecStats().put(ss, i);
    }

    public void addItemReward(ItemRewardInfo iri) {
        getListOfItemRewardInfo().add(iri);
    }
}
