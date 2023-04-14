package com.dori.SpringStory.world.fieldEntities;

import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.utilEntities.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Foothold {
    private int id;
    private int layerId;
    private int groupId;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int next;
    private int prev;

    public Foothold(int id, int layerId, int groupId) {
        this.id = id;
        this.layerId = layerId;
        this.groupId = groupId;
    }

    public Foothold deepCopy() {
        return new Foothold(getId(), getLayerId(), getGroupId(), getX1(), getY1(), getX2(), getY2(), getNext(), getPrev());
    }

    /**
     * Gets the y value of this Foothold according to a given x value.
     * @param x the x where the y should correspond to
     * @return the y such that (x,y) lies on this Foothold
     */
    public int getYFromX(int x) {
        // interpolate between the two foothold ends for the y value below pos.x
        int x1 = getX1();
        int x2 = getX2() - x1;
        x = x - x1;
        double perc = (double) x / (double) x2;
        return (int) Math.ceil(getY1() + (perc * (getY2() - getY1())));
    }

    /**
     * Gets a random Position that is part of this Foothold.
     * @return the randomly generated Position.
     */
    public Position getRandomPosition() {
        int randX = MapleUtils.getRandom(getX1(), getX2());
        return new Position(randX, getYFromX(randX));
    }

    /**
     * Checks whether this Foothold is a wall (i.e., vertical)
     * @return whether this Foothold is a wall
     */
    public boolean isWall() {
        return getX1() == getX2();
    }
}
