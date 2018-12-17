package de.moeth.pacman;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.Encodable;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.moeth.pacman.Board.STATE_COUNT;
import static de.moeth.pacman.Board.SURROUND;

//FROM https://github.com/deeplearning4j/rl4j/blob/master/rl4j-ale/src/main/java/org/deeplearning4j/rl4j/mdp/ale/ALEMDP.java
public class Yeah implements MDP<Encodable, Integer, DiscreteSpace> {

    private static final Logger log = LoggerFactory.getLogger(Yeah.class);

    private final Pacman pacman;
    private final DiscreteSpace discreteSpace;
    private final ObservationSpace<Encodable> observationSpace;
    private static final double scaleFactor = 0.1;

    public Yeah(final Pacman pacman) {
        this.pacman = pacman;
        discreteSpace = new DiscreteSpace(Direction.values().length);
        int[] shape = {(2 * SURROUND + 1) * (2 * SURROUND + 1) * STATE_COUNT};
        observationSpace = new ArrayObservationSpace<>(shape);
    }

    @Override
    public boolean isDone() {
        //TODO
        return pacman.b.numLives < 0;
    }

    @Override
    public Encodable reset() {

        log.info("reset");
        double[] pacmanState = pacman.b.getPacmanState();
        pacman.newGame();
        return () -> pacmanState;
    }

    @Override
    public void close() {
    }

    @Override
    public StepReply<Encodable> step(Integer action) {
        Direction direction = Direction.values()[action];
        Double reward = pacman.doAction(direction);
        double r = reward * scaleFactor;
//        log.info(ale.getEpisodeFrameNumber() + " " + r + " " + action + " ");
        double[] pacmanState = pacman.b.getPacmanState();
        return new StepReply((Encodable) () -> pacmanState, r, isDone(), null);
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
        return new Yeah(pacman);
    }
}
