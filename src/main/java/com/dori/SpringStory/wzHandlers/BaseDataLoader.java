package com.dori.SpringStory.wzHandlers;

public interface BaseDataLoader<T> {
    void loadFromWz();
    void exportToJson();
    void loadFromJson();
}
