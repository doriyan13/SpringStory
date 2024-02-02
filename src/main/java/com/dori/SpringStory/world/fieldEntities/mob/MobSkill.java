package com.dori.SpringStory.world.fieldEntities.mob;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobSkill {
    private int skillSN;
    private int skillID;
    private byte action;
    private int level;
    private int effectAfter;

}
