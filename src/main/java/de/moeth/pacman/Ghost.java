package de.moeth.pacman;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static de.moeth.pacman.Direction.*;

/* Ghost class controls the ghost. */
public class Ghost extends Mover implements Drawable {

    private final Image image;
    /* Direction ghost is heading */
    private Direction direction;
    /* Current ghost location */
    @Deprecated
    private Position location;
    /* The pellet the ghost is on top of */
    private Position pellet;

    /*Constructor places ghost and updates states*/
    public Ghost(Position position, GameMap gameMap, final Image image) {
        super(gameMap);
        this.image = image;
        direction = L;
        pellet = Position.ofGrid(position);
        location = position;
    }

    /* Chooses a new direction randomly for the ghost to move */
    private Direction newDirection() {
        int lookX = location.x;
        int lookY = location.y;
        Direction backwards = direction.backwards();

        Direction newDirection = backwards;
        /* While we still haven't found a valid direction */
        Set<Direction> set = new HashSet<Direction>();
        while (newDirection == backwards || !isValidDest(lookX, lookY)) {
            /* If we've tried every location, turn around and break the loop */
            if (set.size() == 3) {
                newDirection = backwards;
                break;
            }

            lookX = location.x;
            lookY = location.y;

            /* Randomly choose a direction */
            int random = (int) (Math.random() * 4) + 1;
            if (random == 1) {
                newDirection = L;
                lookX -= Board.INCREMENT;
            } else if (random == 2) {
                newDirection = R;
                lookX += Board.GRID_SIZE;
            } else if (random == 3) {
                newDirection = U;
                lookY -= Board.INCREMENT;
            } else if (random == 4) {
                newDirection = D;
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
        /* If we can make a decision, pick a new direction randomly */
        if (location.isGrid()) {
            direction = newDirection();
        }

        /* If that direction is valid, move that way */
        location = move(direction, location);
        pellet = Position.ofGrid(location);
    }

    public Position getLocation() {
        return location;
    }

    public void setLocation(Position location) {
        this.location = location;
    }

    public Position getPellet() {
        return pellet;
    }

    @Override
    public void draw(final Graphics g) {
        g.drawImage(image, getLocation().x, getLocation().y, Color.BLACK, null);
    }
}
