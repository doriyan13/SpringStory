package com.dori.SpringStory.dataHandlers.dataEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@ToString

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NpcData {
    private int npcID;
    private boolean move;
    private Map<Integer, String> scripts;

    public NpcData() {
        this.npcID = -1;
        this.move = false;
        this.scripts  = new HashMap<>();
    }
}
