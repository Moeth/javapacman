package de.moeth.pacman;

/* Both Player and Ghost inherit Mover.  Has generic functions relevant to both*/
class Mover {

    /* Framecount is used to count animation frames*/
    int frameCount = 0;

    /* State contains the game map */
    final boolean[][] state;

    /* gridSize is the size of one square in the game.
       max is the height/width of the game.
       increment is the speed at which the object moves,
       1 increment per move() call */
    final int gridSize;
    final int max;
    final int increment;

    /* Generic constructor */
    public Mover() {
        gridSize = Board.GRID_SIZE;
        increment = 4;
        max = Board.MAX;
        state = new boolean[Board.GRID_SIZE][Board.GRID_SIZE];
        for (int i = 0; i < Board.GRID_SIZE; i++) {
            for (int j = 0; j < Board.GRID_SIZE; j++) {
                state[i][j] = false;
            }
        }
    }

    /* Updates the state information */
    public void updateState(boolean[][] state) {
        for (int i = 0; i < Board.GRID_SIZE; i++) {
            for (int j = 0; j < Board.GRID_SIZE; j++) {
                this.state[i][j] = state[i][j];
            }
        }
    }

    /* Determines if a set of coordinates is a valid destination.*/
    public boolean isValidDest(int x, int y) {
    /* The first statements check that the x and y are inbounds.  The last statement checks the map to
       see if it's a valid location */
        if ((x % Board.GRID_SIZE == 0 || y % Board.GRID_SIZE == 0) && Board.GRID_SIZE <= x && x < Board.MAX && Board.GRID_SIZE <= y && y < Board.MAX && state[x / Board.GRID_SIZE - 1][y / Board.GRID_SIZE - 1]) {
            return true;
        }
        return false;
    }
}
