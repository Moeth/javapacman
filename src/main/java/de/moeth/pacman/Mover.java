package de.moeth.pacman;

/* Both Player and Ghost inherit Mover.  Has generic functions relevant to both*/
class Mover {

    /* Framecount is used to count animation frames*/
    int frameCount = 0;

    /* State contains the game map */
//    final boolean[][] state;
    private final GameMap gameMap;

    /* Generic constructor */
    Mover(final GameMap gameMap) {
//        state = new boolean[Board.GRID_SIZE][Board.GRID_SIZE];
//        for (int i = 0; i < Board.GRID_SIZE; i++) {
//            for (int j = 0; j < Board.GRID_SIZE; j++) {
//                state[i][j] = false;
//            }
//        }
        this.gameMap = gameMap;
    }

    /* Updates the state information */
//    public void updateState(boolean[][] state) {
//        gameMap.updateState(state);
//    }

    Position move(Direction desiredDirection, final Position location) {
        if (isValidDirection(desiredDirection, location)) {
            return desiredDirection.move(location, Board.INCREMENT);
        }
        return location;
    }

    boolean isValidDirection(Direction desiredDirection, final Position location) {
        switch (desiredDirection) {
            case L:
                return isValidDest(location.x - Board.INCREMENT, location.y);
            case R:
                return isValidDest(location.x + Board.GRID_SIZE, location.y);
            case U:
                return isValidDest(location.x, location.y - Board.INCREMENT);
            case D:
                return isValidDest(location.x, location.y + Board.GRID_SIZE);
        }
        return false;
    }

    /* Determines if a set of coordinates is a valid destination.*/
    boolean isValidDest(int x, int y) {
    /* The first statements check that the x and y are inbounds.  The last statement checks the map to
       see if it's a valid location */
        int vvv = x / Board.GRID_SIZE - 1;
        int gy = y / Board.GRID_SIZE - 1;
        return (x % Board.GRID_SIZE == 0 || y % Board.GRID_SIZE == 0) && Board.GRID_SIZE <= x && x < Board.MAX && Board.GRID_SIZE <= y && y < Board.MAX && gameMap.getState(vvv, gy);
    }
}
