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
    public Player(Position position) {
        teleport = false;
        pelletsEaten = 0;
        pellet = Position.ofGrid(position);
        last = position;
        this.location = position;
        currDirection = Direction.L;
        desiredDirection = Direction.L;
    }

    /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
    private Direction newDirection() {
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
                newX -= Board.INCREMENT;
                lookX -= Board.INCREMENT;
            } else if (random == 2) {
                newDirection = Direction.R;
                newX += Board.INCREMENT;
                lookX += Board.GRID_SIZE;
            } else if (random == 3) {
                newDirection = Direction.U;
                newY -= Board.INCREMENT;
                lookY -= Board.INCREMENT;
            } else if (random == 4) {
                newDirection = Direction.D;
                newY += Board.INCREMENT;
                lookY += Board.GRID_SIZE;
            }
            if (newDirection != backwards) {
                set.add(newDirection);
            }
        }
        return newDirection;
    }

    /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
    public void demoMove() {
        last = location;
        if (location.isGrid()) {
            direction = newDirection();
        }
        moveOrTeleport(direction);
        currDirection = direction;
        frameCount++;
    }

    /* The move function moves the pacman for one frame in non demo mode */
    public void move() {
        last = location;

        /* Try to turn in the direction input by the user */
        /*Can only turn if we're in center of a grid*/
        /* Or if we're reversing*/
        if (location.isGrid() || Direction.isOpposite(desiredDirection, currDirection)) {
            location = move(desiredDirection, location);
        }
        /* If we haven't moved, then move in the direction the pacman was headed anyway */
        if (Position.isEqualll(last, location)) {
            moveOrTeleport(currDirection);
        }

        /* If we did change direction, update currDirection to reflect that */
        else {
            currDirection = desiredDirection;
        }

        /* If we didn't move at all, set the stopped flag */
        if (Position.isEqualll(last, location)) {
            stopped = true;
        }

        /* Otherwise, clear the stopped flag and increment the frameCount for animation purposes*/
        else {
            stopped = false;
            frameCount++;
        }
    }

    private void moveOrTeleport(final Direction direction) {
        if (isValidDirection(direction, location)) {
            location = direction.move(location, Board.INCREMENT);
        } else {

            switch (direction) {
                case L:
                    if (location.y == 9 * Board.GRID_SIZE && location.x < 2 * Board.GRID_SIZE) {
                        location = location.setX(Board.MAX - Board.GRID_SIZE * 1);
                        teleport = true;
                    }
                    break;
                case R:
                    if (location.y == 9 * Board.GRID_SIZE && location.x > Board.MAX - Board.GRID_SIZE * 2) {
                        location = location.setX(1 * Board.GRID_SIZE);
                        teleport = true;
                    }
                    break;
            }
        }
    }

    /* Update what pellet the pacman is on top of */
    public void updatePellet() {
        if (location.isGrid()) {
            pellet = Position.ofGrid(location);
        }
    }
}
