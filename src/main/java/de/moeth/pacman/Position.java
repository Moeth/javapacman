package de.moeth.pacman;

import lombok.ToString;

@ToString
public class Position {

    public final int x;
    public final int y;

    private Position(final int x, final int y) {
        this.x = x;
        this.y = y;
//        Preconditions.checkArgument(between(0, GRID_WIDTH), "invalid "+this);
    }

    public static Position of(final int x, final int y) {
        return new Position(x, y);
    }

    public Position move(final int x, final int y) {
        return of(this.x + x, this.y + y);
    }

    public Position setX(final int x) {
        return of(x, y);
    }

    public boolean between(int min, int max) {
        return min <= x && x < max
                && min <= y && y < max;
    }

    public boolean isOnPosition(final Position p) {
        return x == p.x && y == p.y;
    }

    public Position move(Direction direction) {
        return direction.move(this);
    }
}
