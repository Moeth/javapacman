package de.moeth.pacman;

import java.awt.*;

/* This is the pacman object */
public class Player extends Mover implements Drawable {

    final Image pacmanImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacman.jpg"));
    private final Image pacmanUpImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacmanup.jpg"));
    private final Image pacmanDownImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacmandown.jpg"));
    private final Image pacmanLeftImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacmanleft.jpg"));
    private final Image pacmanRightImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacmanright.jpg"));


    /* Direction is used in demoMode, currDirection and desiredDirection are used in non demoMode*/
    public Direction direction;
    public Direction currDirection;
    public Direction desiredDirection;

    /* Keeps track of pellets eaten to determine end of game */
    private int pelletsEaten;
    /* Current location */
    private Position location;
    /* Which pellet the pacman is on top of */
    private Position pellet;
//    private final Supplier<Direction> directionSupplier;

    /* Constructor places pacman in initial location and orientation */
    public Player(Position position, GameMap gameMap) {
        super(gameMap);
//        this.directionSupplier = directionSupplier;
        pelletsEaten = 0;
        pellet = Position.ofGrid(position);
        location = position;
        currDirection = Direction.L;
        desiredDirection = Direction.L;
    }

    public void reset() {
        currDirection = Direction.L;
        direction = Direction.L;
        desiredDirection = Direction.L;
        this.location = Position.of(200, 300);
    }

    /* The move function moves the pacman for one frame in non demo mode */
    public void move() {
        final Position last = location;

        /* Try to turn in the direction input by the user */
        /*Can only turn if we're in center of a grid*/
        /* Or if we're reversing*/
//        if (location.isGrid()) {
//            desiredDirection = directionSupplier.get();
//        }
        if (location.isGrid() || Direction.isOpposite(desiredDirection, currDirection)) {
            location = move(desiredDirection, location);
        }
        /* If we haven't moved, then move in the direction the pacman was headed anyway */
        if (Position.isEqualll(last, location)) {
            if (isValidDirection(currDirection, location)) {
                location = currDirection.move(location, Board.INCREMENT);
            } else {

                switch (currDirection) {
                    case L:
                        if (location.y == 9 * Board.GRID_SIZE && location.x < 2 * Board.GRID_SIZE) {
                            location = location.setX(Board.MAX - Board.GRID_SIZE * 1);
                        }
                        break;
                    case R:
                        if (location.y == 9 * Board.GRID_SIZE && location.x > Board.MAX - Board.GRID_SIZE * 2) {
                            location = location.setX(1 * Board.GRID_SIZE);
                        }
                        break;
                }
            }
        }

        /* If we did change direction, update currDirection to reflect that */
        else {
            currDirection = desiredDirection;
        }
    }

    /* Update what pellet the pacman is on top of */
    public void updatePellet() {
        if (location.isGrid()) {
            pellet = Position.ofGrid(location);
        }
    }

    public boolean hitGhost(Ghost ghost) {
        return location.x == ghost.getLocation().x && Math.abs(location.y - ghost.getLocation().y) < 10
                || location.y == ghost.getLocation().y && Math.abs(location.x - ghost.getLocation().x) < 10;
    }

    public int getPelletsEaten() {
        return pelletsEaten;
    }

    public void incrementPelletsEaten() {
        this.pelletsEaten += 1;
    }

    public Position getLocation() {
        return location;
    }

    public Position getPellet() {
        return pellet;
    }

    @Override
    public void draw(final Graphics g) {
        /* Draw the pacman */
        if (frameCount < 5) {
            /* Draw mouth closed */
            g.drawImage(pacmanImage, getLocation().x, getLocation().y, Color.BLACK, null);
        } else {
            /* Draw mouth open in appropriate direction */
            if (frameCount >= 10) {
                frameCount = 0;
            }

            g.drawImage(getPacmanImage(), getLocation().x, getLocation().y, Color.BLACK, null);
        }
    }

    private Image getPacmanImage() {

        switch (currDirection) {
            case L:
                return pacmanLeftImage;
            case R:
                return pacmanRightImage;
            case U:
                return pacmanUpImage;
            case D:
                return pacmanDownImage;
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean isOnPosition(final Position p) {
        return getPellet().isOnPosition(p);
    }
}
