package de.moeth.tictactoe.algorithm;

import de.moeth.tictactoe.Board;

import java.io.IOException;
import java.util.List;

public interface KIAlgorithm {

    public int getBestAction(final Board board, final int playerNumber);

    void train(List<TrainSingleEntry> trainData);

//    public void updateReward(final double reward);

    public List<TrainSingleEntry> getDataAsTrainingData();

    void storeData() throws IOException;
}
