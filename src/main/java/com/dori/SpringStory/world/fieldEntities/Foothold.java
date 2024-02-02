package com.dori.SpringStory.world.fieldEntities;

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
    public int findYFromX(int x) {
        // interpolate between the two foothold ends for the y value below pos.x
        int x1 = getX1();
        int x2 = getX2() - x1;
        x = x - x1;
        double percent = (double) x / (double) x2;
        return (int) Math.ceil(getY1() + (percent * (getY2() - getY1())));
    }
}
