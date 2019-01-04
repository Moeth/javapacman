package de.moeth.tictactoe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.nd4j.linalg.api.ndarray.INDArray;

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
