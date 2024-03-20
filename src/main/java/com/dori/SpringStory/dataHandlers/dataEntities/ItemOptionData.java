package com.dori.SpringStory.dataHandlers.dataEntities;

import com.dori.SpringStory.enums.BaseStat;
import com.dori.SpringStory.enums.ItemOptionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemOptionData {
    private int id;
    private int optionType;
    private int reqLevel;
    private Map<Integer, Map<BaseStat, Double>> statValuesPerLevel = new HashMap<>();
    private Map<Integer, Map<ItemOptionType, Integer>> miscValuesPerLevel = new HashMap<>();
}
