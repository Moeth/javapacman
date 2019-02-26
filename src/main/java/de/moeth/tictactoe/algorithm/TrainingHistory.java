package de.moeth.tictactoe.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrainingHistory extends Delegator {

    private static final Logger log = LoggerFactory.getLogger(TrainingHistory.class);
    private List<TrainSingleEntry> entries = new ArrayList<>();
    private final int size;

    public TrainingHistory(final KIAlgorithm kiAlgorithm, final int size) {
        super(kiAlgorithm);
        this.size = size;
    }

    @Override
    public void train(final List<TrainSingleEntry> trainData) {
        entries.addAll(trainData);
        log.info("train " + entries.size());
        super.train(entries);
        if (entries.size() > size) {
            Collections.shuffle(entries);
            entries = entries.subList(0, size / 2);
        }
    }
}
