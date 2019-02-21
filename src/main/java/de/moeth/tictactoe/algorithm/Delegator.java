package de.moeth.tictactoe.algorithm;

import de.moeth.tictactoe.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Delegator implements KIAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(Delegator.class);
    private final KIAlgorithm kiAlgorithm;

    public Delegator(final KIAlgorithm kiAlgorithm) {
        this.kiAlgorithm = kiAlgorithm;
    }

    public int getBestAction(final Board board, final int playerNumber) {
        return kiAlgorithm.getBestAction(board, playerNumber);
    }

    public void train(final List<TrainSingleEntry> trainData) {
        kiAlgorithm.train(trainData);
    }

    @Override
    public List<TrainSingleEntry> getDataAsTrainingData() {
        return kiAlgorithm.getDataAsTrainingData();
    }

    public void storeData() throws IOException {
        kiAlgorithm.storeData();
    }
}
