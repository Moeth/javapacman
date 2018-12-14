package de.moeth.pacman;

import static de.moeth.pacman.Board.GRID_SIZE;

public class GameMap {

    /*Contains the game map, passed to player and ghosts */
    boolean[][] state;
//    private final GameMap gameMap = new GameMap();

    /* Contains the state of all pellets*/
    boolean[][] pellets;

    public GameMap() {
        state = new boolean[GRID_SIZE][GRID_SIZE];
        pellets = new boolean[GRID_SIZE][GRID_SIZE];


        /* Clear state and pellets arrays */
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                state[i][j] = true;
                pellets[i][j] = true;
            }
        }

        /* Handle the weird spots with no pellets*/
        for (int i = 5; i < 14; i++) {
            for (int j = 5; j < 12; j++) {
                pellets[i][j] = false;
            }
        }
        pellets[9][7] = false;
        pellets[8][8] = false;
        pellets[9][8] = false;
        pellets[10][8] = false;

        /* Don't let the player go in the ghost box*/
//        state[9][7] = false;

//        state = new boolean[GRID_SIZE][GRID_SIZE];
//        for (int i = 0; i < GRID_SIZE; i++) {
//            for (int j = 0; j < GRID_SIZE; j++) {
//                state[i][j] = false;
//            }
//        }
    }

//    public void updateState(boolean[][] state) {
//        for (int i = 0; i < GRID_SIZE; i++) {
//            for (int j = 0; j < GRID_SIZE; j++) {
//                this.state[i][j] = state[i][j];
//            }
//        }
//    }

    /* Function is called during drawing of the map.
   Whenever the a portion of the map is covered up with a barrier,
   the map and pellets arrays are updated accordingly to note
   that those are invalid locations to travel or put pellets
*/
    public void updateMap(int x, int y, int width, int height) {
        for (int i = x / GRID_SIZE; i < x / GRID_SIZE + width / GRID_SIZE; i++) {
            for (int j = y / GRID_SIZE; j < y / GRID_SIZE + height / GRID_SIZE; j++) {
                state[i - 1][j - 1] = false;
                pellets[i - 1][j - 1] = false;
            }
        }
    }
}
