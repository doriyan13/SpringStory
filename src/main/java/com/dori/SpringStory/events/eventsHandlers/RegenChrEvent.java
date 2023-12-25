package com.dori.SpringStory.events.eventsHandlers;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.EventType;
import com.dori.SpringStory.events.EventManager;

import static com.dori.SpringStory.enums.EventType.REGEN_CHARACTER;

public record RegenChrEvent(MapleChar chr, int amount, boolean healthRegen, int delay, int intervalCount) implements Runnable {
    @Override
    public void run() {
        if (amount > 0) {
            if (healthRegen) {
                chr.modifyHp(amount);
            } else {
                chr.modifyMp(amount);
            }
            if (intervalCount > 0) {
                EventManager.addEvent(chr.getId(), REGEN_CHARACTER, new RegenChrEvent(chr, amount, healthRegen, delay, intervalCount - 1), delay);
            }
        }
    }
}
