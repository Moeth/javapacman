package de.moeth.tictactoe;

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
    private final RewardTableAlghoritm rewardTableAlghoritm;
    private final List<HistoryEntry> history = new ArrayList<>();

    public KIPlayer(final String filePath) {
        rewardTableAlghoritm = RewardTableAlghoritm.create(filePath);
    }

    public KIPlayer(final RewardTableAlghoritm rewardTableAlghoritm) {
        this.rewardTableAlghoritm = rewardTableAlghoritm;
    }

    public int getBestMove(final Board board) {
        int bestAction = board.getPossibleActions()
                .boxed()
                .max(Comparator.comparingDouble(action -> rewardTableAlghoritm.getReward(board.getBoard(), action)))
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
            rewardTableAlghoritm.changeValue(historyEntry.state, historyEntry.action, probabilityValue);
            probabilityValue *= 0.9;
        }
        history.clear();
    }

    public void saveToFile() {
        rewardTableAlghoritm.saveToFile();
    }

    public RewardTableAlghoritm getRewardTableAlghoritm() {
        return rewardTableAlghoritm;
    }

    @AllArgsConstructor
    @ToString
    private static class HistoryEntry {

        private final INDArray state;
        private final int action;
    }
}
