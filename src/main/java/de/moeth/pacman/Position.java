package de.moeth.pacman;

import lombok.ToString;

import static de.moeth.pacman.Board.GRID_SIZE;

@ToString
public class Position {

    public final int x;
    public final int y;

    private Position(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public static Position of(final int x, final int y) {
        return new Position(x, y);
    }

    public static Position ofGrid(Position location) {
        return ofGrid(location.x, location.y);
    }

    private static Position ofGrid(final int x, final int y) {
        return Position.of(x / Board.GRID_SIZE - 1, y / Board.GRID_SIZE - 1);
    }

    public Position move(final int x, final int y) {
        return Position.of(this.x + x, this.y + y);
    }

    public Position setX(final int x) {
        return Position.of(x, y);
    }

    public boolean isGrid() {
        return x % Board.GRID_SIZE == 0 && y % Board.GRID_SIZE == 0;
    }

    public static boolean isEqualll(Position p1, Position p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }

    public static int toGrid(int p) {
        return p / GRID_SIZE;
    }
}
