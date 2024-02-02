package com.dori.SpringStory.utils.utilEntities;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.utils.MapleUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rect {
    private int top;
    private int bottom;
    private int left;
    private int right;

    public Rect(Position lt, Position rb) {
        this.left = lt.getX();
        this.top = lt.getY();
        this.right = rb.getX();
        this.bottom = rb.getY();
    }

    /**
     * Returns the width of this Rect.
     * @return the width of this Rect.
     */
    public int calcWidth() {
        return Math.abs(getLeft() - getRight());
    }

    /**
     * Returns the height of this Rect.
     * @return The height of this Rect.
     */
    public int calcHeight() {
        return Math.abs(getTop() - getBottom());
    }

    /**
     * Encodes this Rect to a given {@link OutPacket}.
     * @param outPacket The OutPacket this Rect should be encoded to.
     */
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(getLeft());
        outPacket.encodeInt(getTop());
        outPacket.encodeInt(getRight());
        outPacket.encodeInt(getBottom());
    }

    /**
     * Returns whether a {@link Position} is inside this Rect.
     * @param position The Position to check.
     * @return if the position is not null and inside this Rect (rect.left < pos.x < rect.right &&
     * rect.top < pos.y < rect.bottom.
     */
    public boolean hasPositionInside(Position position) {
        if(position == null) {
            return false;
        }
        int x = position.getX();
        int y = position.getY();
        return x >= left && y >= top && x <= right && y <= bottom;
    }

    /**
     * Move this Rect left by the width, effectively flipping around the left edge.
     * @return The resulting Rect from the move.
     */
    public Rect moveLeft() {
        return new Rect(getLeft() - calcWidth(), getTop(), getLeft(), getBottom());
    }

    /**
     * Move this Rect right by the width, effectively flipping around the right edge.
     * @return The resulting Rect from the move.
     */
    public Rect moveRight() {
        return new Rect(getRight(), getTop(), getRight() + calcWidth(), getBottom());
    }

    /**
     * Flips this Rect horizontally around a certain Position's x .
     * @param x The x to flip around
     * @return The flipped Rect
     */
    public Rect horizontalFlipAround(int x) {
        return new Rect(getRight() - 2 * (getRight() - x), getTop(), getLeft() + 2 * (x - getLeft()), getBottom());
    }

    /**
     * Returns a deep copy of this Rect.
     * @return a deep copy of this Rect
     */
    public Rect deepCopy() {
        return new Rect(getLeft(), getTop(), getRight(), getBottom());
    }

    @Override
    public String toString() {
        return "Rect{" +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }

    /**
     * Returns a random Position that is inside this Rect.
     * @return the random Position
     */
    public Position calcRandomPositionInside() {
        int randX = MapleUtils.getRandom(getLeft(), getRight());
        int randY = MapleUtils.getRandom(getTop(), getBottom());
        return new Position(randX, randY);
    }
}
