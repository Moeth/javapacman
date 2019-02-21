package de.moeth.tictactoe.algorithm;

import com.google.common.base.Preconditions;
import de.moeth.tictactoe.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class EGreedy extends Delegator {

    private static final Logger log = LoggerFactory.getLogger(EGreedy.class);
    private static final Random RANDOM = new Random();
    private final double start;
    private final double end;
    private final double count;
    private double current = 0;

    public EGreedy(final KIAlgorithm kiAlgorithm, final double start, final double end, final double count) {
        super(kiAlgorithm);
        Preconditions.checkArgument(start >= end);
        Preconditions.checkArgument(count > 0);
        this.start = start;
        this.end = end;
        this.count = count;
    }

    @Override
    public int getBestAction(final Board board, final int playerNumber) {
        double value = start - (start - end) * (current / count);
        if (RANDOM.nextDouble() < value) {
            int bestAction = RANDOM.nextInt(Board.ACTIONS);
            while (!board.isAllowedAction(bestAction)) {
                bestAction = RANDOM.nextInt(Board.ACTIONS);
            }
            Preconditions.checkArgument(board.isAllowedAction(bestAction));
            return bestAction;
        } else {
            int bestAction = super.getBestAction(board, playerNumber);
            return bestAction;
        }
    }

    @Override
    public void train(final List<TrainSingleEntry> trainData) {
        super.train(trainData);
        current++;
    }
}
