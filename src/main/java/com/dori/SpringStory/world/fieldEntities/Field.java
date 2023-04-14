package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.wzHandlers.wzEntities.MapData;
import lombok.*;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class Field extends MapData {
    //TODO: will be the field instances that each channel will have!

    public Field(int id){
        super(id);
    }
}
