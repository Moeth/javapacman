package de.moeth.tictactoe.algorithm;

import de.moeth.tictactoe.Board;

import java.io.IOException;

public interface KIAlgorithm {

    public int getBestAction(final Board board, final int playerNumber);

//    void train(Collection<TrainSingleEntry> trainData);

    public void updateReward(final double reward);

    void storeData() throws IOException;
}
