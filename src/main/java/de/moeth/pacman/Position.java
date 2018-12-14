package de.moeth.pacman;

import lombok.ToString;

@ToString
public class Position {

    public final int x;
    public final int y;

    public Position(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public static Position of(final int x, final int y) {
        return new Position(x, y);
    }

    public Position move(final int x, final int y) {
        return Position.of(this.x + x, this.y + y);
    }

    public Position setX(final int x) {
        return Position.of(x, this.y);
    }
}
