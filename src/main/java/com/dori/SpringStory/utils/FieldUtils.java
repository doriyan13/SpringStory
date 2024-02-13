package com.dori.SpringStory.utils;

import com.dori.SpringStory.client.MapleClient;
import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.enums.Job;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Field;
import com.dori.SpringStory.world.fieldEntities.Portal;
import org.jetbrains.annotations.NotNull;

public interface FieldUtils {
    Logger logger = new Logger(FieldUtils.class);

    static int getStartingFieldByJob(int jobID) {
        int startingField = 10_000; // Maple Road - Mushroom Park
        Job job = Job.getJobById(jobID);
        switch (job) {
            case Citizen -> startingField = 310010000; // resistance headquarters
            case Noblesse -> startingField = 130010220; // kiridu's hatchery
            case Legend -> startingField = 140090000; // Snow Island: Ice Cave
            case Evan -> startingField = 100030100; // Utah's House: Small Attic
        }
        return startingField;
    }

    static void transferChrToField(@NotNull MapleChar chr,
                                   int mapID) {
        MapleClient c = chr.getMapleClient();
        Field field = c.getMapleChannelInstance().getField(mapID);
        if (field != null) {
            Portal currPortal = field.getPortalByName("sp");
            // Set char position in field -
            chr.setPosition(new Position(currPortal.getPosition().getX(), currPortal.getPosition().getY()));
            // Add player to the field -
            field.spawnPlayer(chr, true);
            //TODO: need to handle controller for npcs!!
        } else {
            logger.error("Got un-valid mapID for a char that cause a null field!, closing session for: " + chr.getName());
            c.close();
        }
    }
}
