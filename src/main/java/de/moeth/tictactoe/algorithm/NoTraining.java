package de.moeth.tictactoe.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NoTraining extends Delegator {

    private static final Logger log = LoggerFactory.getLogger(NoTraining.class);

    public NoTraining(final KIAlgorithm kiAlgorithm) {
        super(kiAlgorithm);
    }

    @Override
    public void train(final List<TrainSingleEntry> trainData) {

    }

    @Override
    public void trainWhole(final List<TrainWholeEntry> trainData) {

    }
}
