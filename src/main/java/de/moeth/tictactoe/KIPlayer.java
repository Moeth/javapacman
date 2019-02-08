package de.moeth.tictactoe;

import com.google.common.base.Preconditions;
import de.moeth.tictactoe.algorithm.KIAlgorithm;
import de.moeth.tictactoe.algorithm.TrainSingleEntry;
import de.moeth.tictactoe.history.ActionHistory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

class KIPlayer {

    private static final Logger log = LoggerFactory.getLogger(KIPlayer.class);
    private final KIAlgorithm algorithm;
    //    private final List<ActionHistory.HistoryEntry> history = new ArrayList<>();
    private final ActionHistory actionHistory = new ActionHistory();

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
                .max(Comparator.comparingDouble(action -> reward.getDouble(action)))
                .orElseThrow(() -> new IllegalArgumentException("asdf"));

        actionHistory.addEntry(data, reward, bestAction);
        return bestAction;
    }

    /**
     * Calculate probability of any won or lost or draw game at the end of the game and update stateList and stateProbabilityList.
     * It uses "Temporal Difference" formula to calculate probability of each game move.
     */
    public void updateReward(final double reward) {

        List<TrainSingleEntry> r = actionHistory.updateReward(reward);
        algorithm.train(r);
    }

    public void saveToFile() throws IOException {
        algorithm.storeData();
    }

    public KIAlgorithm getAlgorithm() {
        return algorithm;
    }

}
