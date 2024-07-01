package com.dori.SpringStory.dataHandlers.wzData.property;

import java.util.Map;

public record WzListProperty(Map<String, Object> items) implements WzProperty {

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) items.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        if (!items.containsKey(key)) {
            return defaultValue;
        }
        return (T) items.get(key);
    }
}
