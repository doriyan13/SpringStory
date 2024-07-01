package com.dori.SpringStory.dataHandlers.wzData.property;

import java.nio.ByteBuffer;

public record WzCanvasProperty(
        WzListProperty properties,
        int width,
        int height,
        int format,
        int format2,
        ByteBuffer data
) implements WzProperty {

}
