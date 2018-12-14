package de.moeth.pacman;

public enum Direction {

    L, R, U, D;

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

    public static boolean isOpposite(Direction desiredDirection, Direction currDirection) {
        return desiredDirection.backwards() == currDirection;
//        desiredDirection == L && currDirection == R ||
//                desiredDirection == R && currDirection == L ||
//                desiredDirection == U && currDirection == D ||
//                desiredDirection == D && currDirection == U;
    }

    Position move(final Position location, final int increment) {
        switch (this) {
            case L:
                return location.move(-increment, 0);
            case R:
                return location.move(increment, 0);
            case U:
                return location.move(0, -increment);
            case D:
                return location.move(0, increment);
        }
        return location;
    }
}
