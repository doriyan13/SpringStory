package com.dori.SpringStory.dataHandlers.wzData;

import com.dori.SpringStory.dataHandlers.wzData.property.WzListProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WzImage {
    private int offset;
    private WzListProperty property;

    public WzImage(int offset) {
        this.offset = offset;
    }
}
