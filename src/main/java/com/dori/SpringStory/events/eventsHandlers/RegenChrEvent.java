package com.dori.SpringStory.events.eventsHandlers;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.EventType;
import com.dori.SpringStory.events.EventManager;

import static com.dori.SpringStory.enums.EventType.REGEN_CHARACTER;

public record RegenChrEvent(MapleChar chr, int amount, boolean healthRegen, int delay) implements Runnable {
    @Override
    public void run() {
        if (healthRegen) {
            chr.modifyHp(amount);
        } else {
            chr.modifyMp(amount);
        }
        int intervalCount = chr.getHpIntervalCountLeft().get();
        if(intervalCount > 0) {
            chr.getHpIntervalCountLeft().set(intervalCount - 1);
            EventManager.addEvent(chr.getId(), REGEN_CHARACTER,new RegenChrEvent(chr, amount, healthRegen, delay), delay);
        }
    }
}
