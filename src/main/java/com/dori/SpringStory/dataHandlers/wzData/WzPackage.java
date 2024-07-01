package com.dori.SpringStory.dataHandlers.wzData;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WzPackage {
    private int start;
    private int hash;
    private WzDirectory directory;

    public WzPackage(int start,
                     int hash) {
        this.start = start;
        this.hash = hash;
    }
}
