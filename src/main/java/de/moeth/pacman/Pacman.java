package de.moeth.pacman;/* Drew Schuster */

import org.deeplearning4j.rl4j.learning.HistoryProcessor;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdConv;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.util.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.function.Function;

/* This class contains the entire game... most of the game logic is in the Board class but this
   creates the gui and captures mouse and keyboard input, as well as controls the game states */
public class Pacman extends JApplet implements KeyListener {

    private final Logger log = LoggerFactory.getLogger(Pacman.class);
    //    private static final IHistoryProcessor.Configuration ALE_HP = null;
//
    public static HistoryProcessor.Configuration ALE_HP =
            new HistoryProcessor.Configuration(
                    4,       //History length
                    84,      //resize width
                    110,     //resize height
                    84,      //crop width
                    84,      //crop height
                    0,       //cropping x offset
                    0,       //cropping y offset
                    4        //skip mod (one frame is picked every x
            );

    public static QLearning.QLConfiguration ALE_QL =
            new QLearning.QLConfiguration(
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

    public static DQNFactoryStdConv.Configuration ALE_NET_QL =
            DQNFactoryStdConv.Configuration.builder()
                    .learningRate(0.00025)
                    .l2(0.000)
                    .listeners(null)
                    .updater(null)
                    .build();

//            new DQNFactoryStdConv.Configuration(
//                    , //learning rate
//                    ,   //l2 regularization
//                    null, null
//            );

    public static DQNFactoryStdDense.Configuration ALE_NET_QL2 =
            DQNFactoryStdDense.Configuration.builder()
//                    .learningRate(0.00025)
                    .numHiddenNodes(20)
                    .numLayer(2)
                    .l2(0.000)
                    .listeners(null)
                    .updater(null)
                    .build();

//                    (
//                    0.00025, //learning rate
//                    0,
//                    0.000,   //l2 regularization
//                    null, null
//            );


    public static final int DELAY = 10;
    /* These timers are used to kill title, game over, and victory screens after a set idle period (5 seconds)*/
    private long timer = -1;

    /* Create a new board */
    final Board b;

    /* This timer is used to do request new frames be drawn*/
//    private final Timer frameTimer;

    /* This constructor creates the entire game essentially */
    public Pacman() throws IOException {
//        final Supplier<Direction> directionSupplier = () -> Direction.random();
        final Function<Direction, Double> rewardFunction = new Function<Direction, Double>() {
            @Override
            public Double apply(final Direction direction) {
//                Direction direction = directionSupplier.get();
                final int startScore = getKIScore();
                b.player.desiredDirection = direction;
//                stepFrame(false);
//                while (!b.player.getLocation().isGrid()) {
                    stepFrame(false);
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
        };

        final Yeah yeah = new Yeah(this, rewardFunction);
        b = new Board();
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
//        frameTimer = new Timer(DELAY, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                stepFrame(false);
//            }
//        });

//        Direction direction = directionSupplier.get();
//        b.player.desiredDirection = direction;


        /* Start the timer */
//        frameTimer.start();

        b.requestFocus();
        DataManager manager = new DataManager(true);

//        QLearningDiscreteConv dql = new QLearningDiscreteConv(yeah, ALE_NET_QL, ALE_HP, ALE_QL, manager);
        DQNFactoryStdDense.Configuration dqnFactoryStdDense = DQNFactoryStdDense.Configuration.builder()
//                    .learningRate(0.00025)
                .numHiddenNodes(20)
                .numLayer(5)
                .l2(0.000)
                .listeners(null)
                .updater(null)
                .build();
        QLearningDiscreteDense dql = new QLearningDiscreteDense(yeah, dqnFactoryStdDense, ALE_QL, manager);

        //start the training
        dql.train();

        while (true) {
            dql.train();
//            malmoCliffWalk();
//            loadMalmoCliffWalk();
        }
    }

    private int getKIScore() {
        return b.currScore + b.numLives * 10000;
    }

//    public QLearningDiscrete create(MDP<O, Integer, DiscreteSpace> mdp, IDQN dqn, HistoryProcessor.Configuration hpconf,
//                                    QLearning.QLConfiguration conf, DataManager dataManager) {
//        QLearningDiscrete qLearningDiscrete = new QLearningDiscrete(mdp, dqn, conf, dataManager, conf.getEpsilonNbStep() * hpconf.getSkipFrame());
//        qLearningDiscrete.setHistoryProcessor(hpconf);
//        return qLearningDiscrete;
//
//    }
//
//    public QLearningDiscreteConv create(MDP<O, Integer, DiscreteSpace> mdp, DQNFactory factory,
//                                        HistoryProcessor.Configuration hpconf, QLearning.QLConfiguration conf, DataManager dataManager) {
//        create(mdp, factory.buildDQN(hpconf.getShape(), mdp.getActionSpace().getSize()), hpconf, conf, dataManager);
//    }
//
//    public QLearningDiscreteConv create(MDP<O, Integer, DiscreteSpace> mdp, DQNFactoryStdConv.Configuration netConf,
//                                        HistoryProcessor.Configuration hpconf, QLearning.QLConfiguration conf, DataManager dataManager) {
//        create(mdp, new DQNFactoryStdConv(netConf), hpconf, conf, dataManager);
//    }

    /* This repaint function repaints only the parts of the screen that may have changed.
       Namely the area around every player ghost and the menu bars
    */
    @Override
    public void repaint() {
        b.repaint(0, 0, 600, Board.GRID_SIZE);
        b.repaint(0, 420, 600, 40);
        b.repaint(b.player.getDrawX() - Board.GRID_SIZE, b.player.getDrawY() - Board.GRID_SIZE, 80, 80);
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
//        New = New || (b.New != 0);

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
//        if (!New) {
      /* The pacman player has two functions, demoMove if we're in demo mode and move if we're in
         user playable mode.  Call the appropriate one here */
            b.player.move();

            /* Also move the ghosts, and update the pellet states */
            b.ghost1.move();
            b.ghost2.move();
            b.ghost3.move();
            b.ghost4.move();
            b.player.updatePellet();
//        }

        /* We either have a new game or the user has died, either way we have to reset the board */
//        if (New) {
//            /*Temporarily stop advancing frames */
//            frameTimer.stop();
//            dying();
//            frameTimer.start();
//        } else {
            /* Otherwise we're in a normal state, advance one frame*/
            repaint();
//        }
    }

    private void dying() {
        /* Move all game elements back to starting positions and orientations */
        b.player.reset();
        b.ghost1.setLocation(Position.of(8, 8));
        b.ghost2.setLocation(Position.of(9, 8));
        b.ghost3.setLocation(Position.of(10, 8));
        b.ghost4.setLocation(Position.of(10, 8));

        /* Advance a frame to display main state*/
        b.repaint(0, 0, 600, 600);
    }

    /* Handles user key presses*/
    @Override
    public void keyPressed(KeyEvent e) {
        /* Pressing a key in the title screen starts a game */
        if (b.titleScreen) {
            b.titleScreen = false;
            return;
        } else if (b.winScreen || b.overScreen) {
            /* Pressing a key in the win screen or game over screen goes to the title screen */
            b.titleScreen = true;
            b.winScreen = false;
            b.overScreen = false;
            return;
        }

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
    public static void main(String[] args) throws IOException {
        Pacman c = new Pacman();
    }
}
