package com.dori.SpringStory.dataHandlers.dataEntities;

import com.dori.SpringStory.world.fieldEntities.mob.MobSkill;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MobData {
    // Mob Fields -
    private int id;
    private long maxHp;
    private int maxMp;
    private int level;
    private int exp;
    // Mob WZ fields -
    private int firstAttack;
    private int summonType;
    private int category;
    private int pad;
    private int mad;
    private int pdr;
    private int mdr;
    private int acc;
    private int eva;
    private int pushed;
    private int speed;
    private String mobType = "";
    private double fs;
    private String elemAttr = "";
    private int hpTagColor;
    private int hpTagBgColor;
    private boolean HpGaugeHide;
    private int rareItemDropLevel;
    private boolean boss;
    private int hpRecovery;
    private int mpRecovery;
    private boolean undead;
    private boolean hideName;
    private boolean hideHP;
    private boolean noFlip;
    private long respawnDelay;
    private List<MobSkill> skills = new ArrayList<>();
    private boolean displayable = false;

    public MobData(int templateId) {
        this.id = templateId;
    }

    public void addSkill(MobSkill skill) {
        getSkills().add(skill);
    }
}
