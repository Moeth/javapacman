package de.moeth.tictactoe;

import com.google.common.base.Preconditions;
import lombok.ToString;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

@ToString
public class Board {

    private static final Logger log = LoggerFactory.getLogger(Board.class);
    public static final long[] BOARD_LEARNING_SHAPE = {3, 3, 2};
    public static final int ACTIONS = 9;
    private static final long[] BOARD_SHAPE = {1, ACTIONS};
    public static final long[] ACTION_SHAPE = {1, ACTIONS};

    private final INDArray board;

    private Board(final INDArray board) {
        this.board = board;
    }

    public Board() {
        board = Nd4j.zeros(BOARD_SHAPE);
    }

    public void printBoard() {
        int k = 0;
        StringBuilder boardString = new StringBuilder("\n");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int position = (int) board.getDouble(k);
                boardString.append("  " + position);
                k++;
            }
            if (i < 2) {
                boardString.append("\n");
            }
        }
        log.info(boardString.toString());
    }

    public IntStream getPossibleActions() {
        return IntStream.range(0, (int) board.length())
                .filter(i -> isAllowedAction(i));
    }

    public boolean isAllowedAction(int action) {
        return board.getDouble(action) == 0;
    }

    public Board applyAction(int playerNumber, int action) {
        Preconditions.checkArgument(isAllowedAction(action), action + " is not allowed");
        INDArray inputArray = Nd4j.zeros(BOARD_SHAPE);
        Nd4j.copy(board, inputArray);
        inputArray.putScalar(new int[]{0, action}, playerNumber);
        return new Board(inputArray);
    }

    public INDArray getBoard(int playerNumber) {
        INDArray result = Nd4j.zeros(BOARD_LEARNING_SHAPE);
        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int position = board.getInt(k);
                if (position > 0) {
                    int p = playerNumber == position ? 0 : 1;
                    result.putScalar(i, j, p, 1);
                }

                k++;
            }
        }
        Util.assertShape(result, BOARD_LEARNING_SHAPE);
        return result;
    }

    public int getGameDecision() {
        int boardPosition1 = board.getInt(0);
        int boardPosition2 = board.getInt(1);
        int boardPosition3 = board.getInt(2);
        int boardPosition4 = board.getInt(3);
        int boardPosition5 = board.getInt(4);
        int boardPosition6 = board.getInt(5);
        int boardPosition7 = board.getInt(6);
        int boardPosition8 = board.getInt(7);
        int boardPosition9 = board.getInt(8);

        if (isSame(boardPosition1, boardPosition2, boardPosition3)) {
            return boardPosition1;
        }
        if (isSame(boardPosition4, boardPosition5, boardPosition6)) {
            return boardPosition4;
        }
        if (isSame(boardPosition7, boardPosition8, boardPosition9)) {
            return boardPosition7;
        }
        if (isSame(boardPosition1, boardPosition4, boardPosition7)) {
            return boardPosition1;
        }
        if (isSame(boardPosition2, boardPosition5, boardPosition8)) {
            return boardPosition2;
        }
        if (isSame(boardPosition3, boardPosition6, boardPosition9)) {
            return boardPosition3;
        }
        if (isSame(boardPosition1, boardPosition5, boardPosition9)) {
            return boardPosition1;
        }
        if (isSame(boardPosition3, boardPosition5, boardPosition7)) {
            return boardPosition3;
        }
        for (int i = 0; i < ACTIONS; i++) {
            if (board.getInt(i) == 0) {
                return 0;
            }
        }
        return 3;
    }

    private boolean isSame(int i, int i2, int i3) {
        return i == i2 && i2 == i3 && i > 0;
    }
}
