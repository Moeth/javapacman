package de.moeth.tictactoe;//package org.deeplearning4j.examples.tictactoe;
//
//import org.nd4j.linalg.api.ndarray.INDArray;
//import org.nd4j.linalg.factory.Nd4j;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
///**
// * <b>Developed by KIT Solutions Pvt. Ltd.</b> (www.kitsol.com) on 24-Aug-16
// * This is a GUI to play game using trained network.
// */
//public class PlayTicTacToe extends JFrame {
//
//    private String playerInformation = "FirstPlayer:X";
//    private JFrame frame = new JFrame("TicTacToe");
//    private JButton[] gridMoveButton = new JButton[9];
//    private JButton startButton = new JButton("Start");
//    private JButton switchButton = new JButton("Switch Player");
//    private JLabel infoLabel = new JLabel(playerInformation);
//    private boolean isAIFirstPlayer = true;
//    private int xWon = 0;
//    private int oWon = 0;
//    private int draw = 0;
//    private TicTacToePlayer ticTacToePlayer;
//    private Thread aiLoad;
//
//    /**
//     * Constructor that loads trained data and initializes TicTacToePlayer object to play the game.
//     * Also, initializes the GUI and display it.
//     */
//    public PlayTicTacToe() throws HeadlessException {
//        ticTacToePlayer = new TicTacToePlayer();
//        aiLoad = new Thread(ticTacToePlayer);
//        aiLoad.start();
//        ticTacToePlayer.setUpdateLimit(10);
//        ticTacToePlayer.setAutoUpdate(true);
//        frame.setSize(350, 450);
//        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
//        frame.setVisible(true);
//        frame.setResizable(false);
//    }
//
//    public static void main(String[] args) {
//        PlayTicTacToe game = new PlayTicTacToe();
//        game.renderGUI();
//    }
//
//    /**
//     * Create the GUI for tictactoe game with 9 move button,two utility button.
//     */
//    private void renderGUI() {
//
//        JPanel mainPanel = new JPanel(new BorderLayout());
//        JPanel menu = new JPanel(new BorderLayout());
//        JPanel tital = new JPanel(new BorderLayout());
//        JPanel game = new JPanel(new GridLayout(3, 3));
//
//        frame.add(mainPanel);
//
//        mainPanel.setPreferredSize(new Dimension(325, 425));
//        menu.setPreferredSize(new Dimension(300, 50));
//        tital.setPreferredSize(new Dimension(300, 50));
//        game.setPreferredSize(new Dimension(300, 300));
//
//        //Create the basic layout for game
//
//        mainPanel.add(menu, BorderLayout.NORTH);
//        mainPanel.add(tital, BorderLayout.AFTER_LINE_ENDS);
//        mainPanel.add(game, BorderLayout.SOUTH);
//        tital.add(infoLabel, BorderLayout.CENTER);
//        menu.add(startButton, BorderLayout.WEST);
//        menu.add(switchButton, BorderLayout.EAST);
//
//        //Create the 9 Grid button on UI
//        for (int i = 0; i < 9; i++) {
//
//            gridMoveButton[i] = new JButton();
//            gridMoveButton[i].setText(" ");
//            gridMoveButton[i].setVisible(true);
//            gridMoveButton[i].setEnabled(false);
//            gridMoveButton[i].addActionListener(new PlayTicTacToe.MyActionListener(i));
//            game.add(gridMoveButton[i]);
//        }
//        game.setVisible(true);
//        startButton.setEnabled(false);
//        switchButton.setEnabled(false);
//
//        //Start Button Click Listener.
//        startButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                reset(); //Reset GUI
//                changeGridButtonAccessibility(true);
//                switchButton.setEnabled(true);
//                if (isAIFirstPlayer == true) {
//                    Board firstMove = new Board();
//                    Board nextMove = ticTacToePlayer.getNextBestMove(firstMove, 1);
//                    updateMoveOnBoard(nextMove);
//                }
//            }
//        });
//
//        //switch Button Click Listener
//        switchButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (isAIFirstPlayer == true) {
//                    playerInformation = "FirstPlayer:O";
//                    isAIFirstPlayer = false;
//                } else {
//                    playerInformation = "FirstPlayer:X";
//                    isAIFirstPlayer = true;
//                }
//                reset(); //Reset GUI
//                updateInformation();
//
//            }
//        });
////        while (true) {
//////            if (ticTacToePlayer.isAILoad() == true) {
//////                break;
//////            }
////            try {
////                Thread.sleep(10);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////        }
//        startButton.setEnabled(true);
//        switchButton.setEnabled(true);
//    }
//
//    /**
//     * Update the GUI depending upon the move provided by TicTacToePlayer or by manual player
//     */
//    private void updateMoveOnBoard(Board board) {
//
//        if (board == null) {
//            return;
//        }
//        if (isAIFirstPlayer) {
//            for (int i = 0; i < 9; i++) {
//                int v = (int) board.getDouble(i);
//                if (v == 1) {
//                    gridMoveButton[i].setText("X");
//                } else if (v == 2) {
//                    gridMoveButton[i].setText("O");
//                }
//            }
//        } else {
//            for (int i = 0; i < 9; i++) {
//                int v = (int) board.getDouble(i);
//                if (v == 1) {
//                    gridMoveButton[i].setText("O");
//                } else if (v == 2) {
//                    gridMoveButton[i].setText("X");
//                }
//            }
//        }
//    }
//
//    /**
//     * Reset the button for and also reset the TicTacToe player object after game is finished.
//     */
//    private void reset() {
//        for (int i = 0; i < 9; i++) {
//            gridMoveButton[i].setText(" ");
//            gridMoveButton[i].setEnabled(false);
//        }
//        ticTacToePlayer.reset();
//    }
//
//    /**
//     * Enable or disable the game by disabling all buttons.
//     */
//    private void changeGridButtonAccessibility(boolean enable) {
//        for (int i = 0; i < 9; i++) {
//            gridMoveButton[i].setEnabled(enable);
//        }
//    }
//
//    /**
//     * This function gives the current state board in INDArray
//     */
//    private Board getCurrentStateOfBoard() {
//        INDArray positionArray = Nd4j.zeros(1, 9);
//        for (int i = 0; i < 9; i++) {
//            String gridMoveButtonValue = gridMoveButton[i].getText();
//
//            if (isAIFirstPlayer) {
//
//                if (gridMoveButtonValue.equals("X")) {
//                    positionArray.putScalar(new int[]{0, i}, 1);
//                } else if (gridMoveButtonValue.equals("O")) {
//                    positionArray.putScalar(new int[]{0, i}, 2);
//                }
//            } else {
//                if (gridMoveButtonValue.equals("O")) {
//                    positionArray.putScalar(new int[]{0, i}, 1);
//                } else if (gridMoveButtonValue.equals("X")) {
//                    positionArray.putScalar(new int[]{0, i}, 2);
//                }
//            }
//        }
//        return new Board(positionArray);
//    }
//
//    /**
//     * This method update the UI(Board) for click event on any button on the board and
//     * immediately calls automatic user to play the next move.
//     */
//    private void userNextMove(int indexPosition) {
//        String gridMoveButtonText = gridMoveButton[indexPosition].getText();
//        if (gridMoveButtonText.equals(" ")) {
//            gridMoveButton[indexPosition].setText("O");
//            playUsingAI();
//        }
//    }
//
//    /**
//     * This method is used for playing next move by machine itself.
//     */
//    private void playUsingAI() {
//        Board currentBoard = getCurrentStateOfBoard();
//        Board nextMove = null;
//        boolean gameFinish = false;
//        int gameState = 0;
//
//        if (isAIFirstPlayer) {
//            ticTacToePlayer.addBoardToList(currentBoard, 2);
//            if (isGameFinish()) {
//                gameFinish = true;
//            } else {
//                nextMove = ticTacToePlayer.getNextBestMove(currentBoard, 1);
//                gameState = ticTacToePlayer.getGameDecision(nextMove);
//            }
//        } else {
//            ticTacToePlayer.addBoardToList(currentBoard, 1);
//            if (isGameFinish()) {
//                gameFinish = true;
//            } else {
//                nextMove = ticTacToePlayer.getNextBestMove(currentBoard, 2);
//                gameState = ticTacToePlayer.getGameDecision(nextMove);
//            }
//        }
//        if (gameFinish) {
//            updateInformation();
//        } else {
//            if (nextMove != null) {
//                updateMoveOnBoard(nextMove);
//            }
//            if (gameState != 0) {
//                if (isAIFirstPlayer) {
//                    if (gameState == 1) {
//                        xWon++;
//                    } else if (gameState == 2) {
//                        oWon++;
//                    } else {
//                        draw++;
//                    }
//                } else {
//                    if (gameState == 1) {
//                        oWon++;
//                    } else if (gameState == 2) {
//                        xWon++;
//                    } else {
//                        draw++;
//                    }
//                }
//                updateInformation();
//            }
//        }
//    }
//
//    /**
//     * Updates statisitical information about each user in terms of how many games both user won or lost and drawn also.
//     */
//    private void updateInformation() {
//        String updateInformation = playerInformation + "    X:" + String.valueOf(xWon) + "    O:" + String.valueOf(oWon) + "    Draw:" + String.valueOf(draw);
//        infoLabel.setText(updateInformation);
//        changeGridButtonAccessibility(false);
//    }
//
//    /**
//     * checks is game is finished. It checks it from TicTacToePlayer object.
//     */
//    private boolean isGameFinish() {
//        int result = ticTacToePlayer.getGameDecision(getCurrentStateOfBoard());
//        if (result != 0) {
//            if (result == 1) {
//                if (isAIFirstPlayer) {
//                    xWon++;
//                } else {
//                    oWon++;
//                }
//            } else if (result == 2) {
//                if (!isAIFirstPlayer) {
//                    xWon++;
//                } else {
//                    oWon++;
//                }
//            } else {
//                draw++;
//            }
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * This is Action listener for move buttons
//     */
//    private class MyActionListener implements ActionListener {
//
//        private int index;
//
//        public MyActionListener(int index) {
//            this.index = index;
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            userNextMove(index);
//        }
//    }
//}
