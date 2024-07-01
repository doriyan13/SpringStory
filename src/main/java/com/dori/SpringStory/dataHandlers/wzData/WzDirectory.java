package com.dori.SpringStory.dataHandlers.wzData;

import java.util.Map;

public record WzDirectory(Map<String, WzDirectory> directories,
                          Map<String, WzImage> images) {
}
