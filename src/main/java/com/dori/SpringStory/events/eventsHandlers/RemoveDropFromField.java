package com.dori.SpringStory.events.eventsHandlers;

import com.dori.SpringStory.world.fieldEntities.Drop;
import com.dori.SpringStory.world.fieldEntities.Field;

public record RemoveDropFromField(Drop drop, Field field) implements Runnable {
    @Override
    public void run() {
        boolean dropStillInField = field.getDrops().get(drop.getId()) != null;
        if (dropStillInField) {
            field.removeDrop(drop.getId(), 0, -1);
        }
    }
}
