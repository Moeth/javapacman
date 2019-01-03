package de.moeth.tictactoe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>Developed by KIT Solutions Pvt. Ltd. (www.kitsol.com)</b> on 24-Aug-16.
 * This program does following tasks.
 * - loads tictactoe data file
 * - provide next best move depending on the previous passed
 * - reset the board when a game is over.
 * - checks whether game is finished.
 * - update probability of each move made in lost or won game when game is finished
 */
public class TicTacToePlayer {

    private static final Logger log = LoggerFactory.getLogger(TicTacToePlayer.class);

    private final KIPlayer player1 = new KIPlayer("AllMoveWithReward1.txt");
    private final KIPlayer player2 = new KIPlayer("AllMoveWithReward2.txt");

    /**
     * To retrieve best next move provided current board and player number (i.e. first or second player)
     */
    public Board getNextBestMove(Board board, int playerNumber) {
        int action = getBestMove(board, playerNumber);
        return board.applyAction(playerNumber, action);
    }

    private int getBestMove(final Board board, final int playerNumber) {
        if (playerNumber == 1) {
            return player1.getBestMove(board);
        } else if (playerNumber == 2) {
            return player2.getBestMove(board);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void applyGameResults(Board board) {
        final int won = board.getGameDecision();
        if (won == 1) {
            log.info("Win player_1");
            player1.updateReward(1); //Win player_1
            player2.updateReward(-1);//loose player_2
        } else if (won == 2) {
            log.info("Win player_2");
            player1.updateReward(-1);//loose player_1
            player2.updateReward(1);//Win player_2
        } else if (won == 3) {
            player1.updateReward(-0.1);
            player2.updateReward(-0.1);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void saveToFile() {
        player1.saveToFile();
        player2.saveToFile();
    }

    public KIPlayer getPlayer1() {
        return player1;
    }

    public KIPlayer getPlayer2() {
        return player2;
    }
}
