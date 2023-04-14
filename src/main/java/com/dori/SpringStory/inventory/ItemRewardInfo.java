package com.dori.SpringStory.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemRewardInfo {
    private int count;
    private int itemID;
    private double prob;
    private int period;
    private String effect = "";
}
