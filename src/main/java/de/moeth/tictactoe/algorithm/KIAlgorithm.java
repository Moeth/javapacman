package de.moeth.tictactoe.algorithm;

import de.moeth.tictactoe.Board;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.IOException;
import java.util.List;

public interface KIAlgorithm {

    int getBestAction(Board board, int playerNumber);

    INDArray getReward(final Board board, final int playerNumber);

    void train(List<TrainSingleEntry> trainSingleEntries);

    void trainWhole(List<TrainWholeEntry> trainData);

    List<TrainWholeEntry> getTrainWholeData();

    void storeData() throws IOException;

    void evaluate(List<TrainWholeEntry> dataAsTrainingData);
}
