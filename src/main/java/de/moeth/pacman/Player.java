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

    public Direction desiredDirection;

    private int pelletsEaten;
    private Position lastPellet;
    private Position pellet;

    public Player(GameMap gameMap) {
        super(gameMap);
        pelletsEaten = 0;
        reset();
    }

    public void reset() {
        desiredDirection = Direction.L;
        pellet = Position.of(9, 14);
        lastPellet = pellet;
    }

    public void move() {
        lastPellet = pellet;
        if (isValidDirection(desiredDirection, pellet)) {
            pellet = desiredDirection.move(pellet);
        }
    }

    public boolean hitGhost(Ghost ghost) {
        return pellet.isOnPosition(ghost.getPellet()) || lastPellet.isOnPosition(ghost.getPellet());
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

            switch (desiredDirection) {
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
}
