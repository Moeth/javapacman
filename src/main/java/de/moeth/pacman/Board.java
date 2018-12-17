package de.moeth.pacman;/* Drew Schuster */

import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

import static de.moeth.pacman.GameMap.GRID_WIDTH;

/*This board class contains the player, ghosts, pellets, and most of the game logic.*/
class Board extends JPanel {

    public static final int GRID_SIZE = 20;
    public static final int MAX = 400;
    public static final int INCREMENT = 4;
    public static final int STATE_COUNT = 3;
    public static final int SURROUND = 5;
    /* Initialize the images*/
    private final Image ghost10 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost10.jpg"));
    private final Image ghost20 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost20.jpg"));
    private final Image ghost30 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost30.jpg"));
    private final Image ghost40 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost40.jpg"));
    //    private final Image ghost11 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost11.jpg"));
//    private final Image ghost21 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost21.jpg"));
//    private final Image ghost31 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost31.jpg"));
//    private final Image ghost41 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost41.jpg"));
    private final Image titleScreenImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/titleScreen.jpg"));
    private final Image gameOverImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/gameOver.jpg"));
    private final Image winScreenImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/winScreen.jpg"));

    /* Initialize the player and ghosts */
    public Player player;
    public Ghost ghost1;
    public Ghost ghost2;
    public Ghost ghost3;
    public Ghost ghost4;

    /* Timer is used for playing sound effects and animations */
    private long timer = System.currentTimeMillis();
    /* Dying is used to count frames in the dying animation.  If it's non-zero,
       pacman is in the process of dying */
    public int dying = 0;
    /* Score information */
    int currScore = 0;
    int numLives = 2;
    private final GameMap gameMap = new GameMap();

    /* State flags*/
    public boolean titleScreen = true;
    public boolean winScreen = false;
    public boolean overScreen = false;
    public int New = 0;

//    private final Supplier<Direction> directionSupplier;

    /* This is the font used for the menus */
    private final Font font = new Font("Monospaced", Font.BOLD, 12);

    /* Constructor initializes state flags etc.*/
    public Board() {
//        this.directionSupplier = directionSupplier;
        newGame();
    }

    private void newGame() {
        numLives = 2;
        currScore = 0;
        gameMap.initMap();

        resetPositions();
    }

    private void resetPositions() {
        player = new Player(gameMap);
        ghost1 = new Ghost(Position.of(9, 9), gameMap, ghost10);
        ghost2 = new Ghost(Position.of(10, 9), gameMap, ghost20);
        ghost3 = new Ghost(Position.of(11, 9), gameMap, ghost30);
        ghost4 = new Ghost(Position.of(11, 9), gameMap, ghost40);
    }

    /* Draws one individual pellet.  Used to redraw pellets that ghosts have run over */
    private void fillPellet(int x, int y, Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x * GRID_SIZE + 28, y * GRID_SIZE + 28, INCREMENT, INCREMENT);
    }

    /* This is the main function that draws one entire frame of the game */
    @Override
    public void paint(Graphics g) {
    /* If we're playing the dying animation, don't update the entire screen.
       Just kill the pacman*/
        if (dying > 0) {
            handleDying(g);
            return;
        }

        if (titleScreen) {
            /* If this is the title screen, draw the title screen and return */
            fillImage(g, titleScreenImage);
            New = 1;
            return;
        } else if (winScreen) {
            /* If this is the win screen, draw the win screen and return */
            fillImage(g, winScreenImage);
            New = 1;
            return;
        } else if (overScreen) {
            /* If this is the game over screen, draw the game over screen and return */
            fillImage(g, gameOverImage);
            New = 1;
            return;
        }

        /* Game initialization */
        if (New == 1) {
            newGame();
            New++;
        }

        drawBoard(g);
        drawLives(g);
        drawHighscore(g);

        gameMap.draw(g);

        /* Kill the pacman */
        if (detectCollision()) {
            /* 4 frames of death*/
            dying = 4;
            /*Decrement lives, update screen to reflect that.  And set appropriate flags and timers */
            numLives--;
            drawLives(g);
            resetPositions();
            timer = System.currentTimeMillis();
        }

        /* Delete the players and ghosts */
        g.setColor(Color.BLACK);

        /* Eat pellets */
        if (gameMap.getPellet(player.getPellet())) {
            player.incrementPelletsEaten();
            gameMap.eatPellet(player.getPellet());

            /* Increment the score */
            currScore += 50;
            drawHighscore(g);

            /* If this was the last pellet */
            if (player.getPelletsEaten() == 173) {
                /*Demo mode can't get a high score */
//                if (currScore > highScore) {
//                    updateScore(currScore);
//                }
                winScreen = true;
                return;
            }
        }

        getGhosts().forEach(ghost -> handlePellet(g, ghost));
        getGhosts().forEach(ghost -> ghost.draw(g));
        player.draw(g);
        /* Draw the border around the game in case it was overwritten by ghost movement or something */
        g.setColor(Color.WHITE);
        g.drawRect(19, 19, 382, 382);
    }

    private void drawHighscore(final Graphics g) {
        /* Update the screen to reflect the new score */
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 600, 18);
        g.setColor(Color.YELLOW);
        g.setFont(font);
        g.drawString("Score: " + currScore, GRID_SIZE, 10);
    }

    private void handleDying(final Graphics g) {
        /* Draw the pacman */
        g.drawImage(player.pacmanImage, player.getDrawX(), player.getDrawY(), Color.BLACK, null);
        g.setColor(Color.BLACK);

        /* Kill the pacman */
        if (dying == 4) {
            g.fillRect(player.getDrawX(), player.getDrawY(), GRID_SIZE, 7);
        } else if (dying == 3) {
            g.fillRect(player.getDrawX(), player.getDrawY(), GRID_SIZE, 14);
        } else if (dying == 2) {
            g.fillRect(player.getDrawX(), player.getDrawY(), GRID_SIZE, GRID_SIZE);
        } else if (dying == 1) {
            g.fillRect(player.getDrawX(), player.getDrawY(), GRID_SIZE, GRID_SIZE);
        }

      /* Take .1 seconds on each frame of death, and then take 2 seconds
         for the final frame to allow for the sound effect to end */
        long currTime = System.currentTimeMillis();
        long temp = dying == 1 ? 2000 : 100;
        /* If it's time to draw a new death frame... */
        if (currTime - timer >= temp) {
            dying--;
            timer = currTime;
            /* If this was the last death frame...*/
            if (dying == 0) {
                if (numLives == -1) {
                    /* Game over for player.  If relevant, update high score.  Set gameOver flag*/
//                    if (currScore > highScore) {
//                        updateScore(currScore);
//                    }
                    overScreen = true;
                }
            }
        }
    }

    private void fillImage(final Graphics g, final Image image) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 600, 600);
        g.drawImage(image, 0, 0, Color.BLACK, null);
    }

    private void handlePellet(final Graphics g, final Ghost ghost) {
        if (gameMap.getPellet(ghost.getPellet())) {
            fillPellet(ghost.getPellet().x, ghost.getPellet().y, g);
        }
    }

    private void updateScore(final int currScore) {
        //TODO
    }

    private boolean detectCollision() {
        /* Detect collisions */
        return getGhosts()
                .anyMatch(g -> player.hitGhost(g));
    }

    private Stream<Ghost> getGhosts() {
        return Stream.of(ghost1, ghost2, ghost3, ghost4);
    }

    double[] getPacmanState() {
        final double[] result = new double[(2 * SURROUND + 1) * (2 * SURROUND + 1) * STATE_COUNT];
        int i = 0;

        for (int x = player.getPellet().x - SURROUND; x <= player.getPellet().x + SURROUND; x++) {
            for (int y = player.getPellet().y - SURROUND; y <= player.getPellet().y + SURROUND; y++) {
                if (x <= 0 && x < GRID_WIDTH && y <= 0 && y < GRID_WIDTH) {
                    Position p = Position.of(x, y);
                    boolean valid = p.between(0, GRID_WIDTH);
                    result[i++] = valid && gameMap.getState(p) ? 1 : 0;
                    result[i++] = valid && gameMap.getPellet(p) ? 1 : 0;
                    result[i++] = valid && getGhost(p) ? 1 : 0;
//                result[i++] = valid && player.isOnPosition(p) ? 1 : 0;
                } else {
                    i += 3;
                }
            }
        }

        return result;
    }

//    private boolean isValid() {
//
//    }

    private boolean getGhost(final Position p) {
        return getGhosts()
                .anyMatch(g -> g.isOnPosition(p));
    }

    /* Draws the appropriate number of lives on the bottom left of the screen.
       Also draws the menu */
    private void drawLives(Graphics g) {
        g.setColor(Color.BLACK);

        /*Clear the bottom bar*/
        g.fillRect(0, MAX + 5, 600, GRID_SIZE);
        g.setColor(Color.YELLOW);
        for (int i = 0; i < numLives; i++) {
            /*Draw each life */
            g.fillOval(GRID_SIZE * (i + 1), MAX + 5, GRID_SIZE, GRID_SIZE);
        }
        /* Draw the menu items */
        g.setColor(Color.YELLOW);
        g.setFont(font);
        g.drawString("Reset", 100, MAX + 5 + GRID_SIZE);
        g.drawString("Clear High Scores", 180, MAX + 5 + GRID_SIZE);
        g.drawString("Exit", 350, MAX + 5 + GRID_SIZE);
    }

    /*  This function draws the board.  The pacman board is really complicated and can only feasibly be done
        manually.  Whenever I draw a wall, I call updateMap to invalidate those coordinates.  This way the pacman
        and ghosts know that they can't traverse this area */
    private void drawBoard(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 600, 600);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 420, 420);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GRID_SIZE, 600);
        g.fillRect(0, 0, 600, GRID_SIZE);
        g.setColor(Color.WHITE);
        g.drawRect(19, 19, 382, 382);
        g.setColor(Color.BLUE);
        drawLives(g);
    }
}
