package de.moeth.pacman;

import java.awt.*;

import static de.moeth.pacman.Board.GRID_SIZE;

class GameMap implements Drawable {

    private static final int PELLET_SIZE = 4;
    static final int GRID_WIDTH = 19;
    /*Contains the game map, passed to player and ghosts */
    private final boolean[][] state;
//    private final GameMap gameMap = new GameMap();

    /* Contains the state of all pellets*/
    private final boolean[][] pellets;

    public GameMap() {
        state = new boolean[GRID_WIDTH][GRID_WIDTH];
        pellets = new boolean[GRID_WIDTH][GRID_WIDTH];



//        pellets[9][7] = false;
//        pellets[8][8] = false;
//        pellets[9][8] = false;
//        pellets[10][8] = false;

        /* Don't let the player go in the ghost box*/
//        state[9][7] = false;

//        state = new boolean[GRID_SIZE][GRID_SIZE];
//        for (int i = 0; i < GRID_SIZE; i++) {
//            for (int j = 0; j < GRID_SIZE; j++) {
//                state[i][j] = false;
//            }
//        }
    }

    public boolean getState(Position position) {
        return getState(position.x, position.y);
    }

    public boolean getState(int x, int y) {
        return state[x][y];
    }

    public boolean getPellet(Position position) {
        return getPellet(position.x, position.y);
    }

    public boolean getPellet(int x, int y) {
        return pellets[x][y];
    }

    public void eatPellet(Position position) {
        eatPellet(position.x, position.y);
    }

    private void eatPellet(int x, int y) {
        pellets[x][y] = false;
    }

    public void initMap() {

        /* Clear state and pellets arrays */
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
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

        updateMap2(2, 2, 3, 1);
        updateMap2(6, 2, 3, 1);
        updateMap2(10, 1, 1, 2);
        updateMap2(12, 2, 3, 1);
        updateMap2(16, 2, 3, 1);
        updateMap2(2, 4, 3, 1);
        updateMap2(8, 4, 5, 1);
        updateMap2(10, 4, 1, 3);
        updateMap2(16, 4, 3, 1);

        updateMap2(1, 6, 4, 3);
        updateMap2(16, 6, 4, 3);
        updateMap2(1, 10, 4, 3);
        updateMap2(16, 10, 4, 3);

        updateMap2(8, 8, 2, 1);
        updateMap2(11, 8, 2, 1);
        updateMap2(8, 9, 1, 1);
        updateMap2(8, 10, 5, 1);
        updateMap2(12, 9, 1, 1);
////        g.setColor(Color.BLUE);
//
        updateMap2(6, 6, 3, 1);
        updateMap2(6, 4, 1, 5);
        updateMap2(14, 4, 1, 5);
        updateMap2(12, 6, 3, 1);

        updateMap2(14, 10, 1, 3);
        updateMap2(6, 10, 1, 3);
        updateMap2(8, 12, 5, 1);
        updateMap2(10, 13, 1, 2);

        updateMap2(6, 14, 3, 1);
        updateMap2(12, 14, 3, 1);

        updateMap2(2, 14, 3, 1);
        updateMap2(4, 14, 1, 3);
        updateMap2(16, 14, 3, 1);
        updateMap2(16, 14, 1, 3);
//
        updateMap2(1, 16, 2, 1);
        updateMap2(18, 16, 2, 1);
        updateMap2(8, 16, 5, 1);
        updateMap2(10, 16, 1, 3);

        updateMap2(2, 18, 7, 1);
        updateMap2(12, 18, 7, 1);
        updateMap2(14, 16, 1, 2);
        updateMap2(6, 16, 1, 3);
    }

    private void updateMap2(int x, int y, int width, int height) {
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                state[i - 1][j - 1] = false;
                pellets[i - 1][j - 1] = false;
            }
        }
    }

    @Override
    public void draw(final Graphics g) {
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_WIDTH; y++) {
                boolean b = state[x][y];
                g.setColor(b ? Color.BLACK : Color.BLUE);
                g.fillRect(GRID_SIZE + x * GRID_SIZE, GRID_SIZE + y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
                if (pellets[x][y]) {
                    g.setColor(Color.YELLOW);
                    g.fillOval(GRID_SIZE + x * GRID_SIZE + 8, GRID_SIZE + y * GRID_SIZE + 8, PELLET_SIZE, PELLET_SIZE);
                }
            }
        }
    }

    public boolean isValidDest(final Position position) {
//        Position grid = Position.ofGrid(position);

        return position.between(0, GRID_WIDTH)
                && getState(position.x, position.y);
    }
}
