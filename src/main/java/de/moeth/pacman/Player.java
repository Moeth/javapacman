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
    public Position last;
    /* Current location */
    public Position location;
    /* Which pellet the pacman is on top of */
    public Position pellet;
    /* teleport is true when travelling through the teleport tunnels*/
    public boolean teleport;
    /* Stopped is set when the pacman is not moving or has been killed */
    boolean stopped = false;

    /* Constructor places pacman in initial location and orientation */
    public Player(int x, int y) {
        teleport = false;
        pelletsEaten = 0;
        pellet = Position.of(x / Board.GRID_SIZE - 1, y / Board.GRID_SIZE - 1);
        last = Position.of(x, y);
        this.location = Position.of(x, y);
        currDirection = Direction.L;
        desiredDirection = Direction.L;
    }

    /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
    public Direction newDirection() {
        int newX = location.x, newY = location.y;
        int lookX = location.x, lookY = location.y;
        Direction backwards = direction.backwards();
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
                lookX += Board.GRID_SIZE;
            } else if (random == 3) {
                newDirection = Direction.U;
                newY -= increment;
                lookY -= increment;
            } else if (random == 4) {
                newDirection = Direction.D;
                newY += increment;
                lookY += Board.GRID_SIZE;
            }
            if (newDirection != backwards) {
                set.add(newDirection);
            }
        }
        return newDirection;
    }

    /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
    public boolean isChoiceDest() {
        if (location.x % Board.GRID_SIZE == 0 && location.y % Board.GRID_SIZE == 0) {
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
                } else if (location.y == 9 * Board.GRID_SIZE && location.x < 2 * Board.GRID_SIZE) {
                    location = location.setX(Board.MAX - Board.GRID_SIZE * 1);
                    teleport = true;
                }
                break;
            case R:
                if (isValidDest(location.x + Board.GRID_SIZE, location.y)) {
                    location = location.move(increment, 0);
                } else if (location.y == 9 * Board.GRID_SIZE && location.x > Board.MAX - Board.GRID_SIZE * 2) {
                    location = location.setX(1 * Board.GRID_SIZE);
                    teleport = true;
                }
                break;
            case U:
                if (isValidDest(location.x, location.y - increment)) {
                    location = location.move(0, -increment);
                }
                break;
            case D:
                if (isValidDest(location.x, location.y + Board.GRID_SIZE)) {
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
                        location = location.setX(Board.MAX - gridSize * 1);
                        teleport = true;
                    }
                    break;
                case R:
                    if (isValidDest(location.x + gridSize, location.y)) {
                        location = location.move(increment, 0);
                    } else if (location.y == 9 * gridSize && location.x > Board.MAX - gridSize * 2) {
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
        if (location.x % Board.GRID_SIZE == 0 && location.y % Board.GRID_SIZE == 0) {
            pellet = Position.of(location.x / Board.GRID_SIZE - 1, location.y / Board.GRID_SIZE - 1);
        }
    }
}
