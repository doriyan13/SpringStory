package com.dori.SpringStory.utils;

import com.dori.SpringStory.utils.utilEntities.Position;
import com.dori.SpringStory.world.fieldEntities.Foothold;

public interface PositionUtils {

    /**
     * Gets a random Position that is part of this Foothold.
     * @return the randomly generated Position.
     */
    static Position getRandomPosition(Foothold fh) {
        int randX = MapleUtils.getRandom(fh.getX1(), fh.getX2());
        return new Position(randX, fh.findYFromX(randX));
    }

    /**
     * Checks whether this Foothold is a wall (i.e., vertical)
     * @return whether this Foothold is a wall
     */
    static boolean isWall(Foothold fh) {
        return fh.getX1() == fh.getX2();
    }
}
