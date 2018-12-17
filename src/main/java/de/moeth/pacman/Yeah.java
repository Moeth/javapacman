package de.moeth.pacman;

import lombok.Value;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.Encodable;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.function.Function;

import static de.moeth.pacman.Board.STATE_COUNT;
import static de.moeth.pacman.Board.SURROUND;

//FROM https://github.com/deeplearning4j/rl4j/blob/master/rl4j-ale/src/main/java/org/deeplearning4j/rl4j/mdp/ale/ALEMDP.java
public class Yeah implements MDP<Encodable, Integer, DiscreteSpace> {

    private final Pacman pacman;
    //    private final int[] actions;
    private final DiscreteSpace discreteSpace;
    private final ObservationSpace<Encodable> observationSpace;
    private final Configuration configuration;
    private final double scaleFactor = 1;

    //    private final byte[] screenBuffer;
    private final Function<Direction, Double> rewardFunction;


    //actions
//    private List<Direction> actions = Arrays.asList(Direction.values());

    public Yeah(final Pacman pacman, final Function<Direction, Double> rewardFunction) {
        this(pacman, new Configuration(123, 0, 0, 0, true), rewardFunction);
    }

    public Yeah(final Pacman pacman, Configuration configuration, final Function<Direction, Double> rewardFunction) {
        this.pacman = pacman;
//        this.romFile = romFile;
        this.configuration = configuration;
        this.rewardFunction = rewardFunction;
//        ale = new ALEInterface();
        setupGame();

        // Get the vector of minimal or legal actions

//        IntPointer a = configuration.minimalActionSet ? ale.getMinimalActionSet() : ale.getLegalActionSet();
//        actions = new int[(int) a.limit()];
//        a.get(actions);

        discreteSpace = new DiscreteSpace(Direction.values().length);
//        int[] shape = {(int) GRID_SIZE, (int) GRID_SIZE, STATE_COUNT};
        int[] shape = {(int) (2 * SURROUND + 1) * (2 * SURROUND + 1) * STATE_COUNT};
        observationSpace = new ArrayObservationSpace<>(shape);
//        screenBuffer = new byte[shape[0] * shape[1] * shape[2]];
    }

//    @Override
//    public Direction get() {
//        return Direction.random();
//    }

//    private void actions() {
//        IntPointer a = configuration.minimalActionSet ? ale.getMinimalActionSet() : ale.getLegalActionSet();
//        actions = new int[(int) a.limit()];
//
//    }

    public void setupGame() {

        // Get & Set the desired settings
//        ale.setInt("random_seed", randomSeed);
//        ale.setFloat("repeat_action_probability", configuration.repeatActionProbability);
//
//        ale.setBool("display_screen", render);
//        ale.setBool("sound", render);
//
//        // Causes episodes to finish after timeout tics
//        ale.setInt("max_num_frames", configuration.maxNumFrames);
//        ale.setInt("max_num_frames_per_episode", configuration.maxNumFramesPerEpisode);
//
//        // Load the ROM file. (Also resets the system for new settings to
//        // take effect.)
//        ale.loadROM(romFile);
    }

    @Override
    public boolean isDone() {
        //TODO
//        return ale.game_over();
        return false;
    }

    @Override
    public Encodable reset() {
//        ale.reset_game();
//        ale.getScreenRGB(screenBuffer);
        double[] pacmanState = pacman.b.getPacmanState();
        //        return new GameScreen(encodable);
        return () -> pacmanState;
//        throw new IllegalArgumentException();
    }

    @Override
    public void close() {
//        ale.deallocate();
    }

    @Override
    public StepReply<Encodable> step(Integer action) {
        Direction direction = Direction.values()[action];
        Double reward = rewardFunction.apply(direction);
        double r = reward * scaleFactor;
//        log.info(ale.getEpisodeFrameNumber() + " " + r + " " + action + " ");
//        ale.getScreenRGB(screenBuffer);
        double[] pacmanState = pacman.b.getPacmanState();
        return new StepReply((Encodable) () -> pacmanState, r, isDone(), null);
    }

    private double getReward(final Integer action) {
        //TODO
//        return ale.act(actions[action]);
        return 0;
    }

    @Override
    public ObservationSpace<Encodable> getObservationSpace() {
        return observationSpace;
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return discreteSpace;
    }

    @Override
    public Yeah newInstance() {
        return new Yeah(pacman, rewardFunction);
    }

    @Value
    public static class Configuration {

        private final int randomSeed;
        private final float repeatActionProbability;
        private final int maxNumFrames;
        private final int maxNumFramesPerEpisode;
        private final boolean minimalActionSet;
//
//        private final int randomSeed = 123;
//        private final float repeatActionProbability = 0;
//        private final int maxNumFrames = 0;
//        private final int maxNumFramesPerEpisode = 0;
//        private final boolean minimalActionSet = true;
    }

    public static class GameScreen implements Encodable {

        private final double[] array;

        public GameScreen(byte[] screen) {

            array = new double[screen.length];
            for (int i = 0; i < screen.length; i++) {
                array[i] = (screen[i] & 0xFF) / 255.0;
            }
        }

        @Override
        public double[] toArray() {
            return array;
        }
    }
}
