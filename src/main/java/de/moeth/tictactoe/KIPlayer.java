package de.moeth.tictactoe;

import com.google.common.base.Preconditions;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class KIPlayer {

    private static final Logger log = LoggerFactory.getLogger(KIPlayer.class);
    private final KIAlgorithm algorithm;
    private final List<HistoryEntry> history = new ArrayList<>();

    public KIPlayer(final KIAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public int getBestMove(final Board board, final int playerNumber) {
        INDArray data = board.getBoard(playerNumber);
        INDArray reward = algorithm.getReward(data);
        Util.assertShape(reward, Board.ACTION_SHAPE);
        Preconditions.checkArgument(reward.rank() == 2);
        int abs = (int) Math.abs(new Random().nextGaussian());
        int bestAction = board.getPossibleActions()
                .boxed()
//                .max(Comparator.comparingDouble(action -> algorithm.getReward(board.getBoard(), action)))
                .max(Comparator.comparingDouble(action -> reward.getDouble(action)))
                .orElseThrow(() -> new IllegalArgumentException("asdf"));

        history.add(new HistoryEntry(data, reward, bestAction));
        return bestAction;
    }

    /**
     * Calculate probability of any won or lost or draw game at the end of the game and update stateList and stateProbabilityList.
     * It uses "Temporal Difference" formula to calculate probability of each game move.
     */
    public void updateReward(final double reward) {

        double realReward = reward;
        List<KIAlgorithm.TrainSingleEntry> r = new ArrayList<>();
        for (int p = history.size() - 1; p >= 0; p--) {
            HistoryEntry historyEntry = history.get(p);
//            algorithm.changeValue(historyEntry, realReward);

            KIAlgorithm.TrainSingleEntry asdf = new KIAlgorithm.TrainSingleEntry(historyEntry.getState(), historyEntry.getAction(), realReward);
            r.add(asdf);
            realReward *= 0.99;
        }
        algorithm.train(r);
        history.clear();
    }

    public void saveToFile() throws IOException {
        algorithm.storeData();
    }

    public KIAlgorithm getAlgorithm() {
        return algorithm;
    }

}
