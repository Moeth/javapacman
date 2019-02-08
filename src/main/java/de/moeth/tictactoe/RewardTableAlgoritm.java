package de.moeth.tictactoe;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class RewardTableAlgoritm implements KIAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(RewardTableAlgoritm.class);
    private static final Random random = new Random();
    private static final Comparator<INDArray> StateComparator2 = (r1, r2) -> {
        Preconditions.checkArgument(r1.equalShapes(r2));

        Util.assertShape(r1, Board.BOARD_LEARNING_SHAPE);
        Util.assertShape(r2, Board.BOARD_LEARNING_SHAPE);
        for (int i = 0; i < 9; i++) {
//            Preconditions.checkArgument(r1.s);
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 2; k++) {
                    int compare = Double.compare(r1.getDouble(i, j, k), r2.getDouble(i, j, k));
                    if (compare != 0) {
                        return compare;
                    }
                }
            }
        }
        return 0;
    };
    private static final Comparator<RewardEntry> StateComparator = (r1, r2) -> StateComparator2.compare(r1.board, r2.board);

    private final String filePath;
    private final List<RewardEntry> rewardEntries = new ArrayList<>();

    public static RewardTableAlgoritm create(final String filePath) {

        RewardTableAlgoritm rewardTableAlgoritm = new RewardTableAlgoritm(filePath);
        if (new File(filePath).exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    RewardEntry rewardEntry = RewardEntry.readLine(line);
                    rewardTableAlgoritm.rewardEntries.add(rewardEntry);
                }
                log.info("load " + filePath);
            } catch (IOException e) {
                throw new IllegalArgumentException("", e);
            }
        }
        return rewardTableAlgoritm;
    }

    private RewardTableAlgoritm(final String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void storeData() throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            Collections.sort(rewardEntries, StateComparator);
            for (final RewardEntry rewardEntry : rewardEntries) {
                writer.append(rewardEntry.writeLine());
                writer.append('\r');
                writer.append('\n');
            }
            writer.flush();
            log.info(String.format("saved %d to %s", rewardEntries.size(), filePath));
        }
    }

    @Override
    public INDArray getReward(final INDArray board) {
        Util.assertShape(board, Board.BOARD_LEARNING_SHAPE);
        return rewardEntries.stream()
                .filter(e1 -> e1.board.equals(board))
                .findFirst()
                .map(e -> e.action)
                .orElseGet(() -> Nd4j.rand(Board.ACTION_SHAPE));
    }

    public int size() {
        return rewardEntries.size();
    }

    private long filledSize() {
        return rewardEntries.stream()
//                .filter(s -> Math.abs(s.reward) > 0.001)
                .count();
    }

    private Optional<RewardEntry> find(INDArray board) {
        Util.assertShape(board, Board.BOARD_LEARNING_SHAPE);
        return rewardEntries.stream()
                .filter(e -> e.board.equals(board))
                .findFirst();
    }

    @Override
    public void train(final Collection<TrainSingleEntry> trainData) {
        for (TrainSingleEntry train : trainData) {
            train(train);
        }
    }

    private void train(final TrainSingleEntry train) {
        Util.assertShape(train.getState(), Board.BOARD_LEARNING_SHAPE);
        Optional<RewardEntry> rewardEntry = find(train.getState());
        if (rewardEntry.isPresent()) {
            RewardEntry r = rewardEntry.get();
//            r.action.muli(0.7).addi(train.getRewardChange().muli(0.3));
            r.action.addi(train.getRewardChange().muli(3));
            Util.norm(r.action);
            Util.assertNorm(r.action);

//            double old = r.action.getDouble(train.getAction());
//            r.action.putScalar(train.getAction(), 0.7 * old + 0.3 * train.getReward());
        } else {
            INDArray r = train.getRewardChange();
//            INDArray r = Nd4j.rand(Board.ACTION_SHAPE);
//            r.putScalar(train.getAction(), train.getReward());
            Util.assertShape(r, Board.ACTION_SHAPE);
            Util.norm(r);
            Util.assertNorm(r);
            rewardEntries.add(new RewardEntry(train.getState(), r));
        }
    }

    @Override
    public String toString() {
        return "RewardTableAlghoritm{" +
                "filePath='" + filePath + '\'' +
                ", rewardEntries=" + filledSize() +
                '}';
    }

    public Collection<TrainWholeEntry> getDataAsTrainingData() {
        List<TrainWholeEntry> collect = rewardEntries.stream()
//                .map(e )
//                .collect(Collectors.groupingBy(e -> e.board))
//                .entrySet()
//                .stream()
                .map(e -> asdfasdf(e))
                .collect(Collectors.toList());
        Collections.shuffle(collect);
        return collect;
    }

    private TrainWholeEntry asdfasdf(final RewardEntry e) {
//        INDArray zeros = Nd4j.zeros(BOARD_SHAPE);
//        e.getValue().stream().forEach(r -> zeros.putScalar(r.action, r.reward));
        return new TrainWholeEntry(e.board, e.action);
    }

    @AllArgsConstructor
    private static class RewardEntry {

        private final INDArray board;
        private final INDArray action;
//        private double reward;

        private static RewardEntry readLine(final String line) {
            String[] nextLine = line.split("\t");
            INDArray board = readArray(nextLine[0]);
            INDArray action = readArray(nextLine[1]);
            return new RewardEntry(board, action);
        }

        private static INDArray readArray(final String tempLine1) {
//            try {
//                byte[] bytes = Hex.decodeHex(tempLine1.toCharArray());
//                return Nd4j.fromByteArray(bytes);
//            } catch (DecoderException e) {
//                throw new IOException("", e);
//            }

            String[] split = tempLine1.split("#");

            double[] result = Util.gson.fromJson(split[1], double[].class);
            int[] shape = Util.gson.fromJson(split[0], int[].class);

            return Nd4j.create(result, shape, 'c');
        }

        private String writeLine() {
            Util.norm(action);
            return writeArray(board) + "\t" + writeArray(action);
        }

        private String writeArray(INDArray array) {
            return Util.gson.toJson(array.shape()) + "#" + Util.toString(array);

//            return Hex.encodeHexString(Nd4j.toByteArray(array));
        }
    }
}
