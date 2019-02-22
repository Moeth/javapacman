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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DecisionTreeAlgorithm extends AbstractKIAlgorithm {

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
        String bak = filePath + ".bak";
        try (FileWriter writer = new FileWriter(bak)) {
            arrayMap.write(writer);
            writer.flush();
//            log.info(String.format("saved %d to %s", arrayMap.size(), filePath));
        }
        File dest = new File(filePath);
        dest.delete();
        new File(bak).renameTo(dest);
    }

    @Override
    public Optional<INDArray> output(final INDArray state) {
        return arrayMap.findValue(state);
    }

    @Override
    public int getBestAction(final Board board, final int playerNumber) {
        Integer action = AlgorithmUtil.rewardToAction(board, getReward(board, playerNumber));
        return action;
    }

    @Override
    public INDArray getReward(final Board board, final int playerNumber) {
        return getReward(board.getBoard(playerNumber));
    }

    private INDArray getReward(final INDArray board) {
        Util.assertShape(board, Board.BOARD_LEARNING_SHAPE);
        return output(board)
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
    public void trainWhole(final List<TrainWholeEntry> trainData) {
        throw new IllegalArgumentException();
    }

    //    @Override
    public void train(final List<TrainSingleEntry> trainData) {
        for (TrainSingleEntry train : trainData) {
            train(train);
        }
    }

    private void train(final TrainSingleEntry train) {
        Util.assertShape(train.getState(), Board.BOARD_LEARNING_SHAPE);
        Optional<INDArray> rewardEntry = output(train.getState());
        int action = train.getAction();
        if (rewardEntry.isPresent()) {
//            ArrayMap.RewardEntry r = rewardEntry.get();
            final INDArray value = rewardEntry.get();
            double before = value.getDouble(action);
            double value1 = (before + train.getReward()) / 2;
            value.putScalar(action, value1);
//            INDArray result = Nd4j.zeros(Board.ACTION_SHAPE);
//            result.putScalar(train.getAction(), train.getReward());
//        Util.norm(result);
//            value.addi(result)
//                    .divi(2);
//            Util.norm(value);
//            Util.assertNorm(value);
        } else {
            INDArray result = Nd4j.zeros(Board.ACTION_SHAPE);
            result.putScalar(action, train.getReward());
//        Util.norm(result);
            Util.assertShape(result, Board.ACTION_SHAPE);
//            Util.norm(r);
//            Util.assertNorm(r);
            arrayMap.add(train.getState(), result);
        }
    }

    @Override
    public String toString() {
        return "RewardTableAlghoritm{" +
//                "filePath='" + getFilePath() + '\'' +
                ", rewardEntries=" + filledSize() +
                '}';
    }

    @Override
    public List<TrainWholeEntry> getTrainWholeData() {
        List<TrainWholeEntry> collect = arrayMap.stream()
                .map(e -> new TrainWholeEntry(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        Collections.shuffle(collect);
        return collect;
    }

    //    @Override
    public List<TrainSingleEntry> getDataAsTrainingData() {
        List<TrainSingleEntry> collect = arrayMap.stream()
                .map(e -> TrainSingleEntry.convert(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        Collections.shuffle(collect);
        return collect;
    }

}
