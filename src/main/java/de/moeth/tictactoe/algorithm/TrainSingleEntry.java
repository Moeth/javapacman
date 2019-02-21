package de.moeth.tictactoe.algorithm;

import de.moeth.tictactoe.Board;
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

    public static TrainSingleEntry convert(INDArray state, INDArray action) {
        int max = -1;
        Double maxV = null;

        for (int i = 0; i < Board.ACTIONS; i++) {
            double v = action.getDouble(i);
            if (maxV == null || maxV < v) {
                maxV = v;
                max = i;
            }
        }
        return new TrainSingleEntry(state, max, maxV);
    }

    INDArray getRewardChange() {
        INDArray result = Nd4j.zeros(Board.ACTION_SHAPE);
        result.putScalar(action, reward);
//        Util.norm(result);
        return result;
    }
}
