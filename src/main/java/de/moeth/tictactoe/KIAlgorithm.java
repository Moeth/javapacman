package de.moeth.tictactoe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.Collection;

public interface KIAlgorithm {

    INDArray getReward(INDArray board);

    //    void changeValue(HistoryEntry historyEntry, double reward);
    void train(Collection<TrainSingleEntry> trainData);

    void storeData() throws IOException;

    @AllArgsConstructor
    @ToString
    @Getter
    class TrainSingleEntry {

        private final INDArray state;
        //        private final INDArray result;
        private final int action;
        private final double reward;

        public INDArray getRewardChange() {
//            Preconditions.checkArgument(reward >= 0);
//            Preconditions.checkArgument(reward <= 1);
//        INDArray labels = train.getResult().dup().putScalar(train.getAction(), train.getReward());
            INDArray result = Nd4j.zeros(Board.ACTION_SHAPE);
            result.putScalar(action, reward);
//            for (int i = 0; i < 9; i++) {
//                result.putScalar(i, i == action ? reward : 1 - reward);
//            }
            Util.norm(result);
            return result;
        }
    }

    @AllArgsConstructor
    @ToString
    @Getter
    class TrainWholeEntry {

        private final INDArray state;
        private final INDArray result;
//        private final int action;
//        private final double reward;
    }
}
