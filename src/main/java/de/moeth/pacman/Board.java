package de.moeth.pacman;/* Drew Schuster */

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

/*This board class contains the player, ghosts, pellets, and most of the game logic.*/
class Board extends JPanel {

    public static final int GRID_SIZE = 20;
    public static final int MAX = 400;
    public static final int INCREMENT = 4;
    /* Initialize the images*/
    private final Image pacmanImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacman.jpg"));
    private final Image pacmanUpImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacmanup.jpg"));
    private final Image pacmanDownImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacmandown.jpg"));
    private final Image pacmanLeftImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacmanleft.jpg"));
    private final Image pacmanRightImage = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/pacmanright.jpg"));
    private final Image ghost10 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost10.jpg"));
    private final Image ghost20 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost20.jpg"));
    private final Image ghost30 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost30.jpg"));
    private final Image ghost40 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost40.jpg"));
    private final Image ghost11 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost11.jpg"));
    private final Image ghost21 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost21.jpg"));
    private final Image ghost31 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost31.jpg"));
    private final Image ghost41 = Toolkit.getDefaultToolkit().getImage(Board.class.getResource("/img/ghost41.jpg"));
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
    private int currScore = 0;
    private int highScore;
    /* if the high scores have been cleared, we have to update the top of the screen to reflect that */
    private boolean clearHighScores = false;
    private int numLives = 2;
    private final GameMap gameMap = new GameMap();

    /* State flags*/
    public boolean stopped = false;
    public boolean titleScreen = true;
    public boolean winScreen = false;
    public boolean overScreen = false;
    public int New = 0;

    private Position lastPelletEaten = Position.of(0, 0);
    private final Supplier<Direction> directionSupplier;

    /* This is the font used for the menus */
    private final Font font = new Font("Monospaced", Font.BOLD, 12);

    /* Constructor initializes state flags etc.*/
    public Board(final Supplier<Direction> directionSupplier) {
        this.directionSupplier = directionSupplier;
        init();
    }

    private void init() {
        player = new Player(Position.of(200, 300), gameMap, directionSupplier);
        ghost1 = new Ghost(Position.of(180, 180), gameMap);
        ghost2 = new Ghost(Position.of(200, 180), gameMap);
        ghost3 = new Ghost(Position.of(220, 180), gameMap);
        ghost4 = new Ghost(Position.of(220, 180), gameMap);
    }

    /* Reset occurs on a new game*/
    private void reset() {
        numLives = 2;
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

        fooo(g, 40, 40, 60, GRID_SIZE);
        fooo(g, 120, 40, 60, GRID_SIZE);
        fooo(g, 200, GRID_SIZE, GRID_SIZE, 40);
        fooo(g, 240, 40, 60, GRID_SIZE);
        fooo(g, 320, 40, 60, GRID_SIZE);
        fooo(g, 40, 80, 60, GRID_SIZE);
        fooo(g, 160, 80, 100, GRID_SIZE);
        fooo(g, 200, 80, GRID_SIZE, 60);
        fooo(g, 320, 80, 60, GRID_SIZE);

        fooo(g, GRID_SIZE, 120, 80, 60);
        fooo(g, 320, 120, 80, 60);
        fooo(g, GRID_SIZE, 200, 80, 60);
        fooo(g, 320, 200, 80, 60);

        fooo(g, 160, 160, 40, GRID_SIZE);
        fooo(g, 220, 160, 40, GRID_SIZE);
        fooo(g, 160, 180, GRID_SIZE, GRID_SIZE);
        fooo(g, 160, 200, 100, GRID_SIZE);
        fooo(g, 240, 180, GRID_SIZE, GRID_SIZE);
        g.setColor(Color.BLUE);

        fooo(g, 120, 120, 60, GRID_SIZE);
        fooo(g, 120, 80, GRID_SIZE, 100);
        fooo(g, 280, 80, GRID_SIZE, 100);
        fooo(g, 240, 120, 60, GRID_SIZE);

        fooo(g, 280, 200, GRID_SIZE, 60);
        fooo(g, 120, 200, GRID_SIZE, 60);
        fooo(g, 160, 240, 100, GRID_SIZE);
        fooo(g, 200, 260, GRID_SIZE, 40);

        fooo(g, 120, 280, 60, GRID_SIZE);
        fooo(g, 240, 280, 60, GRID_SIZE);

        fooo(g, 40, 280, 60, GRID_SIZE);
        fooo(g, 80, 280, GRID_SIZE, 60);
        fooo(g, 320, 280, 60, GRID_SIZE);
        fooo(g, 320, 280, GRID_SIZE, 60);

        fooo(g, GRID_SIZE, 320, 40, GRID_SIZE);
        fooo(g, 360, 320, 40, GRID_SIZE);
        fooo(g, 160, 320, 100, GRID_SIZE);
        fooo(g, 200, 320, GRID_SIZE, 60);

        fooo(g, 40, 360, 140, GRID_SIZE);
        fooo(g, 240, 360, 140, GRID_SIZE);
        fooo(g, 280, 320, GRID_SIZE, 40);
        fooo(g, 120, 320, GRID_SIZE, 60);
        drawLives(g);
    }

    private void fooo(Graphics g, final int x, final int y, final int width, final int height) {
        g.fillRect(x, y, width, height);
        gameMap.updateMap(x, y, width, height);
    }

    /* Draws the pellets on the screen */
    private void drawPellets(Graphics g) {
        g.setColor(Color.YELLOW);
        for (int i = 1; i < GRID_SIZE; i++) {
            for (int j = 1; j < GRID_SIZE; j++) {
                if (gameMap.getPellet(i - 1, j - 1)) {
                    g.fillOval(i * GRID_SIZE + 8, j * GRID_SIZE + 8, INCREMENT, INCREMENT);
                }
            }
        }
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

        /* If need to update the high scores, redraw the top menu bar */
        if (clearHighScores) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 600, 18);
            g.setColor(Color.YELLOW);
            g.setFont(font);
            clearHighScores = false;
            g.drawString("Score: " + currScore + "\t High Score: " + highScore, GRID_SIZE, 10);
        }

        /* oops is set to true when pacman has lost a life */

        /* Game initialization */
        if (New == 1) {
            reset();
            init();
            currScore = 0;
            drawBoard(g);
            drawPellets(g);
            drawLives(g);

            /* Draw the top menu bar*/
            g.setColor(Color.YELLOW);
            g.setFont(font);
            g.drawString("Score: " + currScore + "\t High Score: " + highScore, GRID_SIZE, 10);
            New++;
        } else if (New == 2) {
            /* Second frame of new game */
            New++;
        } else if (New == 3) {
            /* Third frame of new game */
            New++;
            timer = System.currentTimeMillis();
            return;
        } else if (New == 4) {
            /* Fourth frame of new game */
            New = 0;
        }

        /* Drawing optimization */
        g.copyArea(player.location.x - GRID_SIZE, player.location.y - GRID_SIZE, 80, 80, 0, 0);
        g.copyArea(ghost1.location.x - GRID_SIZE, ghost1.location.y - GRID_SIZE, 80, 80, 0, 0);
        g.copyArea(ghost2.location.x - GRID_SIZE, ghost2.location.y - GRID_SIZE, 80, 80, 0, 0);
        g.copyArea(ghost3.location.x - GRID_SIZE, ghost3.location.y - GRID_SIZE, 80, 80, 0, 0);
        g.copyArea(ghost4.location.x - GRID_SIZE, ghost4.location.y - GRID_SIZE, 80, 80, 0, 0);
        boolean oops = detectCollision();

        /* Kill the pacman */
        if (oops && !stopped) {
            /* 4 frames of death*/
            dying = 4;

            /*Decrement lives, update screen to reflect that.  And set appropriate flags and timers */
            numLives--;
            stopped = true;
            drawLives(g);
            timer = System.currentTimeMillis();
        }

        /* Delete the players and ghosts */
        g.setColor(Color.BLACK);
        g.fillRect(player.last.x, player.last.y, GRID_SIZE, GRID_SIZE);
        g.fillRect(ghost1.last.x, ghost1.last.y, GRID_SIZE, GRID_SIZE);
        g.fillRect(ghost2.last.x, ghost2.last.y, GRID_SIZE, GRID_SIZE);
        g.fillRect(ghost3.last.x, ghost3.last.y, GRID_SIZE, GRID_SIZE);
        g.fillRect(ghost4.last.x, ghost4.last.y, GRID_SIZE, GRID_SIZE);

        /* Eat pellets */
        if (gameMap.getPellet(player.pellet) && New != 2 && New != 3) {
            lastPelletEaten = player.pellet;
            /* Increment pellets eaten value to track for end game */
            player.pelletsEaten++;

            /* Delete the pellet*/
            gameMap.eatPellet(player.pellet);

            /* Increment the score */
            currScore += 50;

            /* Update the screen to reflect the new score */
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 600, GRID_SIZE);
            g.setColor(Color.YELLOW);
            g.setFont(font);
            g.drawString("Score: " + currScore + "\t High Score: " + highScore, GRID_SIZE, 10);

            /* If this was the last pellet */
            if (player.pelletsEaten == 173) {
                /*Demo mode can't get a high score */
                if (currScore > highScore) {
                    updateScore(currScore);
                }
                winScreen = true;
                return;
            }
        }

        /* Replace pellets that have been run over by ghosts */
        handlePellet(g, ghost1);
        handlePellet(g, ghost2);
        handlePellet(g, ghost3);
        handlePellet(g, ghost4);


        /*Draw the ghosts */
        if (ghost1.frameCount < 5) {
            /* Draw first frame of ghosts */
            g.drawImage(ghost10, ghost1.location.x, ghost1.location.y, Color.BLACK, null);
            g.drawImage(ghost20, ghost2.location.x, ghost2.location.y, Color.BLACK, null);
            g.drawImage(ghost30, ghost3.location.x, ghost3.location.y, Color.BLACK, null);
            g.drawImage(ghost40, ghost4.location.x, ghost4.location.y, Color.BLACK, null);
            ghost1.frameCount++;
        } else {
            /* Draw second frame of ghosts */
            g.drawImage(ghost11, ghost1.location.x, ghost1.location.y, Color.BLACK, null);
            g.drawImage(ghost21, ghost2.location.x, ghost2.location.y, Color.BLACK, null);
            g.drawImage(ghost31, ghost3.location.x, ghost3.location.y, Color.BLACK, null);
            g.drawImage(ghost41, ghost4.location.x, ghost4.location.y, Color.BLACK, null);
            if (ghost1.frameCount >= 10) {
                ghost1.frameCount = 0;
            } else {
                ghost1.frameCount++;
            }
        }

        /* Draw the pacman */
        if (player.frameCount < 5) {
            /* Draw mouth closed */
            g.drawImage(pacmanImage, player.location.x, player.location.y, Color.BLACK, null);
        } else {
            /* Draw mouth open in appropriate direction */
            if (player.frameCount >= 10) {
                player.frameCount = 0;
            }

            switch (player.currDirection) {
                case L:
                    g.drawImage(pacmanLeftImage, player.location.x, player.location.y, Color.BLACK, null);
                    break;
                case R:
                    g.drawImage(pacmanRightImage, player.location.x, player.location.y, Color.BLACK, null);
                    break;
                case U:
                    g.drawImage(pacmanUpImage, player.location.x, player.location.y, Color.BLACK, null);
                    break;
                case D:
                    g.drawImage(pacmanDownImage, player.location.x, player.location.y, Color.BLACK, null);
                    break;
            }
        }

        /* Draw the border around the game in case it was overwritten by ghost movement or something */
        g.setColor(Color.WHITE);
        g.drawRect(19, 19, 382, 382);
    }

    private void handleDying(final Graphics g) {
        /* Draw the pacman */
        g.drawImage(pacmanImage, player.location.x, player.location.y, Color.BLACK, null);
        g.setColor(Color.BLACK);

        /* Kill the pacman */
        if (dying == 4) {
            g.fillRect(player.location.x, player.location.y, GRID_SIZE, 7);
        } else if (dying == 3) {
            g.fillRect(player.location.x, player.location.y, GRID_SIZE, 14);
        } else if (dying == 2) {
            g.fillRect(player.location.x, player.location.y, GRID_SIZE, GRID_SIZE);
        } else if (dying == 1) {
            g.fillRect(player.location.x, player.location.y, GRID_SIZE, GRID_SIZE);
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
                    if (currScore > highScore) {
                        updateScore(currScore);
                    }
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
        if (gameMap.getPellet(ghost.lastPellet)) {
            fillPellet(ghost.lastPellet.x, ghost.lastPellet.y, g);
        }
    }

    private void updateScore(final int currScore) {
        //TODO
    }

    private boolean detectCollision() {
        /* Detect collisions */
        return Stream.of(ghost1, ghost2, ghost3, ghost4)
                .anyMatch(g -> player.hitGhost3(g));
    }
}
