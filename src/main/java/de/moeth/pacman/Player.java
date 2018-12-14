package de.moeth.pacman;

import java.util.HashSet;
import java.util.Set;

/* This is the pacman object */
public class Player extends Mover {

    /* Direction is used in demoMode, currDirection and desiredDirection are used in non demoMode*/
    public Direction direction;
    public Direction currDirection;
    public Direction desiredDirection;

    /* Keeps track of pellets eaten to determine end of game */
    int pelletsEaten;

    /* Last location */
    public int lastX;
    public int lastY;

    /* Current location */
    public int x;
    public int y;

    /* Which pellet the pacman is on top of */
    int pelletX;
    int pelletY;

    /* teleport is true when travelling through the teleport tunnels*/
    public boolean teleport;

    /* Stopped is set when the pacman is not moving or has been killed */
    boolean stopped = false;

    /* Constructor places pacman in initial location and orientation */
    public Player(int x, int y) {

        teleport = false;
        pelletsEaten = 0;
        pelletX = x / gridSize - 1;
        pelletY = y / gridSize - 1;
        lastX = x;
        lastY = y;
        this.x = x;
        this.y = y;
        currDirection = Direction.L;
        desiredDirection = Direction.L;
    }

    /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
    public Direction newDirection() {
        Direction backwards = Direction.U;
        int newX = x, newY = y;
        int lookX = x, lookY = y;
        switch (direction) {
            case L:
                backwards = Direction.R;
                break;
            case R:
                backwards = Direction.L;
                break;
            case U:
                backwards = Direction.D;
                break;
            case D:
                backwards = Direction.U;
                break;
        }
        Direction newDirection = backwards;
        Set<Direction> set = new HashSet<Direction>();
        while (newDirection == backwards || !isValidDest(lookX, lookY)) {
            if (set.size() == 3) {
                newDirection = backwards;
                break;
            }
            newX = x;
            newY = y;
            lookX = x;
            lookY = y;
            int random = (int) (Math.random() * 4) + 1;
            if (random == 1) {
                newDirection = Direction.L;
                newX -= increment;
                lookX -= increment;
            } else if (random == 2) {
                newDirection = Direction.R;
                newX += increment;
                lookX += gridSize;
            } else if (random == 3) {
                newDirection = Direction.U;
                newY -= increment;
                lookY -= increment;
            } else if (random == 4) {
                newDirection = Direction.D;
                newY += increment;
                lookY += gridSize;
            }
            if (newDirection != backwards) {
                set.add(newDirection);
            }
        }
        return newDirection;
    }

    /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
    public boolean isChoiceDest() {
        if (x % gridSize == 0 && y % gridSize == 0) {
            return true;
        }
        return false;
    }

    /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
    public void demoMove() {
        lastX = x;
        lastY = y;
        if (isChoiceDest()) {
            direction = newDirection();
        }
        switch (direction) {
            case L:
                if (isValidDest(x - increment, y)) {
                    x -= increment;
                } else if (y == 9 * gridSize && x < 2 * gridSize) {
                    x = max - gridSize * 1;
                    teleport = true;
                }
                break;
            case R:
                if (isValidDest(x + gridSize, y)) {
                    x += increment;
                } else if (y == 9 * gridSize && x > max - gridSize * 2) {
                    x = 1 * gridSize;
                    teleport = true;
                }
                break;
            case U:
                if (isValidDest(x, y - increment)) {
                    y -= increment;
                }
                break;
            case D:
                if (isValidDest(x, y + gridSize)) {
                    y += increment;
                }
                break;
        }
        currDirection = direction;
        frameCount++;
    }

    /* The move function moves the pacman for one frame in non demo mode */
    public void move() {
        lastX = x;
        lastY = y;

        /* Try to turn in the direction input by the user */
        /*Can only turn if we're in center of a grid*/
        int gridSize = 20;
        if (x % 20 == 0 && y % 20 == 0 ||
                /* Or if we're reversing*/
                (desiredDirection == Direction.L && currDirection == Direction.R) ||
                (desiredDirection == Direction.R && currDirection == Direction.L) ||
                (desiredDirection == Direction.U && currDirection == Direction.D) ||
                (desiredDirection == Direction.D && currDirection == Direction.U)
                ) {
            switch (desiredDirection) {
                case L:
                    if (isValidDest(x - increment, y)) {
                        x -= increment;
                    }
                    break;
                case R:
                    if (isValidDest(x + gridSize, y)) {
                        x += increment;
                    }
                    break;
                case U:
                    if (isValidDest(x, y - increment)) {
                        y -= increment;
                    }
                    break;
                case D:
                    if (isValidDest(x, y + gridSize)) {
                        y += increment;
                    }
                    break;
            }
        }
        /* If we haven't moved, then move in the direction the pacman was headed anyway */
        if (lastX == x && lastY == y) {
            switch (currDirection) {
                case L:
                    if (isValidDest(x - increment, y)) {
                        x -= increment;
                    } else if (y == 9 * gridSize && x < 2 * gridSize) {
                        x = max - gridSize * 1;
                        teleport = true;
                    }
                    break;
                case R:
                    if (isValidDest(x + gridSize, y)) {
                        x += increment;
                    } else if (y == 9 * gridSize && x > max - gridSize * 2) {
                        x = 1 * gridSize;
                        teleport = true;
                    }
                    break;
                case U:
                    if (isValidDest(x, y - increment)) {
                        y -= increment;
                    }
                    break;
                case D:
                    if (isValidDest(x, y + gridSize)) {
                        y += increment;
                    }
                    break;
            }
        }

        /* If we did change direction, update currDirection to reflect that */
        else {
            currDirection = desiredDirection;
        }

        /* If we didn't move at all, set the stopped flag */
        if (lastX == x && lastY == y) {
            stopped = true;
        }

            /* Otherwise, clear the stopped flag and increment the frameCount for animation purposes*/
        else {
            stopped = false;
            frameCount++;
        }
    }

    /* Update what pellet the pacman is on top of */
    public void updatePellet() {
        if (x % gridSize == 0 && y % gridSize == 0) {
            pelletX = x / gridSize - 1;
            pelletY = y / gridSize - 1;
        }
    }
}
