package de.moeth.pacman;/* Drew Schuster */

import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning.QLConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense.Configuration;
import org.deeplearning4j.rl4j.util.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.function.Function;

public class Pacman extends JApplet {

    private static final Logger log = LoggerFactory.getLogger(Pacman.class);

    public static QLConfiguration ALE_QL =
            new QLConfiguration(
                    123,      //Random seed
                    10000,    //Max step By epoch
                    8000000,  //Max step
                    1000000,  //Max size of experience replay
                    32,       //size of batches
                    10000,    //target update (hard)
                    500,      //num step noop warmup
                    0.1,      //reward scaling
                    0.99,     //gamma
                    100.0,    //td-error clipping
                    0.1f,     //min epsilon
                    100000,   //num step for eps greedy anneal
                    true      //double-dqn
            );

    final Board b = new Board();

    public Pacman() {
        JFrame f = new JFrame();
        f.setSize(420, 460);

        /* Add the board to the frame */
        f.add(b, BorderLayout.CENTER);
        f.setVisible(true);
        f.setResizable(false);
        stepFrame();
        b.requestFocus();
    }

    private int getKIScore() {
        return b.currScore + b.numLives * 10000;
    }

    /* This repaint function repaints only the parts of the screen that may have changed.
       Namely the area around every player ghost and the menu bars
    */
    @Override
    public void repaint() {
        b.repaint(0, 0, 600, Board.GRID_SIZE);
        b.repaint(0, 420, 600, 40);
        b.repaint(b.player.getDrawX() - Board.GRID_SIZE, b.player.getDrawY() - Board.GRID_SIZE, 80, 80);
    }

    private void stepFrame() {

        b.player.move();

        /* Also move the ghosts, and update the pellet states */
        b.ghost1.move();
        b.ghost2.move();
        b.ghost3.move();
        b.ghost4.move();

        b.checkCollision();
        b.eatPellet();
        repaint();
    }

    private void dying() {
        /* Move all game elements back to starting positions and orientations */
        b.player.reset();
        b.ghost1.setLocation(Position.of(8, 8));
        b.ghost2.setLocation(Position.of(9, 8));
        b.ghost3.setLocation(Position.of(10, 8));
        b.ghost4.setLocation(Position.of(10, 8));

        b.repaint(0, 0, 600, 600);
    }

    /* Main function simply creates a new pacman instance*/
    public static void main(String[] args) throws IOException {
        Pacman c = new Pacman();
        c.train();
    }

    private void train() throws IOException {

        final Function<Direction, Double> rewardFunction = direction -> doAction(direction);

        final Yeah yeah = new Yeah(this);

        DataManager manager = new DataManager(true);

        Configuration dqnFactoryStdDense = Configuration.builder()
//                    .learningRate(0.00025)
                .numHiddenNodes(10)
                .numLayer(1)
                .l2(0.01)
                .listeners(null)
                .updater(null)
                .build();
        QLearningDiscreteDense dql = new QLearningDiscreteDense(yeah, dqnFactoryStdDense, ALE_QL, manager);

        while (true) {
            dql.train();
        }
    }

    public void newGame() {
        b.newGame();
    }

    public Double doAction(Direction direction) {
        final int startScore = getKIScore();
        b.player.desiredDirection = direction;
        stepFrame();
//                    synchronized (this) {
//                        try {
//                            this.wait(30);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
        Double result = Double.valueOf(getKIScore() - startScore - 1);
        log.info("score: " + result + " " + b.player.getPellet());
        return result;
    }
}
