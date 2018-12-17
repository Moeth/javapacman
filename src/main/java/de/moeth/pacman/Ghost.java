package de.moeth.pacman;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.moeth.pacman.Board.GRID_SIZE;
import static de.moeth.pacman.Direction.L;

/* Ghost class controls the ghost. */
public class Ghost extends Mover implements Drawable {

    private final Image image;
    private Direction direction;
    private Position pellet;

    public Ghost(Position position, GameMap gameMap, final Image image) {
        super(gameMap);
        this.image = image;
        direction = L;
        pellet = position;
    }

    private Direction newDirection() {
        List<Direction> set = new ArrayList<>();
        set.addAll(Arrays.asList(Direction.values()));
        set.remove(direction.backwards());
        Collections.shuffle(set);
        return set.stream()
                .filter(d -> isValidDirection(d, pellet))
                .findFirst()
                .orElse(direction.backwards());
    }

    /* Random move function for ghost */
    public void move() {
        direction = newDirection();
        pellet = move(direction, pellet);
    }

    public void setLocation(Position location) {
        this.pellet = location;
    }

    public Position getPellet() {
        return pellet;
    }

    @Override
    public void draw(final Graphics g) {
        g.drawImage(image, getPellet().x * GRID_SIZE + Board.GRID_SIZE, getPellet().y * GRID_SIZE + Board.GRID_SIZE, Color.BLACK, null);
    }

    public boolean isOnPosition(final Position p) {
        return getPellet().isOnPosition(p);
    }
}
