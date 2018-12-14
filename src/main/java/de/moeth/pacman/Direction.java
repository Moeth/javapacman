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


}
