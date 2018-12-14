package de.moeth.pacman;

import java.util.HashSet;
import java.util.Set;

import static de.moeth.pacman.Direction.*;

/* Ghost class controls the ghost. */
public class Ghost extends Mover {

    /* Direction ghost is heading */
    Direction direction;
    /* Last ghost location*/
    Position last;
    /* Current ghost location */
    public Position location;
    /* The pellet the ghost is on top of */
    public Position pellet;
    /* The pellet the ghost was last on top of */
    public Position lastPellet;

    /*Constructor places ghost and updates states*/
    public Ghost(Position position) {
        direction = L;
        pellet = Position.ofGrid(position);
        lastPellet = pellet;
        last = position;
        this.location = position;
    }

    /* update pellet status */
    public void updatePellet() {
        Position temp = Position.ofGrid(location);
        if (temp.x != pellet.x || temp.y != pellet.y) {
            lastPellet = pellet;
            pellet = temp;
        }
    }

    /* Chooses a new direction randomly for the ghost to move */
    private Direction newDirection() {
        Direction backwards = U;
        int newX = location.x, newY = location.y;
        int lookX = location.x, lookY = location.y;
        backwards = direction.backwards();

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
                newX -= Board.INCREMENT;
                lookX -= Board.INCREMENT;
            } else if (random == 2) {
                newDirection = R;
                newX += Board.INCREMENT;
                lookX += Board.GRID_SIZE;
            } else if (random == 3) {
                newDirection = U;
                newY -= Board.INCREMENT;
                lookY -= Board.INCREMENT;
            } else if (random == 4) {
                newDirection = D;
                newY += Board.INCREMENT;
                lookY += Board.GRID_SIZE;
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
        if (location.isGrid()) {
            direction = newDirection();
        }

        /* If that direction is valid, move that way */
        location = move(direction, location);
    }
}
