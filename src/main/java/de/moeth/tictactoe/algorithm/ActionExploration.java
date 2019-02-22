package de.moeth.tictactoe.algorithm;

import de.moeth.tictactoe.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionExploration extends Delegator {

    private static final Logger log = LoggerFactory.getLogger(ActionExploration.class);
    private final double explorationRate;

    public ActionExploration(final KIAlgorithm kiAlgorithm, final double explorationRate) {
        super(kiAlgorithm);
        this.explorationRate = explorationRate;
    }

    @Override
    public int getBestAction(final Board board, final int playerNumber) {
        return AlgorithmUtil.rewardToAction(board, getReward(board, playerNumber), explorationRate);
    }
}
