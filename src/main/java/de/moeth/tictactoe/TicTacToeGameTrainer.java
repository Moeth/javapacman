package de.moeth.tictactoe;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>Developed by KIT Solutions Pvt. Ltd.</b> (www.kitsol.com) on 24-Aug-16.
 * This program is used for training.(Update the move reward based on the win or loose of the game).
 * Here both player are being played automatically and update probability in AllMoveWithReward.txt.
 * AllMoveWithReward.txt file can be any file containing TicTacToe data generated by running TicTacToeData.java or any other old file
 * updated by this program when run earlier.
 */
public class TicTacToeGameTrainer {

    private static Log log = LogFactory.getLog(TicTacToeGameTrainer.class);
    private static final int PLAY_TOTAL_GAME = 100;

    private int totalGameCounter = 0;
    private int numberOfWinPlayer1 = 0;
    private int numberOfWinPlayer2 = 0;
    private int draw = 0;
    private final KIPlayer player1;
    private final KIPlayer player2;

    private TicTacToeGameTrainer(final KIPlayer player1, final KIPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public static void main(String[] args) {
        try {
            new TicTacToeGameTrainer(
                    new KIPlayer("AllMoveWithReward1.txt"),
                    new KIPlayer("AllMoveWithReward2.txt")
            ).train();
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void train() {

        // sets a player number for first player  it can be 1 or 2, i.e. X or O.
        int firstPlayerNumber = 0;
        try {
            while (totalGameCounter < PLAY_TOTAL_GAME) {
                firstPlayerNumber %= 2;
                firstPlayerNumber++;

                play(firstPlayerNumber);
            }
            saveToFile();
        } catch (Exception e) {
//            throw new IllegalArgumentException("", e);
            log.error(e);
        }
    }

    private void play(final int tempMoveType) {

        log.info("train");
        Board board = new Board();
        totalGameCounter++;
        board.printBoard();

        int moveType = tempMoveType;
        while (board.getGameDecision() == 0) {
            board = getNextBestMove(board, moveType);
            board.printBoard();
            moveType = moveType == 1 ? 2 : 1;
        }

        applyGameResults(board);
        // verifies current game decision (win or draw)
        int gameState = board.getGameDecision();
        log.info("gameState " + gameState);

        // if gameState != 0, means game is finished with a decision
//        if (gameState != 0) {
        if (gameState == 1) {           // player 1 won
            numberOfWinPlayer1++;
        } else if (gameState == 2) {  // player 2 won
            numberOfWinPlayer2++;
        } else {  // game is draw
            draw++;
        }
        log.info("\nTotal Game :" + totalGameCounter
                + "\n   Player 1:" + numberOfWinPlayer1
                + "\n   Player 2:" + numberOfWinPlayer2
                + "\n   XXDrawOO:" + draw + "\n"
                + "getProbatilityMap: " + player1.getRewardTableAlghoritm().filledSize() + " : " + player2.getRewardTableAlghoritm().filledSize());
    }

    private Board getNextBestMove(Board board, int playerNumber) {
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

    private void applyGameResults(Board board) {
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

    private void saveToFile() {
        player1.saveToFile();
        player2.saveToFile();
    }
}
