package de.moeth.pacman;

import java.util.HashSet;
import java.util.Set;

import static de.moeth.pacman.Direction.*;

/* Ghost class controls the ghost. */
public class Ghost extends Mover {

    /* Direction ghost is heading */
    Direction direction;

    /* Last ghost location*/
//    int lastX;
//    int last.y;
    Position last;

    /* Current ghost location */
//    public int locationx;
//    public int locationy;
    public Position location;

    /* The pellet the ghost is on top of */
//    int pelletX, pellet.y;
    public Position pellet;

    /* The pellet the ghost was last on top of */
//    int lastPellet.x, lastPellet.y;
    public Position lastPellet;

    /*Constructor places ghost and updates states*/
    public Ghost(int x, int y) {
        direction = L;
        pellet = Position.of(x / gridSize - 1, y / gridSize - 1);
        lastPellet = pellet;
        last = Position.of(x, y);
        this.location = Position.of(x, y);
    }

    /* update pellet status */
    public void updatePellet() {
        int tempX = location.x / gridSize - 1;
        int tempY = location.y / gridSize - 1;
        if (tempX != pellet.x || tempY != pellet.y) {
            lastPellet = pellet;
            pellet = Position.of(tempX, tempY);
        }
    }

    /* Determines if the location is one where the ghost has to make a decision*/
    public boolean isChoiceDest() {
        if (location.x % gridSize == 0 && location.y % gridSize == 0) {
            return true;
        }
        return false;
    }

    /* Chooses a new direction randomly for the ghost to move */
    public Direction newDirection() {
        Direction backwards = U;
        int newX = location.x, newY = location.y;
        int lookX = location.x, lookY = location.y;
        switch (direction) {
            case L:
                backwards = R;
                break;
            case R:
                backwards = L;
                break;
            case U:
                backwards = D;
                break;
            case D:
                backwards = U;
                break;
        }

        Direction newDirection = backwards;
        /* While we still haven't found a valid direction */
        Set<Direction> set = new HashSet<Direction>();
        while (newDirection == backwards || !isValidDest(lookX, lookY)) {
            /* If we've tried every location, turn around and break the loop */
            if (set.size() == 3) {
                newDirection = backwards;
                break;
            }

            newX = location.x;
            newY = location.y;
            lookX = location.x;
            lookY = location.y;

            /* Randomly choose a direction */
            int random = (int) (Math.random() * 4) + 1;
            if (random == 1) {
                newDirection = L;
                newX -= increment;
                lookX -= increment;
            } else if (random == 2) {
                newDirection = R;
                newX += increment;
                lookX += gridSize;
            } else if (random == 3) {
                newDirection = U;
                newY -= increment;
                lookY -= increment;
            } else if (random == 4) {
                newDirection = D;
                newY += increment;
                lookY += gridSize;
            }
            if (newDirection != backwards) {
                set.add(newDirection);
            }
        }
        return newDirection;
    }

    /* Random move function for ghost */
    public void move() {
        last = location;

        /* If we can make a decision, pick a new direction randomly */
        if (isChoiceDest()) {
            direction = newDirection();
        }

        /* If that direction is valid, move that way */
        switch (direction) {
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
}
