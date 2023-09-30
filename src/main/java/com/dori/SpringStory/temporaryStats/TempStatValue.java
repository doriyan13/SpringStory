package com.dori.SpringStory.temporaryStats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempStatValue {
    private int value; // Value of the buff
    private int reason; // Reason for the buff, Usually buffId
    private int duration; // Buff Duration
}
