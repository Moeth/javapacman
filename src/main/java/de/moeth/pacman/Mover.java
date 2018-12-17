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
            return desiredDirection.move(location);
        }
        return location;
    }

    boolean isValidDirection(Direction desiredDirection, final Position location) {
        Position move = desiredDirection.move(location);
        return isValidDest(move);
    }

    boolean isValidDest(final Position position) {
        return gameMap.isValidDest(position);
    }
}
