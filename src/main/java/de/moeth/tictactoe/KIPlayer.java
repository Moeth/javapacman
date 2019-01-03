package de.moeth.tictactoe;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KIPlayer {

    private static final Logger log = LoggerFactory.getLogger(KIPlayer.class);
    private final KIAlgorithm algorithm;
    private final List<HistoryEntry> history = new ArrayList<>();

    public KIPlayer(final KIAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public int getBestMove(final Board board) {
        INDArray reward = algorithm.getReward(board.getBoard());
        Preconditions.checkArgument(reward.rank() == 2);
        int bestAction = board.getPossibleActions()
                .boxed()
//                .max(Comparator.comparingDouble(action -> algorithm.getReward(board.getBoard(), action)))
                .max(Comparator.comparingDouble(action -> reward.getDouble(action)))
                .orElseThrow(() -> new IllegalArgumentException("asdf"));

        history.add(new HistoryEntry(board.getBoard(), bestAction));
        return bestAction;
    }

    /**
     * Calculate probability of any won or lost or draw game at the end of the game and update stateList and stateProbabilityList.
     * It uses "Temporal Difference" formula to calculate probability of each game move.
     */
    public void updateReward(final double reward) {

        double probabilityValue = reward;
        for (int p = history.size() - 1; p >= 0; p--) {
            HistoryEntry historyEntry = history.get(p);
            algorithm.changeValue(historyEntry.state, historyEntry.action, probabilityValue);
            probabilityValue *= 0.9;
        }
        history.clear();
    }

    public void saveToFile() {
        algorithm.storeData();
    }

    public KIAlgorithm getAlgorithm() {
        return algorithm;
    }

    @AllArgsConstructor
    @ToString
    private static class HistoryEntry {

        private final INDArray state;
        private final int action;
    }

    @AllArgsConstructor
    @ToString
    private static class ActionWithReward {

        private final INDArray state;
        private final int action;
    }


}
