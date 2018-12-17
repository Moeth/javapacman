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

    /* Initialize the player and ghosts */
    public Player player;
    public Ghost ghost1;
    public Ghost ghost2;
    public Ghost ghost3;
    public Ghost ghost4;

    /* Score information */
    int currScore = 0;
    int numLives = 2;
    private final GameMap gameMap = new GameMap();

    /* This is the font used for the menus */
    private final Font font = new Font("Monospaced", Font.BOLD, 12);

    /* Constructor initializes state flags etc.*/
    public Board() {
        newGame();
    }

    void newGame() {
        numLives = 2;
        currScore = 0;
        gameMap.initMap();

        resetPositions();
    }

    private void resetPositions() {
        player = new Player(gameMap);
        ghost1 = new Ghost(Position.of(8, 8), gameMap, ghost10);
        ghost2 = new Ghost(Position.of(9, 8), gameMap, ghost20);
        ghost3 = new Ghost(Position.of(10, 8), gameMap, ghost30);
        ghost4 = new Ghost(Position.of(10, 8), gameMap, ghost40);
    }

    /* Draws one individual pellet.  Used to redraw pellets that ghosts have run over */
    private void fillPellet(int x, int y, Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x * GRID_SIZE + 28, y * GRID_SIZE + 28, INCREMENT, INCREMENT);
    }

    /* This is the main function that draws one entire frame of the game */
    @Override
    public void paint(Graphics g) {
        drawBoard(g);
        drawLives(g);
        drawHighscore(g);

        gameMap.draw(g);

        getGhosts().forEach(ghost -> handlePellet(g, ghost));
        getGhosts().forEach(ghost -> ghost.draw(g));
        player.draw(g);
        /* Draw the border around the game in case it was overwritten by ghost movement or something */
        g.setColor(Color.WHITE);
        g.drawRect(19, 19, 382, 382);
    }

    void eatPellet() {
        /* Eat pellets */
        if (gameMap.getPellet(player.getPellet())) {
            player.incrementPelletsEaten();
            gameMap.eatPellet(player.getPellet());

            currScore += 50;
//            drawHighscore();

            if (player.getPelletsEaten() == 173) {
                /*Demo mode can't get a high score */
//                if (currScore > highScore) {
//                    updateScore(currScore);
//                }
//                winScreen = true;
//                return true;
            }
        }
    }

    void checkCollision() {
        if (detectCollision()) {
            numLives--;
            resetPositions();
        }
    }

    private void drawHighscore(final Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 600, 18);
        g.setColor(Color.YELLOW);
        g.setFont(font);
        g.drawString("Score: " + currScore, GRID_SIZE, 10);
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
