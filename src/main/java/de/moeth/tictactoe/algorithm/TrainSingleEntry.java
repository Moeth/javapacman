package de.moeth.tictactoe.algorithm;

import de.moeth.tictactoe.Board;
import de.moeth.tictactoe.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

@AllArgsConstructor
@ToString
@Getter
public class TrainSingleEntry {

    private final INDArray state;
    private final int action;
    private final double reward;

    INDArray getRewardChange() {
        INDArray result = Nd4j.zeros(Board.ACTION_SHAPE);
        result.putScalar(action, reward);
        Util.norm(result);
        return result;
    }
}
