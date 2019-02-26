package de.moeth.tictactoe.history;

import de.moeth.tictactoe.algorithm.TrainSingleEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ActionHistory {

    private static final Logger log = LoggerFactory.getLogger(ActionHistory.class);
    private final List<HistoryEntry> history = new ArrayList<>();

    public void addEntry(final INDArray data, final int bestAction) {
        history.add(new ActionHistory.HistoryEntry(data, bestAction));
    }

    public List<TrainSingleEntry> updateReward(final double reward) {

        double realReward = reward;
        List<TrainSingleEntry> r = new ArrayList<>();
        for (int p = history.size() - 1; p >= 0; p--) {
            ActionHistory.HistoryEntry historyEntry = history.get(p);

            TrainSingleEntry asdf = new TrainSingleEntry(historyEntry.getState(), historyEntry.getAction(), realReward);
            r.add(asdf);
            realReward -= 0.01;
        }
        history.clear();
        return r;
    }

    public void clear() {
        history.clear();
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class HistoryEntry {

        private final INDArray state;
        //        private final INDArray reward;
        private final int action;
    }
}
