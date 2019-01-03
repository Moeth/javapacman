package de.moeth.tictactoe;

import lombok.AllArgsConstructor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RewardTableAlgoritm implements KIAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(RewardTableAlgoritm.class);
    private static final Random random = new Random();
    private static final Comparator<RewardEntry> StateComparator = (r1, r2) -> {
        for (int i = 0; i < 9; i++) {
            int compare = Double.compare(r1.board.getDouble(i), r2.board.getDouble(i));
            if (compare != 0) {
                return compare;
            }
        }
        return Double.compare(r1.action, r2.action);
    };

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
    public void storeData() {
        try (FileWriter writer = new FileWriter(filePath)) {
            Collections.sort(rewardEntries, StateComparator);
            for (final RewardEntry rewardEntry : rewardEntries) {
                writer.append(rewardEntry.writeLine());
                writer.append('\r');
                writer.append('\n');
            }
            writer.flush();
            log.info("saved to " + filePath);
        } catch (Exception i) {
            log.error("saveToFile failed", i);
        }
    }

    @Override
    public INDArray getReward(final INDArray board) {
        INDArray rand = Nd4j.rand(1, 9);
        rewardEntries.stream()
                .filter(e1 -> e1.board.equals(board))
                .forEach(e -> rand.putScalar(e.action, e.reward));
        return rand;
    }

    public int size() {
        return rewardEntries.size();
    }

    private long filledSize() {
        return rewardEntries.stream()
                .filter(s -> Math.abs(s.reward) > 0.001)
                .count();
    }

    private Optional<RewardEntry> find(INDArray board, int action) {
        return rewardEntries.stream()
                .filter(e -> e.board.equals(board) && e.action == action)
                .findFirst();
    }

    @Override
    public void changeValue(final INDArray state, final int action, final double reward) {
        Optional<RewardEntry> rewardEntry = find(state, action);
        if (rewardEntry.isPresent()) {
            RewardEntry r = rewardEntry.get();
            r.reward = 0.7 * r.reward + 0.3 * reward;
        } else {
            rewardEntries.add(new RewardEntry(state, action, reward));
        }
    }

    @Override
    public String toString() {
        return "RewardTableAlghoritm{" +
                "filePath='" + filePath + '\'' +
                ", rewardEntries=" + filledSize() +
                '}';
    }

    @AllArgsConstructor
    private static class RewardEntry {

        private final INDArray board;
        private final int action;
        private double reward;

        private static RewardEntry readLine(final String line) {
            INDArray input = Nd4j.zeros(1, 9);
            String[] nextLine = line.split(" ");
            String tempLine1 = nextLine[0];
            String testLine[] = tempLine1.split(":");
            for (int i = 0; i < 9; i++) {
                int number = Integer.parseInt(testLine[i]);
                input.putScalar(new int[]{i}, number);
            }
            int action = Integer.parseInt(nextLine[1]);
            double reward = Double.parseDouble(nextLine[2]);
            return new RewardEntry(input, action, reward);
        }

        private String writeLine() {
            final String tempString = IntStream.range(0, 9)
                    .map(i -> board.getInt(i))
                    .mapToObj(i -> Integer.toString(i))
                    .collect(Collectors.joining(":"));

            return tempString + " " + action + " " + Double.toString(reward);
        }
    }
}
