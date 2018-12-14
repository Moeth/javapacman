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
//    public int last.x;
//    public int last.y;
    public Position last;

    /* Current location */
//    public int location.x;
//    public int location.y;
    public Position location;

    /* Which pellet the pacman is on top of */
//    int pellet.x;
//    int pellet.y;
    public Position pellet;

    /* teleport is true when travelling through the teleport tunnels*/
    public boolean teleport;

    /* Stopped is set when the pacman is not moving or has been killed */
    boolean stopped = false;

    /* Constructor places pacman in initial location and orientation */
    public Player(int x, int y) {

        teleport = false;
        pelletsEaten = 0;
        pellet = Position.of(x / gridSize - 1, y / gridSize - 1);
        last = Position.of(x, y);
        this.location = Position.of(x, y);
        currDirection = Direction.L;
        desiredDirection = Direction.L;
    }

    /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
    public Direction newDirection() {
        Direction backwards = Direction.U;
        int newX = location.x, newY = location.y;
        int lookX = location.x, lookY = location.y;
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
            newX = location.x;
            newY = location.y;
            lookX = location.x;
            lookY = location.y;
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
        if (location.x % gridSize == 0 && location.y % gridSize == 0) {
            return true;
        }
        return false;
    }

    /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
    public void demoMove() {
        last = location;
        if (isChoiceDest()) {
            direction = newDirection();
        }
        switch (direction) {
            case L:
                if (isValidDest(location.x - increment, location.y)) {
                    location = location.move(-increment, 0);
                } else if (location.y == 9 * gridSize && location.x < 2 * gridSize) {
                    location = location.setX(max - gridSize * 1);
                    teleport = true;
                }
                break;
            case R:
                if (isValidDest(location.x + gridSize, location.y)) {
                    location = location.move(increment, 0);
                } else if (location.y == 9 * gridSize && location.x > max - gridSize * 2) {
                    location = location.setX(1 * gridSize);
                    teleport = true;
                }
                break;
            case U:
                if (isValidDest(location.x, location.y - increment)) {
                    location = location.move(0, -increment);
                }
                break;
            case D:
                if (isValidDest(location.x, location.y + gridSize)) {
                    location = location.move(0, increment);
                }
                break;
        }
        currDirection = direction;
        frameCount++;
    }

    /* The move function moves the pacman for one frame in non demo mode */
    public void move() {
        last = location;

        /* Try to turn in the direction input by the user */
        /*Can only turn if we're in center of a grid*/
        int gridSize = Board.GRID_SIZE;
        if (location.x % Board.GRID_SIZE == 0 && location.y % Board.GRID_SIZE == 0 ||
                /* Or if we're reversing*/
                (desiredDirection == Direction.L && currDirection == Direction.R) ||
                (desiredDirection == Direction.R && currDirection == Direction.L) ||
                (desiredDirection == Direction.U && currDirection == Direction.D) ||
                (desiredDirection == Direction.D && currDirection == Direction.U)
                ) {
            switch (desiredDirection) {
                case L:
                    if (isValidDest(location.x - increment, location.y)) {
                        location = location.move(-increment, 0);
                    }
                    break;
                case R:
                    if (isValidDest(location.x + gridSize, location.y)) {
                        location = location.move(increment, 0);
                    }
                    break;
                case U:
                    if (isValidDest(location.x, location.y - increment)) {
                        location = location.move(0, -increment);
                    }
                    break;
                case D:
                    if (isValidDest(location.x, location.y + gridSize)) {
                        location = location.move(0, increment);
                    }
                    break;
            }
        }
        /* If we haven't moved, then move in the direction the pacman was headed anyway */
        if (last.x == location.x && last.y == location.y) {
            switch (currDirection) {
                case L:
                    if (isValidDest(location.x - increment, location.y)) {
                        location = location.move(-increment, 0);
                    } else if (location.y == 9 * gridSize && location.x < 2 * gridSize) {
                        location = location.setX(max - gridSize * 1);
                        teleport = true;
                    }
                    break;
                case R:
                    if (isValidDest(location.x + gridSize, location.y)) {
                        location = location.move(increment, 0);
                    } else if (location.y == 9 * gridSize && location.x > max - gridSize * 2) {
                        location = location.setX(1 * gridSize);
                        teleport = true;
                    }
                    break;
                case U:
                    if (isValidDest(location.x, location.y - increment)) {
                        location = location.move(0, -increment);
                    }
                    break;
                case D:
                    if (isValidDest(location.x, location.y + gridSize)) {
                        location = location.move(0, increment);
                    }
                    break;
            }
        }

        /* If we did change direction, update currDirection to reflect that */
        else {
            currDirection = desiredDirection;
        }

        /* If we didn't move at all, set the stopped flag */
        if (last.x == location.x && last.y == location.y) {
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
        if (location.x % gridSize == 0 && location.y % gridSize == 0) {
            pellet = Position.of(location.x / gridSize - 1, location.y / gridSize - 1);
        }
    }
}
