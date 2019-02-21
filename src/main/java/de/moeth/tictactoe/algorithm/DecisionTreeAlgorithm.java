package de.moeth.tictactoe.algorithm;

import de.moeth.tictactoe.Board;
import de.moeth.tictactoe.Util;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DecisionTreeAlgorithm implements KIAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(DecisionTreeAlgorithm.class);

    private final ArrayMap arrayMap = new ArrayMap(Board.BOARD_LEARNING_SHAPE, Board.ACTION_SHAPE);
    private final String filePath;

    public static DecisionTreeAlgorithm create(final String filePath) throws IOException {
        return new DecisionTreeAlgorithm(filePath);
    }

    private DecisionTreeAlgorithm(final String filePath) throws IOException {
        this.filePath = filePath;
        arrayMap.read(new File(filePath));
    }

    @Override
    public void storeData() throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            arrayMap.write(writer);
            writer.flush();
//            log.info(String.format("saved %d to %s", arrayMap.size(), filePath));
        }
    }

    @Override
    public int getBestAction(final Board board, final int playerNumber) {
        Integer action = AlgorithmUtil.rewardToAction(board, getReward(board.getBoard(playerNumber)));
        return action;
    }

    private INDArray getReward(final INDArray board) {
        Util.assertShape(board, Board.BOARD_LEARNING_SHAPE);
        return arrayMap.findValue(board)
                .orElseGet(() -> Nd4j.rand(Board.ACTION_SHAPE));
    }

    public int size() {
        return arrayMap.size();
    }

    private long filledSize() {
        return arrayMap.stream()
                .count();
    }

    @Override
    public void train(final List<TrainSingleEntry> trainData) {
        for (TrainSingleEntry train : trainData) {
            train(train);
        }
    }

    private void train(final TrainSingleEntry train) {
        Util.assertShape(train.getState(), Board.BOARD_LEARNING_SHAPE);
        Optional<INDArray> rewardEntry = arrayMap.findValue(train.getState());
        if (rewardEntry.isPresent()) {
//            ArrayMap.RewardEntry r = rewardEntry.get();
            final INDArray value = rewardEntry.get();
            value.addi(train.getRewardChange());
//            Util.norm(value);
//            Util.assertNorm(value);
        } else {
            INDArray r = train.getRewardChange();
            Util.assertShape(r, Board.ACTION_SHAPE);
//            Util.norm(r);
//            Util.assertNorm(r);
            arrayMap.add(train.getState(), r);
        }
    }

    @Override
    public String toString() {
        return "RewardTableAlghoritm{" +
//                "filePath='" + getFilePath() + '\'' +
                ", rewardEntries=" + filledSize() +
                '}';
    }

    public Collection<TrainWholeEntry> getDataAsTrainingData222() {
        List<TrainWholeEntry> collect = arrayMap.stream()
                .map(e -> asdfasdf(e))
                .collect(Collectors.toList());
        Collections.shuffle(collect);
        return collect;
    }

    private TrainWholeEntry asdfasdf(final Map.Entry<INDArray, INDArray> e) {
        return new TrainWholeEntry(e.getKey(), e.getValue());
    }

    @Override
    public List<TrainSingleEntry> getDataAsTrainingData() {
        List<TrainSingleEntry> collect = arrayMap.stream()
                .map(e -> TrainSingleEntry.convert(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        Collections.shuffle(collect);
        return collect;
    }

    //    @Override
//    public void read(final Reader reader) throws IOException {
//        arrayMap.read(reader);
//    }
//
//    @Override
//    public void write(final Writer writer) throws IOException {
//        arrayMap.write(writer);
//    }
}
