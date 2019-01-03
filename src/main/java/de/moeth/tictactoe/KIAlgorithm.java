package de.moeth.tictactoe;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface KIAlgorithm {

    INDArray getReward(INDArray board);

    void changeValue(INDArray state, int action, double reward);

    void storeData();
}
