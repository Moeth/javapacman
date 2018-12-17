package de.moeth.pacman;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/* This is the pacman object */
public class Player extends Mover implements Drawable {

    private final static Logger log = LoggerFactory.getLogger(Player.class);
    final Image pacmanImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacman.jpg"));
    private final Image pacmanUpImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacmanup.jpg"));
    private final Image pacmanDownImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacmandown.jpg"));
    private final Image pacmanLeftImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacmanleft.jpg"));
    private final Image pacmanRightImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacmanright.jpg"));


    /* Direction is used in demoMode, currDirection and desiredDirection are used in non demoMode*/
    public Direction direction;
    public Direction currDirection;
    public Direction desiredDirection;

    private int pelletsEaten;
    //    private Position location;
    private Position pellet;

    public Player(GameMap gameMap) {
        super(gameMap);
        pelletsEaten = 0;
//        pellet = Position.ofGrid(position);
//        currDirection = Direction.L;
//        desiredDirection = Direction.L;
        reset();
    }

    public void reset() {
        currDirection = Direction.L;
        direction = Direction.L;
        desiredDirection = Direction.L;
        pellet = Position.of(9, 14);
    }

    public void move() {
//        final Position last = location;

        /* Try to turn in the direction input by the user */
        /*Can only turn if we're in center of a grid*/
        /* Or if we're reversing*/
//        if (location.isGrid()) {
//            desiredDirection = directionSupplier.get();
//        }

        if (isValidDirection(desiredDirection, pellet)) {
            pellet = desiredDirection.move(pellet);
//                    return;
                }

//        log.info("move to: " + getPellet());
//            }

//            pellet = move(desiredDirection, pellet);
//        }
//        /* If we haven't moved, then move in the direction the pacman was headed anyway */
//                switch (currDirection) {
//                    case L:
//                        if (pellet.y == 9 * Board.GRID_SIZE && location.x < 2 * Board.GRID_SIZE) {
//                            pellet = location.setX(Board.MAX - Board.GRID_SIZE * 1);
//                        }
//                        break;
//                    case R:
//                        if (pellet.y == 9 * Board.GRID_SIZE && pellet.x > Board.MAX - Board.GRID_SIZE * 2) {
//                            pellet = pellet.setX(1 * Board.GRID_SIZE);
//                        }
//                        break;
//                }


    }

    /* Update what pellet the pacman is on top of */
    public void updatePellet() {

    }

    public boolean hitGhost(Ghost ghost) {
        return isOnPosition(ghost.getPellet());
    }

    public int getPelletsEaten() {
        return pelletsEaten;
    }

    public void incrementPelletsEaten() {
        pelletsEaten += 1;
    }

    public Position getPellet() {
        return pellet;
    }

    public int getDrawX() {
        return getPellet().x * Board.GRID_SIZE + Board.GRID_SIZE;
    }

    public int getDrawY() {
        return getPellet().y * Board.GRID_SIZE + Board.GRID_SIZE;
    }

    @Override
    public void draw(final Graphics g) {

        g.drawImage(getPacmanImage(), getDrawX(), getDrawY(), Color.BLACK, null);
    }

    private Image getPacmanImage() {

        if (frameCount < 5) {
            /* Draw mouth closed */
            return pacmanImage;
        } else {
            /* Draw mouth open in appropriate direction */
            if (frameCount >= 10) {
                frameCount = 0;
            }

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
    }

    public boolean isOnPosition(final Position p) {
        return getPellet().isOnPosition(p);
    }
}
