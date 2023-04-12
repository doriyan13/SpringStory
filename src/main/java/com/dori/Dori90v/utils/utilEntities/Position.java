package com.dori.Dori90v.utils.utilEntities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Position {
    private int x;
    private int y;

    @Override
    public String toString() {
        return String.format("x: %d, y: %d", getX(), getY());
    }

    public Position deepCopy() {
        return new Position(getX(), getY());
    }

    /**
     * Creates a Rect around this Position at its center.
     * Corners will be (pos.x + left, pos.y + top), (pos.x + right, pos.y + bottom)
     * @param rect The Rect around this Position
     * @return The newly created Rect
     */
    public Rect getRectAround(Rect rect) {
        int x = getX();
        int y = getY();
        return new Rect(x + rect.getLeft(), y + rect.getTop(), x + rect.getRight(), y + rect.getBottom());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x &&
                y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
