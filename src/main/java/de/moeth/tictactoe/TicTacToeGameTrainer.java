package de.moeth.tictactoe;

import de.moeth.tictactoe.algorithm.DecisionTreeAlgorithm;
import de.moeth.tictactoe.algorithm.KIAlgorithm;
import de.moeth.tictactoe.algorithm.NeuralNetStateAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * <b>Developed by KIT Solutions Pvt. Ltd.</b> (www.kitsol.com) on 24-Aug-16.
 * This program is used for training.(Update the move reward based on the win or loose of the game).
 * Here both player are being played automatically and update probability in AllMoveWithReward.txt.
 * AllMoveWithReward.txt file can be any file containing TicTacToe data generated by running TicTacToeData.java or any other old file
 * updated by this program when run earlier.
 */
public class TicTacToeGameTrainer {

//    SentimentExampleIterator

    private static final Logger log = LoggerFactory.getLogger(TicTacToeGameTrainer.class);
    private static final int PLAY_TOTAL_GAME = 100;

    private final KIPlayer player1;
    private final KIPlayer player2;
    private final boolean learn;

    private TicTacToeGameTrainer(final KIAlgorithm player1, final KIAlgorithm player2, final boolean learn) {
        this.player1 = new KIPlayer(player1);
        this.player2 = new KIPlayer(player2);
        this.learn = learn;
    }

    public static void main(String[] args) {
        try {
//            do {
            DecisionTreeAlgorithm player1 = DecisionTreeAlgorithm.create("AllMoveWithReward_1.txt");
//            NeuralNetAlgorithm neuralNetAlgorithm = NeuralNetAlgorithm.create("player1");
            NeuralNetStateAlgorithm neuralNetAlgorithm = NeuralNetStateAlgorithm.create("player1");
            NeuralNetStateAlgorithm neuralNetAlgorithm2 = NeuralNetStateAlgorithm.create("player2");
            new TicTacToeGameTrainer(neuralNetAlgorithm, neuralNetAlgorithm2, true).train(100);
            new TicTacToeGameTrainer(neuralNetAlgorithm, neuralNetAlgorithm2, true).play2(1, true);
            new TicTacToeGameTrainer(neuralNetAlgorithm, neuralNetAlgorithm2, true).play2(1, true);
            new TicTacToeGameTrainer(neuralNetAlgorithm, neuralNetAlgorithm2, true).play2(1, true);

            //
//            //            RewardTableAlgoritm player2 = RewardTableAlgoritm.create("AllMoveWithReward_2.txt");
////            GameResult gameResult = new TicTacToeGameTrainer(player1, player1, true)
////                    .train(1000);
////            } while (ga)
//
////            Preconditions.checkArgument(gameResult.draw == 10);
//
////            double evaluate = 1;
////            for (int i = 0; i < 1 && evaluate > 0.01; i++) {
////                neuralNetAlgorithm.trainWhole(player1.getDataAsTrainingData());
////                evaluate = neuralNetAlgorithm.evaluate();
////            }
//
//            TicTacToeGameTrainer comp = new TicTacToeGameTrainer(player1, neuralNetAlgorithm, true);
//            comp.train(50000);
//
//////            neuralNetAlgorithm.evaluate(player1.getDataAsTrainingData());
//////            player1.getDataAsTrainingData()
////
////            NeuralNetAlgorithm neuralNetAlgorithm2 = NeuralNetAlgorithm.create("player2");
////            neuralNetAlgorithm2.train(player2.getDataAsTrainingData());
////
//            for (int i = 0; i < 2; i++) {
//                GameResult gameResult2 = new TicTacToeGameTrainer(neuralNetAlgorithm, neuralNetAlgorithm, true)
//                        .train(10000);
//            }
////
////            TicTacToeGameTrainer neural = new TicTacToeGameTrainer(neuralNetAlgorithm, neuralNetAlgorithm, false);
////            GameResult gameResult3 = neural.train(1000);
////            neural.train(100);
//
////////
////////            Preconditions.checkArgument(gameResult2.draw == 50);

        } catch (Exception e) {
            log.error("", e);
        }
    }

    private static void train(final KIAlgorithm player1, final KIAlgorithm player2, int times) throws IOException {
        TicTacToeGameTrainer comp = new TicTacToeGameTrainer(player1, player2, true);
        comp.train(times);
    }
    private GameResult train(final int playTotalGame) throws IOException {

        // sets a player number for first player  it can be 1 or 2, i.e. X or O.
        int firstPlayerNumber = 0;
        GameResult gameResult = new GameResult();
        while (gameResult.totalGameCounter < playTotalGame) {
            firstPlayerNumber %= 2;
            firstPlayerNumber++;

            //        log.info("train");
            int gameState = play2(firstPlayerNumber, false);
            gameResult.applyGameState(gameState);
            if (gameResult.totalGameCounter % 1 == 0) {
                log.info(gameResult.toString());
            }
//        log.info(
//                gameResult.toString() + "\n"
//                        + "getProbatilityMap: " + player1.getAlgorithm().toString() + " : " + player2.getAlgorithm().toString());
            if (gameResult.totalGameCounter % 10 == 0) {
                saveToFile();
            }
        }
        saveToFile();
        return gameResult;
    }

    private int play2(final int tempMoveType, final boolean printBoard) {
        Board board = new Board();
        if (printBoard) {
            board.printBoard();
        }

        int moveType = tempMoveType;
        while (board.getGameDecision() == 0) {
            board = getNextBestMove(board, moveType);
            if (printBoard) {
                board.printBoard();
            }
            moveType = moveType == 1 ? 2 : 1;
        }

        applyGameResults(board);
        // verifies current game decision (win or draw)
        return board.getGameDecision();
    }

    private Board getNextBestMove(Board board, int playerNumber) {
        int action = getBestMove(board, playerNumber);
        return board.applyAction(playerNumber, action);
    }

    private int getBestMove(final Board board, final int playerNumber) {
        if (playerNumber == 1) {
            return player1.getBestMove(board, playerNumber);
        } else if (playerNumber == 2) {
            return player2.getBestMove(board, playerNumber);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void applyGameResults(Board board) {
        if (!learn) {
            return;
        }
        final int won = board.getGameDecision();
        if (won == 1) {
//            log.info("Win player_1");
            player1.updateReward(1); //Win player_1
            player2.updateReward(-1);//loose player_2
        } else if (won == 2) {
//            log.info("Win player_2");
            player1.updateReward(-1);//loose player_1
            player2.updateReward(1);//Win player_2
        } else if (won == 3) {
//            log.info("Draw");
            player1.updateReward(0.5);
            player2.updateReward(0.5);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void saveToFile() throws IOException {
        player1.saveToFile();
        player2.saveToFile();
    }

    private static class GameResult {

        private int numberOfWinPlayer1 = 0;
        private int numberOfWinPlayer2 = 0;
        private int draw = 0;
        private int totalGameCounter = 0;

        private void applyGameState(final int gameState) {
            totalGameCounter++;

            if (gameState == 1) {
                // player 1 won
                numberOfWinPlayer1++;
            } else if (gameState == 2) {
                // player 2 won
                numberOfWinPlayer2++;
            } else {
                // game is draw
                draw++;
            }
        }

        @Override
        public String toString() {
            return String.format("GameResult{numberOfWinPlayer1=%.2f, numberOfWinPlayer2=%.2f, draw=%.2f, totalGameCounter=%d}",
                    100.0 * numberOfWinPlayer1 / totalGameCounter,
                    100.0 * numberOfWinPlayer2 / totalGameCounter,
                    100.0 * draw / totalGameCounter,
                    totalGameCounter);
        }
    }
}

