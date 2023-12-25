package com.dori.SpringStory.events.eventsHandlers;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.events.EventManager;

import static com.dori.SpringStory.enums.EventType.CONSUME_CHARACTER_HP;

public record ConsumeChrHpOrMpEvent(MapleChar chr, int amount, boolean consumeHealth, int delay,
                                    int intervalCountLeft) implements Runnable {
    @Override
    public void run() {
        if (amount > 0 && (chr.getHp() - amount > 0)) {
            if (consumeHealth) {
                chr.modifyHp(-amount);
            } else {
                chr.modifyMp(-amount);
            }
            if (intervalCountLeft > 0) {
                EventManager.addEvent(chr.getId(), CONSUME_CHARACTER_HP, new ConsumeChrHpOrMpEvent(chr, amount, consumeHealth, delay, intervalCountLeft - 1), delay);
            }
        }
    }
}
