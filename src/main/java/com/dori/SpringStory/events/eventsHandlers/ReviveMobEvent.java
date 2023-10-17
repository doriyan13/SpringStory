package com.dori.SpringStory.events.eventsHandlers;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.mob.Mob;

import static com.dori.SpringStory.enums.MobSummonType.*;

public record ReviveMobEvent(Mob mob, MapleChar lastController)
        implements Runnable {

    @Override
    public void run() {
        // Field -
        Field field = mob.getField();
        // Verify if you need to spawn the mob or just add back to the list of mobs?
        if (!field.getPlayers().isEmpty()) {
            mob.setAppearType(Regen);
            field.spawnMob(mob, lastController);
            mob.setAppearType(Normal);
        } else {
            field.addMob(mob);
        }
    }
}
