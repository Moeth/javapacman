package de.moeth.tictactoe;

import de.moeth.tictactoe.algorithm.KIAlgorithm;
import de.moeth.tictactoe.history.ActionHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class KIPlayer {

    private static final Logger log = LoggerFactory.getLogger(KIPlayer.class);
    private final KIAlgorithm algorithm;
    private final ActionHistory actionHistory = new ActionHistory();

    public KIPlayer(final KIAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public int getBestMove(final Board board, final int playerNumber) {

        int bestAction = algorithm.getBestAction(board, playerNumber);
        actionHistory.addEntry(board.getBoard(playerNumber), bestAction);

        return bestAction;
    }

    /**
     * Calculate probability of any won or lost or draw game at the end of the game and update stateList and stateProbabilityList.
     * It uses "Temporal Difference" formula to calculate probability of each game move.
     */
    public void updateReward(final double reward) {
        algorithm.train(actionHistory.updateReward(reward));
//        algorithm.updateReward(reward);
    }

    public void saveToFile() throws IOException {
        algorithm.storeData();
    }

    public KIAlgorithm getAlgorithm() {
        return algorithm;
    }

}
