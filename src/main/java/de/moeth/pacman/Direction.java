package de.moeth.pacman;

import java.util.Random;

public enum Direction {

    L, R, U, D;

    public static final Random RANDOM = new Random();

    public Direction backwards() {
        switch (this) {
            case L:
                return R;
            case R:
                return L;
            case U:
                return D;
            case D:
                return U;
        }
        throw new IllegalArgumentException();
    }

    Position move(final Position location) {
        switch (this) {
            case L:
                return location.move(-1, 0);
            case R:
                return location.move(1, 0);
            case U:
                return location.move(0, -1);
            case D:
                return location.move(0, 1);
        }
        return location;
    }

    public static Direction random() {
        int v = RANDOM.nextInt(Direction.values().length);
        return Direction.values()[v];
    }
}
