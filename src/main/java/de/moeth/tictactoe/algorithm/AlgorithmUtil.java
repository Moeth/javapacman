package de.moeth.tictactoe.algorithm;

import com.google.common.base.Preconditions;
import de.moeth.tictactoe.Board;
import de.moeth.tictactoe.Util;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.LongStream;

public class AlgorithmUtil {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmUtil.class);

    public static Integer rewardToAction(final Board board, INDArray reward) {
//        INDArray data = board.getBoard(playerNumber);
//        INDArray reward = algorithm.getReward(data);
        Util.assertShape(reward, Board.ACTION_SHAPE);
        Preconditions.checkArgument(reward.rank() == 2);
        int abs = (int) Math.abs(new Random().nextGaussian());
        return board.getPossibleActions()
                .boxed()
                .max(Comparator.comparingDouble(action -> reward.getDouble(action)))
                .orElseThrow(() -> new IllegalArgumentException("asdf"));
    }

    public static long shadeToNodeCount(long[] shape) {
        return LongStream.of(shape).reduce((i, j) -> i * j).orElse(1);
    }
}
