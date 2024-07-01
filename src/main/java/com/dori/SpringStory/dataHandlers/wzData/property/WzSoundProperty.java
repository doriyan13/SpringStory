package com.dori.SpringStory.dataHandlers.wzData.property;

import java.nio.ByteBuffer;

public record WzSoundProperty(ByteBuffer header,
                              ByteBuffer data) implements WzProperty {
}
