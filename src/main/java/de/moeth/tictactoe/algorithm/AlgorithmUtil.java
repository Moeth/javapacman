package de.moeth.tictactoe.algorithm;

import com.google.common.base.Preconditions;
import de.moeth.tictactoe.Board;
import de.moeth.tictactoe.Util;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class AlgorithmUtil {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmUtil.class);
    public static final Random RANDOM = new Random();

    public static Integer rewardToAction(final Board board, INDArray reward, double explorationRate) {
        List<Integer> possibleActions = getSortedActions(board, reward);

        Preconditions.checkArgument(!possibleActions.isEmpty());
        for (Integer action : possibleActions) {
            if (RANDOM.nextDouble() > explorationRate) {
                return action;
            }
        }
        return possibleActions.get(possibleActions.size() - 1);
    }

    public static Integer rewardToAction(final Board board, INDArray reward) {
        Util.assertShape(reward, Board.ACTION_SHAPE);
        Preconditions.checkArgument(reward.rank() == 2);
        return board.getPossibleActions()
                .boxed()
                .max(Comparator.comparingDouble(reward::getDouble))
                .orElseThrow(() -> new IllegalArgumentException("No Action"));
    }

    public static int getPosition(final Board board, final INDArray reward, final Integer action) {
        List<Integer> possibleActions = getSortedActions(board, reward);
        for (int i = 0; i < Board.ACTIONS; i++) {
            if (possibleActions.get(i).equals(action)) {
                return i;
            }
        }
        return Board.ACTIONS;
    }

    private static List<Integer> getSortedActions(final Board board, final INDArray reward) {
        Util.assertShape(reward, Board.ACTION_SHAPE);
        Preconditions.checkArgument(reward.rank() == 2);
        Comparator<Integer> objectComparator = Comparator.comparingDouble(reward::getDouble);
        return board.getPossibleActions()
                .boxed()
                .sorted(objectComparator.reversed())
                .collect(Collectors.toList());
    }

    public static long shadeToNodeCount(long[] shape) {
        return LongStream.of(shape)
                .reduce((i, j) -> i * j)
                .orElseThrow(() -> new IllegalArgumentException("No Shapes"));
    }
}
