package de.moeth.pacman;/* Drew Schuster */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Supplier;

/* This class contains the entire game... most of the game logic is in the Board class but this
   creates the gui and captures mouse and keyboard input, as well as controls the game states */
public class Pacman extends JApplet implements KeyListener {

    public static final int DELAY = 10;
    /* These timers are used to kill title, game over, and victory screens after a set idle period (5 seconds)*/
    private long timer = -1;

    /* Create a new board */
    private final Board b;

    /* This timer is used to do request new frames be drawn*/
    private final Timer frameTimer;

    /* This constructor creates the entire game essentially */
    public Pacman() {
        final Supplier<Direction> directionSupplier = () -> Direction.random();
        b = new Board(directionSupplier);
        b.requestFocus();

        /* Create and set up window frame*/
        JFrame f = new JFrame();
        f.setSize(420, 460);

        /* Add the board to the frame */
        f.add(b, BorderLayout.CENTER);

        /*Set listeners for mouse actions and button clicks*/
        b.addKeyListener(this);

        /* Make frame visible, disable resizing */
        f.setVisible(true);
        f.setResizable(false);

        /* Set the New flag to 1 because this is a new game */
        b.New = 1;

        /* Manually call the first frameStep to initialize the game. */
        stepFrame(true);

        /* Create a timer that calls stepFrame every 30 milliseconds */
        frameTimer = new Timer(DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepFrame(false);
            }
        });

        /* Start the timer */
        frameTimer.start();

        b.requestFocus();
    }

    /* This repaint function repaints only the parts of the screen that may have changed.
       Namely the area around every player ghost and the menu bars
    */
    @Override
    public void repaint() {
        if (b.player.teleport) {
            b.repaint(b.player.last.x - Board.GRID_SIZE, b.player.last.y - Board.GRID_SIZE, 80, 80);
            b.player.teleport = false;
        }
        b.repaint(0, 0, 600, Board.GRID_SIZE);
        b.repaint(0, 420, 600, 40);
        b.repaint(b.player.location.x - Board.GRID_SIZE, b.player.location.y - Board.GRID_SIZE, 80, 80);
        b.repaint(b.ghost1.location.x - Board.GRID_SIZE, b.ghost1.location.y - Board.GRID_SIZE, 80, 80);
        b.repaint(b.ghost2.location.x - Board.GRID_SIZE, b.ghost2.location.y - Board.GRID_SIZE, 80, 80);
        b.repaint(b.ghost3.location.x - Board.GRID_SIZE, b.ghost3.location.y - Board.GRID_SIZE, 80, 80);
        b.repaint(b.ghost4.location.x - Board.GRID_SIZE, b.ghost4.location.y - Board.GRID_SIZE, 80, 80);
    }

    /* Steps the screen forward one frame */
    private void stepFrame(boolean New) {
        /* If we aren't on a special screen than the timers can be set to -1 to disable them */
        if (!b.titleScreen && !b.winScreen && !b.overScreen) {
            timer = -1;
        }

        /* If we are playing the dying animation, keep advancing frames until the animation is complete */
        if (b.dying > 0) {
            b.repaint();
            return;
        }

    /* New can either be specified by the New parameter in stepFrame function call or by the state
       of b.New.  Update New accordingly */
        New = New || (b.New != 0);

//        if (b.titleScreen) {
//            b.repaint();
//            return;
//        } else
        if (b.titleScreen || b.winScreen || b.overScreen) {
    /* If this is the win screen or game over screen, make sure to only stay on the screen for 5 seconds.
       If after 5 seconds the user hasn't pressed a key, go to title screen */
            if (timer == -1) {
                timer = System.currentTimeMillis();
            }

            long currTime = System.currentTimeMillis();
            if (currTime - timer >= 500) {
                b.winScreen = false;
                b.overScreen = false;
                b.titleScreen = false;
                timer = -1;
            }
            b.repaint();
            return;
        }


        /* If we have a normal game state, move all pieces and update pellet status */
        if (!New) {
      /* The pacman player has two functions, demoMove if we're in demo mode and move if we're in
         user playable mode.  Call the appropriate one here */
                b.player.move();

            /* Also move the ghosts, and update the pellet states */
            b.ghost1.move();
            b.ghost2.move();
            b.ghost3.move();
            b.ghost4.move();
            b.player.updatePellet();
            b.ghost1.updatePellet();
            b.ghost2.updatePellet();
            b.ghost3.updatePellet();
            b.ghost4.updatePellet();
        }

        /* We either have a new game or the user has died, either way we have to reset the board */
        if (b.stopped || New) {
            /*Temporarily stop advancing frames */
            frameTimer.stop();

            /* If user is dying ... */
            while (b.dying > 0) {
                /* Play dying animation. */
                stepFrame(false);
            }

            /* Move all game elements back to starting positions and orientations */
            b.player.currDirection = Direction.L;
            b.player.direction = Direction.L;
            b.player.desiredDirection = Direction.L;
            b.player.location = Position.of(200, 300);
            b.ghost1.location = Position.of(180, 180);
            b.ghost2.location = Position.of(200, 180);
            b.ghost3.location = Position.of(220, 180);
            b.ghost4.location = Position.of(220, 180);

            /* Advance a frame to display main state*/
            b.repaint(0, 0, 600, 600);

            /*Start advancing frames once again*/
            b.stopped = false;
            frameTimer.start();
        }
        /* Otherwise we're in a normal state, advance one frame*/
        else {
            repaint();
        }
    }

    /* Handles user key presses*/
    @Override
    public void keyPressed(KeyEvent e) {
        /* Pressing a key in the title screen starts a game */
        if (b.titleScreen) {
            b.titleScreen = false;
            return;
        }
        /* Pressing a key in the win screen or game over screen goes to the title screen */
        else if (b.winScreen || b.overScreen) {
            b.titleScreen = true;
            b.winScreen = false;
            b.overScreen = false;
            return;
        }

        /* Otherwise, key presses control the player! */
//        switch (e.getKeyCode()) {
//            case KeyEvent.VK_LEFT:
//                b.player.desiredDirection = Direction.L;
//                break;
//            case KeyEvent.VK_RIGHT:
//                b.player.desiredDirection = Direction.R;
//                break;
//            case KeyEvent.VK_UP:
//                b.player.desiredDirection = Direction.U;
//                break;
//            case KeyEvent.VK_DOWN:
//                b.player.desiredDirection = Direction.D;
//                break;
//        }

        repaint();
    }

    private Direction ki() {
        return Direction.random();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /* Main function simply creates a new pacman instance*/
    public static void main(String[] args) {
        Pacman c = new Pacman();
    }
}
