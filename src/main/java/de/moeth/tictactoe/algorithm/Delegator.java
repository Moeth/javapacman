package de.moeth.tictactoe.algorithm;

import de.moeth.tictactoe.Board;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Delegator extends AbstractKIAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(Delegator.class);
    private final KIAlgorithm kiAlgorithm;

    public Delegator(final KIAlgorithm kiAlgorithm) {
        this.kiAlgorithm = kiAlgorithm;
    }

    public int getBestAction(final Board board, final int playerNumber) {
        return kiAlgorithm.getBestAction(board, playerNumber);
    }

    @Override
    public INDArray getReward(final Board board, final int playerNumber) {
        return kiAlgorithm.getReward(board, playerNumber);
    }

    public void train(final List<TrainSingleEntry> trainData) {
        kiAlgorithm.train(trainData);
    }

    @Override
    public void trainWhole(final List<TrainWholeEntry> trainData) {
        kiAlgorithm.trainWhole(trainData);
    }

    @Override
    public List<TrainWholeEntry> getTrainWholeData() {
        return kiAlgorithm.getTrainWholeData();
    }

    public void storeData() throws IOException {
        kiAlgorithm.storeData();
    }

    @Override
    public Optional<INDArray> output(final INDArray state) {
        return kiAlgorithm.output(state);
    }
}
